package org.zhd.crm.server.service

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.zhd.crm.server.dubbo.CustomerDubboService
import org.zhd.crm.server.model.crm.CustomerCall
import org.zhd.crm.server.model.crm.CustomerRecord
import org.zhd.crm.server.model.crm.Message
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.service.crm.CustomerService
import org.zhd.crm.server.service.crm.SalesManageService
import org.zhd.crm.server.service.crm.SettingService
import org.zhd.crm.server.service.statistic.GradingService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Component
@Profile("dev", "stage", "prod")
class ScheduleService {
    @Autowired
    private lateinit var customerRepo: CustomerRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository
    @Autowired
    private lateinit var cstmRedPepo: CustomerRecordRepository
    @Autowired
    private lateinit var cstmCallPepo: CustomerCallRepository
    @Autowired
    private lateinit var gradingService: GradingService
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var cstmMdyPepo: CustomerModifyRepository
    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService
    @Autowired
    private lateinit var msgRepo: MessageRepository
    @Autowired
    private lateinit var mobInfoRepo: MobileInfoRepository
    @Autowired
    private lateinit var settingService: SettingService
    @Autowired
    private lateinit var salesManageService: SalesManageService
    @Autowired
    private lateinit var customerDubboService: CustomerDubboService
    @Autowired
    private lateinit var customerService: CustomerService

    private val log = LoggerFactory.getLogger(ScheduleService::class.java)

	//正式客户自动流失
	@Scheduled(cron = "0 0 2 * * ?")
	fun cstmLost(){
		log.info(">>>>cstmLost start>>>>${Date()}")
		//获取正式客户的时间,不包括lockStatus = 1和 billDate为null的
        val day = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-90))
        log.info(">>>90天前的时间为：$day")
        val list = customerRepo.findCstmList(day)
        log.info(">>>90天未开单的客户数：${list.size}")
        if (list.isNotEmpty()){
            list.map { s ->
                //变为公共客户
                val originObj = customerRepo.findOne(s.id)
                originObj.mark = 3
                originObj.transType = 0
                originObj.convertDate = Timestamp(Date().time)//流失时间
                customerRepo.save(originObj)
                //保存到流失表
                val record = CustomerRecord()
                record.fkCustom = originObj
                record.type = 3
                record.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)//超级管理员
                record.reason = "自动流失"
                val obj = cstmRedPepo.save(record)
                log.info(">>>>cstmLost save successed>>>>${obj.id}")
            }
        }
	}

    //客户拜访自动超时
    @Scheduled(cron = "0 0 2 * * ?")
    fun cstmCallTimeout(){
        log.info(">>>>cstmCallTimeout start>>>>${Date()}")
        //查询昨天的拜访中记录
        cstmCallPepo.findUnderwayAll().map { s ->
            s.status = 2
            s.clockTime = s.planVisitTime//超时的打卡时间和计划时间一致
            cstmCallPepo.save(s)
        }
    }

    //客户首字母批量更新
    @Scheduled(cron = "0 0 2 * * ?")
    fun batchUpdateCstmInitial(){
        log.info(">>>>batchUpdateCstmInitial start>>>>${Date()}")
        //查询没有首字母的客户
        val list = customerRepo.findCstmInitial()
        log.info(">>>No initials count:${list.size}")
        list.map { s ->
            log.info(">>>handle cstm:${s.compName}")
            val initial = commUtil.getFirstSpell(s.compName)
            s.compNameInitial = initial
            customerRepo.save(s)
        }
        //查询昨天修改名称的客户
        val startTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-1))
        val endTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(0))
        log.info(">>>startTime:$startTime,>>>endTime:$endTime")
        cstmMdyPepo.findCstmMdy(startTime, endTime).map { s ->
            val obj = customerRepo.findOne(s.customer.id)
            if (obj != null){
                log.info(">>>handle cstm:${obj.compName}")
                val initial = commUtil.getFirstSpell(obj.compName)
                obj.compNameInitial = initial
                customerRepo.save(obj)
            }
        }
        log.info(">>>batch update compNameInitial successed")
    }

    // 每天1点执行客户分级的三方存储过程
    @Scheduled(cron = "0 0 1 * * *")
    fun cstmGradePro() {
        gradingService.platformProcedure()
    }

    // 每天2点执行客户分级明细数据同步以及客户评分
    @Scheduled(cron = "0 0 2 * * *")
    fun cstmGradeDataAndScore() {
        gradingService.batchSaveCstmClassify()
    }

    // 每天2点保存今日拜访信息到手机消息表
    @Scheduled(cron = "0 0 2 * * *")
    fun handleMobileMessage(){
        //所有业务员的今日拜访信息
        val callList = cstmCallPepo.findCallForMobile(0,commUtil.getHourStart(0),commUtil.getHourEnd(23))
        //按照业务员id分类
        val callMap: Map<Long, List<CustomerCall>> = callList.stream().collect(Collectors.groupingBy { s -> s.creator.id })
        //处理每个业务员的消息
        callMap.keys.map { key ->
            val list = callMap[key]
            val json = JSONArray()
            list!!.map { s ->
                val jo = JSONObject()
                jo.put("callId",s.id)
                jo.put("cstmName",s.customer.compName)
                json.add(jo)
                // 单条拜访延时发送
                val sendMap = HashMap<String, String>()
                val diff = commUtil.getMinute(s.planVisitTime,-30).time - Date().time
                sendMap["mq_type"] = "0"
                sendMap["call_id"] = s.id.toString()
                mqSenderService.sendDelayMsg(sendMap, diff)
            }
            val jsonStr = json.toString()
            val msgInfo = "您今日有${list.size}个客户需要拜访，点击查看今日待拜访"
            log.info(">>>用户$key：$msgInfo")
            //所有今日拜访保存到消息表
            val msg = Message()
            //type = 0 contentType = 0 status = 0
            msg.content = jsonStr
            msg.acctCode = key.toString()
            msg.msgInfo = msgInfo
            val obj = msgRepo.save(msg)
            //所有今日拜访延时到8点发送
            val sendMap = HashMap<String, String>()
            val diff = commUtil.getHourStart(8).time - Date().time
            sendMap["mq_type"] = "1"
            sendMap["msg_id"] = obj.id.toString()
            mqSenderService.sendDelayMsg(sendMap, diff)
        }
    }

    // 每周一2点删除超过3个月未使用的设备信息
    @Scheduled(cron = "0 0 2 ? * MON")
    fun deleteMobileInfo(){
        val day = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-90))
        log.info(">>>90天前的时间为：$day")
        val list = mobInfoRepo.findAll(day)
        log.info(">>>90天未使用的设备数：${list.size}")
        list.map { s ->
            mobInfoRepo.delete(s.id)
        }
    }

    // 每隔一分钟获取新浪期货数据 螺纹钢 热卷 铁矿 焦炭
