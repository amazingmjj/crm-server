package org.zhd.crm.server.service

import com.alibaba.fastjson.JSON
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import org.zhd.crm.server.GlobalConfig
import org.zhd.crm.server.model.crm.MqData
import org.zhd.crm.server.repository.crm.MqDataRepository
import org.zhd.crm.server.service.crm.CallCenterService
import org.zhd.crm.server.service.crm.CustomerService
import org.zhd.crm.server.service.crm.SettingService
import org.zhd.crm.server.util.CommUtil

@Service
class ActiveMqRecieverService {
    private val log = LoggerFactory.getLogger(ActiveMqRecieverService::class.java)
    @Value("\${spring.profiles.active}")
    private var currentProfile = ""

    @Autowired
    private lateinit var custService: CustomerService
    @Autowired
    private lateinit var setService: SettingService
    @Autowired
    private lateinit var callService: CallCenterService
    @Autowired
    private lateinit var mqDataRepo: MqDataRepository
    @Autowired
    private lateinit var commUtil: CommUtil

    @JmsListener(destination = GlobalConfig.Activemq.MQ_SCP2CRM_NAME)
    fun scpInfoQueue(resp: Map<String, String>) {
        val mqType = resp["mq_type"]!!
        val destination = GlobalConfig.Activemq.MQ_SCP2CRM_NAME
        log.info(">>>receive msg>>>destination:$destination>>>mq_type:$mqType")
        val obj = saveMqData(resp as Map<String, String>, destination, if (mqType == null) -1 else mqType!!.toInt())
        try {
            when (mqType) {
                // 保存微信关注客户信息
                "1" -> custService.cstmWxLinker(resp["params"]!!, resp["name"]!!, resp["openId"]!!, resp["appName"]!!, resp["appKey"]!!, resp["avatar"]!!, resp["subscribe"]!!)
                // 更新客户取关状态
                "2" -> custService.cstmWxLinkerUnsubscribe(resp["openId"]!!)
                else -> {
                    log.info("invalid mq type:>>$mqType")
                }
            }
        } catch (e: Exception) {
            log.error("队列:${destination}处理失败,mqType为：$mqType,异常：${e.printStackTrace()}")
            commUtil.errLogSave("队列:${destination}处理失败,mqType为：$mqType,异常：${e.message}")
            updateMqData(obj.id!!, "队列处理失败")
        }
    }

    @JmsListener(destination = GlobalConfig.Activemq.MQ_XY2CRM_NAME)
    fun customChangeQueue(resp: Map<String, String>) {
        val mqType = resp["mq_type"]
        val destination = GlobalConfig.Activemq.MQ_XY2CRM_NAME
        log.info(">>>receive msg>>>destination:$destination>>>mq_type:$mqType")
        //保存队列信息
        val type = if (mqType == "1") 1 else if (mqType == "2") 4 else if (mqType == "3") 2 else if (mqType == "4") 3 else 0
        val obj = saveMqData(resp as Map<String, String>, destination, type)
        try {
            when (mqType) {
                "1" -> custService.behaviorRecordSave(resp)//已废弃，使用db link
                "2" -> custService.xyCstmOrderSave(resp)
                "3" -> custService.statOrderSave(resp)
                "4" -> custService.xyCstmUpdate(resp)
                else -> {
                }
            }
        } catch (e: Exception) {
            log.error("队列:${destination}处理失败,mqType为：$mqType,异常：${e.printStackTrace()}")
            commUtil.errLogSave("队列:${destination}处理失败,mqType为：$mqType,异常：${e.message}")
            updateMqData(obj.id!!, "队列处理失败")
        }
    }

