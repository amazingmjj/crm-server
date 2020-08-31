package org.zhd.crm.server.service

import com.alibaba.fastjson.JSON
import org.apache.activemq.ScheduledMessage
import org.apache.activemq.command.ActiveMQQueue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jms.core.JmsMessagingTemplate
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.MqData
import org.zhd.crm.server.repository.crm.MqDataRepository
import javax.jms.Message
import javax.jms.MessageProducer
import javax.jms.Queue
import javax.jms.Session

@Service
class ActiveMqSenderService {

    private val log = LoggerFactory.getLogger(ActiveMqSenderService::class.java)

    @Autowired(required = false)
    private lateinit var jmsMessagingTemplate: JmsMessagingTemplate

    @Autowired
    private lateinit var erpCustomChangeQueue: Queue
    @Autowired
    private lateinit var xyCustomChangeQueue: Queue
    @Autowired
    private lateinit var crmDelayQueue: Queue
    @Autowired
    private lateinit var mqDataRepo: MqDataRepository

    @Value("\${spring.profiles.active}")
    private var currentProfile = ""

    @Throws(Exception::class)
    fun sendMsg(msgMap: Map<String, String>, type: String?) {
        val c = jmsMessagingTemplate.connectionFactory.createConnection()
        val s = c.createSession(false, Session.AUTO_ACKNOWLEDGE)
        try {
            var producer: MessageProducer? = null
            var message: Message? = null
            var destination = ""
            if (type == "1") {//通知erp
                val map = msgMap as HashMap<String, String>
                message = s.createObjectMessage(map)
                producer = s.createProducer(erpCustomChangeQueue)
                destination = erpCustomChangeQueue.queueName
            } else if (type == "2") {//通知型云
                message = s.createMapMessage()
                msgMap.keys.map { key ->
                    message.setString(key, msgMap.get(key))
                }
                producer = s.createProducer(xyCustomChangeQueue)
                destination = xyCustomChangeQueue.queueName
            }
            //保存队列信息
            val mqType = when(msgMap["mq_type"]){
                "4" -> 3
                "5" -> 8
                "8" -> 16
                "9" -> 17
                else -> (msgMap["mq_type"] ?: error("mq_type为空")).toInt()
            }
            saveMqData(msgMap, destination, mqType)
            producer?.send(message)
            log.info(">>>send Msg>>>destination:${producer!!.destination}>>>msg:$message")
        } catch (e: Exception) {
            log.error("mq sender error", e)
        } finally {
            s?.close()
            c?.close()
        }
    }

    @Throws(Exception::class)
    fun sendDelayMsg(msgMap: Map<String, String>, delayTime: Long) {
        val c = jmsMessagingTemplate.connectionFactory.createConnection()
        val s = c.createSession(false, Session.AUTO_ACKNOWLEDGE)
        try {
            var producer: MessageProducer? = null
            var message: Message? = null
            var destination = ""
            val mqType = msgMap["mq_type"]
            if (mqType != "-1") {//客户拜访延时通知 0 1,短信延时发送 2
                message = s.createMapMessage()
                msgMap.keys.map { key ->
                    message.setString(key, msgMap.get(key))
                }
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayTime)
                producer = s.createProducer(crmDelayQueue)
                destination = crmDelayQueue.queueName
            }
            //保存队列信息
            val type = if (mqType == "0") 11 else if (mqType == "1") 12 else if (mqType == "2") 13 else 0
            saveMqData(msgMap, destination, type)
            log.info(">>>send delayMsg>>>destination:${producer!!.destination}>>>msg:$message")
            producer.send(message)
        } catch (e: Exception) {
            log.error("mq sender error", e)
        } finally {
            if (s != null) s.close()
            if (c != null) c.close()
        }
    }

    @Throws(Exception::class)
    fun reSendMsg(msgMap: HashMap<String, String>, mqType: Int, mqId: Long, destName: String) {
        val c = jmsMessagingTemplate.connectionFactory.createConnection()
        val s = c.createSession(false, Session.AUTO_ACKNOWLEDGE)
        try {
            var producer: MessageProducer? = null
            var message: Message? = null
            val destination: Queue = ActiveMQQueue(destName)
            if (mqType == 8||mqType == 16||mqType == 17) {//crm客户同步erp
                message = s.createObjectMessage(msgMap)
                producer = s.createProducer(destination)
            } else if (mqType == 3) {//crm客户同步xy
                message = s.createMapMessage()
                msgMap.keys.map { key ->
                    message.setString(key, msgMap[key])
                }
                producer = s.createProducer(destination)
            }
            //更新重发次数
            updateMqData(mqId)
            log.info(">>>reSendMsg start>>>destination:${producer!!.destination}>>>msg:$message")
            if (producer != null) producer.send(message)
        } catch (e: Exception) {
            log.error("mq sender error", e)
        } finally {
            if (s != null) s.close()
            if (c != null) c.close()
        }
    }

    private fun saveMqData(resp: Map<String, String>, destination: String, type: Int) {
        val jsonStr = JSON.toJSONString(resp)
        log.info(">>>msg data:$jsonStr")
        val mq = MqData()
        mq.msgType = 1
        mq.destination = destination
        mq.mqType = type
        mq.content = jsonStr

        var mqName = ""
        when (type) {
            0 -> mqName = "未知"
            3 -> mqName = "crm客户同步xy"
            8 -> mqName = "crm客户同步erp"
            11 -> mqName = "单个拜访延时同步"
            12 -> mqName = "所有拜访延时同步"
            13 -> mqName = "短信延时同步"
            16 -> mqName = "客户合并"
            17 -> mqName = "处理超期未提同步erp"
        }
        mq.mqName = mqName

        mqDataRepo.save(mq)
        log.info(">>>mq data save successed")
    }

    private fun updateMqData(id: Long) {
        val mqData = mqDataRepo.findOne(id)
        mqData.dealNum += 1
        mqDataRepo.save(mqData)
    }
}