//    @Scheduled(cron = "0 0/1 9-12,13-15,21-23 * * ?")
    fun getSinaData(){
        val dictList = settingService.findSinaDict()
        if (dictList.isNotEmpty()) {
            dictList.map { dict ->
                log.info(">>>开始处理${dict.name}")
                salesManageService.saveSinaData(dict.value)
            }
        } else {
            log.info(">>>找不到物资代码字典")
        }
    }

    //每天一点半更新新老客户状态
    @Scheduled(cron = "0 30 1 * * *")
    fun updateCstType(){
        val newCstList = customerRepo.findNewCstList()
        if (newCstList.isNotEmpty()){
            /*var cstStr = ""
            newCstList.map { item->
                if (StringUtils.isNotEmpty(item.erpCode)){
                    cstStr += when{
                        StringUtils.isEmpty(cstStr)-> item.erpCode
                        else-> ","+item.erpCode
                    }
                }
            }
            //找到未开单的客户
            val neverDeliverCst = customerDubboService.findCstNeverDeliver(cstStr)*/
            newCstList.map { item ->
                //未开单客户依然当做新客户
                if (null!=item.firstBillDate){
                    //满足周期365天则转化
                    if (null==item.startTime
                            ||(null==item.billDate&&(Date().time-item.startTime!!.time)>365*24*3600*1000L)
                            ||(null!=item.billDate&&(item.billDate!!.time-item.startTime!!.time)>365*24*3600*1000L)){
                        log.info(">>>开始新老客户转化${item.compName}")
                        item.cstmType = 1
                        //同步erp
                        customerService.crmSync2XyOrErp(item)
                        customerRepo.save(item)
                    }
                }
            }
        }
    }
}