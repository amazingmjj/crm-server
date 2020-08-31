package org.zhd.crm.server.service.crm

import com.alibaba.fastjson.JSON
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.service.ActiveMqSenderService
import org.zhd.crm.server.service.MsgService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import kotlin.collections.ArrayList


@Service
@Transactional
class CallCenterService {
    @Autowired
    private lateinit var linkRepo: LinkerRepository
    @Autowired
    private lateinit var templateRepo: SmsTemplateRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository
    @Autowired
    private lateinit var smsRepo: SmsRepository
    @Autowired
    private lateinit var smsStatRepo: SmsStatisticRepository
    @Autowired
    private lateinit var outLinkRepo: OutLinkerRepository
    @Autowired
    private lateinit var smsReceiveRepo: SmsReceiveRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var msgService: MsgService
    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService
    @PersistenceContext
    private lateinit var entityManager: EntityManager //实体管理对象

    private val log = LoggerFactory.getLogger(CallCenterService::class.java)

    //新增或修改短信模板
    fun templateCreate(template: SmsTemplate): SmsTemplate {
        var obj = template
        if (obj.id != null) {
            val originObj = templateRepo.findOne(obj.id)
            obj = commUtil.autoSetClass(template, originObj, arrayOf("id", "createAt", "updateAt")) as SmsTemplate
        }
        return templateRepo.save(obj)
    }

    //查重
    fun checkTemplate(tmp: SmsTemplate): String {
        var msg = ""
        if (tmp.groupName.isNullOrBlank()) msg = "分组名称不能为空"
        else if (tmp.name.isNullOrBlank()) msg = "模板名称不能为空"
        else {
            val count = templateRepo.countTemplate(tmp.groupName!!, tmp.name!!)
            if (tmp.id != null) {//修改
                val originObj = templateRepo.findOne(tmp.id)
                if (!(originObj.groupName == tmp.groupName && originObj.name == tmp.name) && count != 0) {//有变化
                    msg = "分组下的存在该模板名称"
                }
            } else {//新增
                if (count != 0) msg = "分组下的存在该模板名称"
            }
        }
        return msg
    }

    //删除短信模板
    fun templateDelete(id: Long) = templateRepo.delete(id)

    //下拉查询模板所有分组
    fun queryGroupName() = templateRepo.queryGroupName()

    //分页查询短信模板
    fun findTemplateAll(groupName: String?, name: String?, content: String?, pageable: Pageable) = templateRepo.findTemplateAll(groupName, name, content, pageable)

    //发送短信同时创建发送记录
    fun smsCreate(mobile: String, content: String, delayTime: String?, uid: String, mark: String): ArrayList<String> {
        val list = ArrayList<String>()
        var msg = ""
        //去重
        val mobileArr = mobile.split(",")
        val originSize = mobileArr.size
        val phoneSet = HashSet<String>(mobileArr)
        val currentSize = phoneSet.size
        val mobileStr = phoneSet.joinToString(",")
        var numMsg = ""
        if (originSize != currentSize) numMsg = (originSize - currentSize).toString()
        log.info(">>>mobileStr length is ${mobileStr.length},originSize is $originSize,currentSize is $currentSize")
        //处理数据
        val sendCount = phoneSet.size
        val time = if (delayTime == null) null else Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm").parse(delayTime).time)
        log.info(">>>delayTime is $time")
        //创建统计记录
        val smsStat = SmsStatistic()
        smsStat.content = content
        smsStat.creator = acctRepo.findOne(uid.toLong())
        smsStat.mobileArray = if (mobileStr.length > 1000) mobileStr.substring(0,996) else mobileStr
        smsStat.type = if (delayTime == null) 1 else 2
        smsStat.status = if (delayTime == null) 1 else 4
        smsStat.sendType = if (mark == "0") 1 else 2
        smsStat.delayTime = time
        smsStat.sendCount = sendCount
        val stat = smsStatRepo.save(smsStat)
        log.info(">>>create smsStatistic successed,${stat.failureNum}")
        //发送
        if (delayTime != null) {//延时发送
            handleSmsDelay(mobileStr, content, uid, stat, delayTime, mark)
        } else {
            msg = handleSmsCreate(mobileStr, content, uid, stat, mark)
        }
        list.add(msg)
        list.add(numMsg)
        return list
    }