    @JmsListener(destination = GlobalConfig.Activemq.MQ_ERP2CRM_NAME)
    fun erpChangeQueue(resp: Map<String, Any>) {
        val mqType = resp["mq_type"]
        val destination = GlobalConfig.Activemq.MQ_ERP2CRM_NAME
        log.info(">>>receive msg>>>destination:$destination>>>mq_type:$mqType")
        //保存队列信息
        val type = if (mqType == "1") 0 else if (mqType == "2") 5 else if (mqType == "3") 6 else if (mqType == "4") 7 else if (mqType == "6") 9 else if (mqType == "7") 10 else 0
        val obj = saveMqData(resp as Map<String, String>, destination, type)
        try {
            when (mqType) {
//                "1" -> custService.erpGoodSaleSave(resp)//已废弃
                "2" -> setService.erpDptSave(resp)
                "3" -> setService.erpOrgSave(resp)
                "4" -> setService.erpAcctSave(resp)
                "6" -> custService.erpCodeSave(resp)
                "7" -> custService.erpCstmOrderSave(resp)
                "8" -> custService.erpCstFirstOrder(resp)
                else -> {
                }
            }
        } catch (e: Exception) {
            log.error("队列:${destination}处理失败,mqType为：$mqType,异常：${e.printStackTrace()}")
            commUtil.errLogSave("队列:${destination}处理失败,mqType为：$mqType,异常：${e.message}")
            updateMqData(obj.id!!, "队列处理失败")
        }
    }

    @JmsListener(destination = GlobalConfig.Activemq.MQ_CRM_DELAY_NAME)
    fun crmDelayQueue(resp: Map<String, String>) {
        val mqType = resp["mq_type"]
        val destination = GlobalConfig.Activemq.MQ_CRM_DELAY_NAME
        log.info(">>>receive delayMsg>>>destination:$destination>>>mq_type:$mqType")
        val type = if (mqType == "0") 11 else if (mqType == "1") 12 else if (mqType == "2") 13 else 0
        saveMqData(resp, destination, type)
        when (mqType) {
            "0" -> custService.msgSaveAndSend(resp["call_id"]!!.toLong())
            "1" -> custService.msgSend2Person(resp["msg_id"]!!.toLong())
            "2" -> callService.handleSmsDelay(resp)
            else -> {
            }
        }
    }

    // 消息队列接受短信
    @JmsListener(destination = GlobalConfig.Activemq.MQ_SMS_MESSAGR_NAME, containerFactory = "jmsListenerContainerTopic")
    fun msgReceiveTopic(resp: Map<String, String>) {
        // 具体返回信息查看wiki
        val type = resp["type"]!!
        val destination = GlobalConfig.Activemq.MQ_SMS_MESSAGR_NAME
        val mqType = if (type == "1") 14 else 15
        log.info(">>>receive message notify>>>destination:$destination>>>mq_type:$mqType")
        saveMqData(resp, destination, mqType)
        if (type == "1") callService.smsReceiveCreate(resp) else callService.handleSmsResp(resp)
    }

    private fun saveMqData(resp: Map<String, String>, destination: String, type: Int): MqData {
        val jsonStr = JSON.toJSONString(resp)
        log.info(">>>msg data:$jsonStr")
        val mq = MqData()//接收
        mq.destination = destination
        mq.mqType = type
        mq.content = jsonStr

        var mqName = ""
        when (type) {
            0 -> mqName = "未知"
            1 -> mqName = "xy用户行为同步crm"
            3 -> mqName = "xy用户订单情况同步crm"
            4 -> mqName = "xy审核用户同步crm"
            2 -> mqName = "xy下单成功同步crm"
            5 -> mqName = "erp部门同步crm"
            6 -> mqName = "erp机构同步crm"
            7 -> mqName = "erp业务员同步crm"
            9 -> mqName = "erpCode反馈同步crm"
            10 -> mqName = "erp下单成功同步crm"
            11 -> mqName = "单个拜访延时同步"
            12 -> mqName = "所有拜访延时同步"
            13 -> mqName = "短信延时同步"
            14 -> mqName = "客户短信回复"
            15 -> mqName = "短信消息回执"
        }
        mq.mqName = mqName

        val obj = mqDataRepo.save(mq)
        log.info(">>>mq data save successed")
        return obj
    }

    private fun updateMqData(id: Long, remark: String) {
        val mqData = mqDataRepo.findOne(id)
        mqData.status = 1
        mqData.remark = remark
        mqDataRepo.save(mqData)
    }
}