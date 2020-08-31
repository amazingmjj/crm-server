package org.zhd.crm.server.service

import com.alibaba.fastjson.JSONObject
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.zhd.crm.server.GlobalConfig
import org.zhd.crm.server.model.crm.LogRecord
import org.zhd.crm.server.repository.crm.LogRecordRepository

@Aspect
@Service
class WebAopService {
    @Autowired
    private lateinit var logRecordRepo: LogRecordRepository
    val logger = LoggerFactory.getLogger(WebAopService::class.java)

    private val logThread = ThreadLocal<LogRecord>()

    @Pointcut("execution(public * org.zhd.crm.server.controller..*.*(..))")
    fun webLog() {
    }

    @Before("webLog()")
    fun doBefore(joinPoint: JoinPoint) = beforeAction(joinPoint, "webLog")


    private fun beforeAction(joinPoint: JoinPoint, aopType: String) {
        val attributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = attributes.request
        // 记录内容
        val classMethod = "${joinPoint.target.javaClass.name}.${joinPoint.signature.name}()"
        val uri = request.requestURI
        val reqMethod = request.method
        val ip = GlobalConfig.api.getRemoteIp(request)
        val ipAddr = if (ip == null) "" else ip!!
        val params = if (request.parameterMap.isNotEmpty()) JSONObject.toJSONString(request.parameterMap) else ""
        logger.info("=============================START============================")
        logger.info("REQ CLASS: $classMethod")
        logger.info("URI: ${uri}")
        logger.info("HTTP METHOD: ${reqMethod}")
        logger.info("IP: $ipAddr")
        logger.info("HTTP INPUT: $params")
        val logRecord = LogRecord(classMethod, uri, reqMethod, ipAddr)
        try {
            logRecord.inParams = params
            logRecordRepo.save(logRecord)
            logThread.set(logRecord)
        } catch (e: Exception) {
            logger.error("$aopType aop error", e)
            logRecord.status = -1
            logRecord.errMsg = e.message
            logRecord.description = "occur error"
            logRecordRepo.save(logRecord)
        }
    }

    @AfterReturning(returning = "resp", pointcut = "webLog()")
    fun doAfter(resp: Any) = afterAction(resp)

    private fun afterAction(resp: Any) {
        val logRecord = logThread.get()
        val elapsedTime = System.currentTimeMillis() - logRecord.createAt!!.time
        logRecord.outParams = resp.toString()
        logRecord.status = 0
        logRecord.elapsedTime = elapsedTime
        logRecordRepo.save(logRecord)
        logger.info("OUTPUT: $resp")
        logger.info("ELAPSEDTIME: $elapsedTime")
        logger.info("=============================FINISH============================")
        logThread.remove()
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    fun doAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
        logger.info("exception method: ${joinPoint.target.javaClass.name}.${joinPoint.signature.name}()")
        logger.info("exception code: ${e.javaClass.name}")
        logger.info("exception msg: ${e.message}")
        val logRecord = logThread.get()
        logRecord.errMsg = GlobalConfig.api.getExceptionMsg(e)
        logRecord.status = -1
        logRecord.description = e.javaClass.name
        logRecordRepo.save(logRecord)
        logger.info("=============================FINISH============================")
        logThread.remove()
    }

}