    fun handleSmsDelay(mobileStr: String, content: String, uid: String, stat: SmsStatistic, delayTime: String, mark: String) {
        val diff = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(delayTime).time - Date().time
        log.info(">>>>SmsDelay delayTime is:$delayTime,currentTime is:${Date()},diff is:${diff / (1000 * 60)}分钟")
        //先创建详情
        val phoneArr = mobileStr.split(",")
        phoneArr.map { phone ->
            if (mark == "0") {//内部联系人
                val list = linkRepo.findListByPhone(phone)
                if (list.isNotEmpty()) {
                    list.map { s ->
                        log.info(">>>>创建内部联系人sms,phone:$phone,linkId:${s.id}")
                        smsCreate(null, phone, s.name, 3, content, 1, s.id, uid.toLong(), stat)
                    }
                } else {
                    log.info(">>>>内部联系人中不存在该号码[$phone]")
                }
            } else {//外部联系人
                val obj = outLinkRepo.findByPhone(phone)
                if (obj != null) {
                    log.info(">>>>创建外部联系人sms,phone:$phone")
                    smsCreate(null, phone, obj.name!!, 3, content, 2, obj.id, uid.toLong(), stat)
                } else {//号码可能是一次性的
                    smsCreate(null, phone, "未知", 3, content, 2, null, uid.toLong(), stat)
                }
            }
        }
        val map = HashMap<String, String>()
        map["mobile"] = mobileStr
        map["content"] = content
        map["parentId"] = stat.id.toString()
        map["uid"] = uid
        map["mq_type"] = "2"
        mqSenderService.sendDelayMsg(map, diff)
        log.info(">>>send sms delay msg")
    }

    fun handleSmsCreate(mobileStr: String, content: String, uid: String, stat: SmsStatistic, mark: String): String {
        var msg = ""
        val jsonStr = msgService.sendMsg(mobileStr, content, null)
        log.info(">>>SmsCreate sendMsg resp:$jsonStr")
        val sendMap = JSON.parseObject(jsonStr, HashMap::class.java) as HashMap<String, String>
        //返回预生成的短信id,逗号分隔,与手机号一一对应
        val code = sendMap["code"]!!
        if (code == "-1") {
            msg = "创建发送记录失败,原因是:${sendMap["msg"]!!}"
        } else {
            val idsArr = sendMap["ids"]!!.split(",")
            val phoneArr = mobileStr.split(",")
            log.info(">>>phoneArr size:${phoneArr.size}")
            if (idsArr.size == phoneArr.size) {
                (0..(idsArr.size - 1)).map { idx ->
                    if (mark == "0") {//内部联系人
                        val list = linkRepo.findListByPhone(phoneArr[idx])
                        if (list.isNotEmpty()) {
                            list.map { s ->
                                log.info(">>>>创建内部联系人sms,msgId:${idsArr[idx]},phone:${phoneArr[idx]},linkId:${s.id}")
                                smsCreate(idsArr[idx].toLong(), phoneArr[idx], s.name, 1, content, 1, s.id, uid.toLong(), stat)
                            }
                        } else {
                            log.info(">>>>内部联系人中不存在该号码[${phoneArr[idx]}]")
                        }
                    } else {//外部联系人
                        val obj = outLinkRepo.findByPhone(phoneArr[idx])
                        if (obj != null) {
                            log.info(">>>>创建外部联系人sms,msgId:${idsArr[idx]},phone:${phoneArr[idx]}")
                            smsCreate(idsArr[idx].toLong(), phoneArr[idx], obj.name!!, 1, content, 2, obj.id, uid.toLong(), stat)
                        } else {//号码可能是一次性的
                            smsCreate(idsArr[idx].toLong(), phoneArr[idx], "未知", 1, content, 2, null, uid.toLong(), stat)
                        }
                    }
                }
                log.info(">>>>send all sms msg")
            } else {
                msg = "创建发送记录失败,原因是:预生成的短信id数和手机号数目不匹配"
                updateSmsStat(stat.id!!, phoneArr)
            }
        }
        return msg
    }

    private fun smsCreate(msgId: Long?, phone: String, name: String, status: Int, content: String, sendType: Int, refId: Long?, uid: Long, stat: SmsStatistic) {
        val sms = Sms()
        sms.msgId = msgId
        sms.phone = phone
        sms.name = name
        sms.status = status
        sms.content = content
        sms.sendType = sendType
        sms.refId = refId
        sms.creator = acctRepo.findOne(uid)
        sms.parent = stat
        smsRepo.save(sms)
    }

    //处理延时发送队列
    fun handleSmsDelay(map: Map<String, String>) {
        val smsId = map["parentId"]!!.toLong()
        val originObj = smsStatRepo.findOne(smsId)
        if (originObj.status == 5) {
            log.info(">>>>短信延时已取消,发送统计表数据id为:$smsId")
        } else {
            val mobileStr = map["mobile"]!!
            val content = map["content"]!!
            val uid = map["uid"]!!
            log.info(">>>>短信延时发送开始")
            val jsonStr = msgService.sendMsg(mobileStr, content, null)
            log.info(">>>sendMsg resp:$jsonStr")
            val sendMap = JSON.parseObject(jsonStr, HashMap::class.java) as HashMap<String, String>
            //返回预生成的短信id,逗号分隔,与手机号一一对应
            val code = sendMap["code"]!!
            if (code == "-1") {
                commUtil.errLogSave("延时创建发送记录失败,原因是:${sendMap["msg"]!!}")
            } else {
                val idsArr = sendMap["ids"]!!.split(",")
                val phoneArr = mobileStr.split(",")
                if (code == "0" && idsArr.size == phoneArr.size) {
                    (0..(idsArr.size - 1)).map { idx ->
                        log.info(">>>smsId:$smsId,phone:${phoneArr[idx]}")
                        val smsList = smsRepo.findListByIdAndPhone(smsId, phoneArr[idx])
                        if (smsList.isNotEmpty()) {
                            smsList.map { s ->
                                s.msgId = idsArr[idx].toLong()
                                s.status = 1
                            }
                        }
                    }
                    log.info(">>>>send all sms msg")
                } else {
                    commUtil.errLogSave("延时创建发送记录失败,原因是:预生成的短信id数和手机号数目不匹配")
                    updateSmsStat(smsId, phoneArr)
                }
            }
        }
    }

    //更改统计表状态
    fun updateSmsStat(smsId: Long, phoneArr: List<String>) {
        val origin = smsStatRepo.findOne(smsId)
        origin.status = 3
        origin.failureNum = phoneArr.size
        smsStatRepo.save(origin)
    }

    //取消定时发送
    fun cancelSms(id: Long) {
        //更新统计表状态
        val smsStat = smsStatRepo.findOne(id)
        smsStat.status = 5
        smsStatRepo.save(smsStat)
        //更新详情表状态
        val list = smsRepo.findByParent(id)
        log.info(">>>smsList size is ${list.size}")
        list.map { sms ->
            sms.status = 4
            smsRepo.save(sms)
        }
    }

    //处理客户回复
    fun smsReceiveCreate(resp: Map<String, String>) {
        val receiveId = resp["id"]!!
        val phone = resp["phone"]!!
        val content = resp["content"]!!
        val receiveTime = resp["received_time"]!!.toLong()
        log.info(">>>客户回复>>>短信id:$receiveId,手机号:$phone,回复内容:$content,回复时间:${Timestamp(receiveTime)}")
        val list = linkRepo.findViewByPhone(phone)
        val outLink = outLinkRepo.findByPhone(phone)
        if (list.isNotEmpty()) {
            log.info(">>>获取到${list.size}条内部联系人相关数据")
            list.map { link ->
                log.info(">>>内部联系人：${JSON.toJSONString(link)}")
                receiveCreate(link.getCompName(), link.getAcctName(), link.getDptName(), phone, link.getLinkName(), content, receiveId, receiveTime, 1)
            }
        } else if (outLink != null) {
            log.info(">>>外部联系人：${JSON.toJSONString(outLink)}")
            receiveCreate(null, null, null, phone, outLink.name!!, content, receiveId, receiveTime, 2)
        } else {
            log.info(">>>$phone 不存在")
            receiveCreate(null, null, null, phone, "陌生人", content, receiveId, receiveTime, 3)
        }
    }

    private fun receiveCreate(cstmName: String?, acctName: String?, dptName: String?, phone: String, name: String, content: String, msgId: String, receiveTime: Long, type: Int) {
        val receive = SmsReceive()
        receive.cstmName = cstmName
        receive.acctName = acctName
        receive.dptName = dptName
        receive.phone = phone
        receive.name = name
        receive.content = content
        receive.msgId = msgId.toLong()
        receive.type = type
        receive.replyTime = Timestamp(receiveTime)
        smsReceiveRepo.save(receive)
        log.info(">>>smsReceive create successed")
    }

    //处理消息回执
    fun handleSmsResp(resp: Map<String, String>) {
        val smsId = resp["id"]!!.toLong()
        val status = resp["status"]!!
        val originStatus = resp["status_original"]!!
        val obj = smsRepo.findByMsgId(smsId)
        if (obj != null) {//找不到的是型云的干扰数据
            log.info(">>>消息回执>>>短信id:$smsId,短信状态:$status,原始状态:$originStatus")
            obj.status = if (status == "成功") 0 else 2
            smsRepo.save(obj)
            val smsStat = smsStatRepo.findOne(obj.parent.id)
            log.info(">>>原失败个数：${smsStat.failureNum}")
            smsStat.status = if (status == "成功") 2 else 3
            if (status != "成功") {
                smsStat.failureNum += 1
                log.info(">>>现失败个数：${smsStat.failureNum}")
            }
            smsStatRepo.save(smsStat)
            log.info(">>>sms status update successed")
        }
    }

    //分页查询发送详情
    fun findSmsAll(parentId: Long, status: String?, cstmName: String?, name: String?, phone: String?, msgId: String?, mark: String, pageable: Pageable): Page<Any> {
        if (mark == "0") return smsRepo.findInnerSms(parentId, status, cstmName, name, phone, msgId, pageable)
        else return smsRepo.findOutSms(parentId, status, name, phone, msgId, pageable)
    }

    //分页查询发送统计信息
    fun findSmsStatAll(id: String?, acctName: String?, type: String?, status: String?, cstmName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, mark: String, pageable: Pageable): Page<Any> {
        if (mark == "0") return smsStatRepo.findInnerSmsStat(id, acctName, type, status, cstmName, name, phone, startTime, endTime, content, pageable)
        else return smsStatRepo.findOutSmsStat(id, acctName, type, status, name, phone, startTime, endTime, content, pageable)
    }

    //分页查询接收信息
    fun findSmsReceiveAll(msgId: String?, acctName: String?, dptName: String?, cstmName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, mark: String, pageable: Pageable): Page<SmsReceive> {
        if (mark == "0") return smsReceiveRepo.findInnerAll(msgId, acctName, dptName, cstmName, name, phone, startTime, endTime, content, pageable)
        else return smsReceiveRepo.findOutAll(msgId, acctName, dptName, cstmName, name, phone, startTime, endTime, content, pageable)
    }

    //查重
    fun checkOutLink(ol: OutLinker): String {
        var msg = ""
        if (ol.name.isNullOrBlank()) msg = "名称不能为空"
        else if (ol.phone.isBlank()) msg = "电话号码不能为空"
        else {
            val count = outLinkRepo.countPhone(ol.phone)
            if (ol.id != null) {//修改
                val originObj = outLinkRepo.findOne(ol.id)
                if (originObj.phone != ol.phone && count != 0) {//有变化
                    msg = "存在该号码，请确认"
                }
            } else {//新增
                if (count != 0) msg = "存在该号码，请确认"
            }
        }
        return msg
    }

    //保存或修改外部联系人
    fun outLinkSave(outLink: OutLinker, uid: Long): OutLinker {
        var obj = outLink
        if (obj.id == null) {
            obj.creator = acctRepo.findOne(uid)
        } else {
            val originObj = outLinkRepo.findOne(obj.id)
            obj = commUtil.autoSetClass(outLink, originObj, arrayOf("id", "createAt", "updateAt", "creator")) as OutLinker
        }
        return outLinkRepo.save(obj)
    }

    fun outLinkDel(id: Long) = outLinkRepo.delete(id)

    //分页查询外部联系人
    fun outLinker(name: String?, phone: String?, label: String?, remark: String?, pageable: Pageable) = outLinkRepo.outLinker(name, phone, label, remark, pageable)

    //查询所有外部联系人
    fun outLinkerAll(name: String?, phone: String?, label: String?, remark: String?) = outLinkRepo.outLinkerAll(name, phone, label, remark)

    //查询所有联系人id
    fun outLinkerIds(name: String?, phone: String?, label: String?, remark: String?): Map<String, Any> {
        val result = HashMap<String, Any>()
        val list = outLinkerAll(name, phone, label, remark)
        val ids = list.map { s -> s.id }.joinToString(",")
        log.info(">>>ids:$ids,size:${list.size}")
        result["returnCode"] = 0
        result["ids"] = ids
        result["total"] = list.size
        return result
    }

    //批量删除外部联系人
    fun outLkBatchDel(ids: String) {
        ids.split(",").map { id ->
            outLinkDel(id.toLong())
        }
    }

    //批量更新标签
    fun outLkBatchUpdate(ids: String, label: String) {
        ids.split(",").map { id ->
            val lk = outLinkRepo.findOne(id.toLong())
            val str = StringBuffer()
            lk.label = str.append(lk.label).append(",$label").toString()
            log.info(">>>label:${lk.label}")
            outLinkRepo.save(lk)
        }
    }

    //poi导出excel
    fun exportOutLinker(resp: HttpServletResponse, excelName: String) {
        log.info(">>>start export")
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("通讯录")
        val linkerList = outLinkRepo.outLinkerAll(null, null, null, null)
        log.info(">>>linkerList size:${linkerList.size}")
        var rowNum = 1

        val headers = arrayOf("姓名", "手机号", "标签", "备注")
        val row = sheet.createRow(0)
        headers.indices.map { i ->
            val cell = row.createCell(i)
            val text = HSSFRichTextString(headers[i])
            cell.setCellValue(text)
        }
        linkerList.map { link ->
            val row = sheet.createRow(rowNum)
            row.createCell(0).setCellValue(link.name)
            row.createCell(1).setCellValue(link.phone)
            row.createCell(2).setCellValue(link.label)
            row.createCell(3).setCellValue(link.remark)
            rowNum++
        }
        resp.setContentType("application/octet-stream")//告诉浏览器输出内容为流
        resp.setHeader("Content-disposition", "attachment;filename=$excelName")
        try {
            val out = resp.outputStream
            workbook.write(out)
            out.flush()
            out.close()
            log.info(">>>end export")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //entityManager的原生查询统一写在最后
    fun findCstmLinker(listOrPage: Int, xyMark: String?, uid: String, linkName: String?, linkPhone: String?, mainStatus: String?, compName: String?, region: String?, dptName: String?, acctName: String?, busiName: String?, proName: String?, summaryLevel: String?, billType: String?, mark: String?, result: HashMap<String, Any>, currentPage: String?, pageSize: String?): HashMap<String, Any> {
        //预处理
        val dataLevel = commUtil.getDataLevel(uid.toLong())
        val time = getStartAndEndTime(billType)
        //主体
        val mainStr = StringBuffer()
        if (linkName != null) mainStr.append(" and a.link_name like '%$linkName%'")
        if (linkPhone != null) mainStr.append(" and a.link_phone like '%$linkPhone%'")
        if (mainStatus != null) mainStr.append(" and a.main_status = '${mainStatus.toInt()}'")
        if (compName != null) mainStr.append(" and a.comp_name like '%$compName%'")
        if (region != null) mainStr.append(" and a.region like '%$region%'")
        if (dptName != null) mainStr.append(" and a.dpt_name like '%$dptName%'")
        if (acctName != null) mainStr.append(" and a.acct_name like '%$acctName%'")
        if (busiName != null) mainStr.append(" and a.busi_name = '$busiName'")
        if (proName != null) mainStr.append(" and a.pro_name = '$proName'")
        if (summaryLevel != null) mainStr.append(" and a.summary_level = '$summaryLevel'")
        if (time["startTime"] != null) mainStr.append(" and to_char(a.bill_date,'yyyy-MM-dd') >= '${time["startTime"]}'")
        if (time["endTime"] != null) mainStr.append(" and to_char(a.bill_date,'yyyy-MM-dd') < '${time["endTime"]}'")
        if (mark != null) mainStr.append(" and a.mark = '${mark.toInt()}'")
        if (dataLevel["acctId"] != null) mainStr.append(" and a.acct_id = '${dataLevel["acctId"]!!.toLong()}'")
        if (dataLevel["dptId"] != null) mainStr.append(" and a.dpt_id = '${dataLevel["dptId"]!!.toLong()}'")
        if (dataLevel["orgId"] != null) mainStr.append(" and a.org_id = '${dataLevel["orgId"]!!.toLong()}'")
        if (xyMark == null) {} else if (xyMark == "1") mainStr.append(" and a.erp_code is not null and a.xy_code is not null") else mainStr.append(" and a.erp_code is not null and a.xy_code is null")
        val selectStr = "select a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.main_status from v_cstm_linker_list a"
        val groupStr = " group by a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.create_at,a.main_status"
        val queryStr = if (mainStr.isBlank()) "$selectStr$groupStr" else "$selectStr where${mainStr.toString().substring(4)}$groupStr"
        val sortStr = " order by a.create_at desc"
        if (listOrPage == 0) {//筛选后返回所有客户联系人信息
            //原生查询
            log.info("querySql>>>$queryStr")
            val query = this.entityManager.createNativeQuery(queryStr)
            val queryList = query.resultList as List<Any>
            result["list"] = queryList
            result["total"] = queryList.size
        } else {//分页查询客户联系人信息
            //计数
            val countSql = "select count(*) from($queryStr)"
            log.info("countSql>>>$countSql")
            //分页
            val querySql = commUtil.selectPageSql(currentPage!!.toInt(), pageSize!!.toInt(), "$queryStr$sortStr")
            log.info("querySql>>>$querySql")
            //原生查询
            val countQuery = this.entityManager.createNativeQuery(countSql)
            val count = countQuery.resultList
            val total = count[0].toString().toInt()
            val query = this.entityManager.createNativeQuery(querySql)
            val queryList = query.resultList as List<Any>
            result["list"] = queryList
            result["total"] = total
        }
        return result
    }

    private fun getStartAndEndTime(billType: String?): Map<String, String?> {
        log.info(">>>billType:$billType")
        val firstTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-30))//30天前
        val secondTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-60))//60天前
        val thirdTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getDay(-90))//90天前
        //不传表示全部  0  30天内 1  30-60 2  60-90  3  90天以上   start<=date<end
        val startTime = if (billType == "0") firstTime else if (billType == "1") secondTime else if (billType == "2") thirdTime else if (billType == "3") null else null
        val endTime = if (billType == "0") null else if (billType == "1") firstTime else if (billType == "2") secondTime else if (billType == "3") thirdTime else null
        log.info(">>>startTime:$startTime,endTime:$endTime")
        return mapOf("startTime" to startTime, "endTime" to endTime)
    }

    //短信验证码
    fun verifySms(mobile: String, content: String): String {
        var msg = ""
        if (mobile.length > 11) {
            msg = "手机号码错误"
        } else {
            val deadTime = Timestamp(Date().time + 5 * 60 * 1000)
            log.info(">>>$content,$mobile,$deadTime")
            val smsStat = SmsStatistic()
            smsStat.content = content
            smsStat.creator = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
            smsStat.mobileArray = mobile
            smsStat.sendType = 3
            smsStat.deadTime = deadTime
            val stat = smsStatRepo.save(smsStat)
            val jsonStr = msgService.sendMsg(mobile, content, null)
            log.info(">>>verifySms resp:$jsonStr")
            val sendMap = JSON.parseObject(jsonStr, HashMap::class.java) as HashMap<String, String>
            //返回预生成的短信id
            val code = sendMap["code"]!!
            if (code == "-1") {
                msg = "创建短信记录失败,原因是:${sendMap["msg"]!!}"
            } else {
                val idsArr = sendMap["ids"]!!.split(",")
                if (idsArr.size == 1) {
                    log.info(">>>msgId:${idsArr[0]}")
                    smsCreate(idsArr[0].toLong(), mobile, "未知", 1, content, 3, null, CrmConstants.DEFAULT_ACCT_ID, stat)
                } else {
                    msg = "短信个数不对"
                }
            }
        }
        return msg
    }

    //手机号校验,true表示已被注册
    fun checkMobile(mobile: String): Boolean {
        val count = acctRepo.loginAcctCount(mobile)
        return (count > 0)
    }

    //短信校验,true表示校验失败
    fun checkVerifyCode(mobile: String, verifyCode: String): Boolean {
        val startTime = Timestamp(Date().time)
        val endTime = Timestamp(Date().time + 5 * 60 * 1000)
        log.info(">>>mobile:$mobile,verifyCode:$verifyCode,startTime:$startTime,endTime:$endTime")
        val verifyList = smsStatRepo.verifyCodeList(mobile, startTime, endTime)
        //验证码五分钟过期，取最新的
        if (verifyList.isEmpty()) {
            log.info(">>>未找到输入的验证码")
            return true
        } else {
            val verifyContent = verifyList[0]
            log.info(">>>verifyList:${JSON.toJSONString(verifyList)}")
            if (verifyCode != verifyContent) {
                log.info(">>>验证码不一致")
                return true
            }
        }
        return false
    }
}