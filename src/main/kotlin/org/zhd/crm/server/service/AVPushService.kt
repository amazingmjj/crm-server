package org.zhd.crm.server.service

import com.alibaba.fastjson.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.URL


@Service
class AVPushService {

    private val log = LoggerFactory.getLogger(AVPushService::class.java)

    @Value("\${lc.appid}")
    private var lcId = ""

    @Value("\${lc.appkey}")
    private var lcKey = ""

    @Value("\${lc.pushurl}")
    private var lcPushurl = ""

    @Value("\${lc.and.channel}")
    private var andChannel = ""

    @Value("\${spring.profiles.active}")
    private var currentProfile = ""

    // 消息推送到个人
    @Async
    fun send2Persion(content: String, deviceType: String, deviceToken: String) {
        val jsonObj = JSONObject()
        val alertObj = JSONObject()
        if ("prod".equals(currentProfile)) {
            jsonObj.set("prod", currentProfile)
        } else {
            jsonObj.set("prod", "dev")
        }
        alertObj.set("alert", content)
        val queryObj = JSONObject()
        if ("ios".equals(deviceType)) {
            queryObj.set("deviceToken", deviceToken)
            alertObj.set("badge", "Increment")
        } else {
            queryObj.set("installationId", deviceToken)
        }
        jsonObj.set("data", alertObj)
        jsonObj.set("where", queryObj)
        log.info("push json string: ${jsonObj.toJSONString()}")
        val resp = leanPush(lcPushurl, lcId, lcKey, jsonObj.toJSONString())
        log.info("push resp:>>>${resp}")
        if ("android".equals(deviceType)) {
            alertObj.set("action", andChannel)
            jsonObj.set("data", alertObj)
            log.info("android 特殊通知:>>${jsonObj.toJSONString()}")
            val respAnd = leanPush(lcPushurl, lcId, lcKey, jsonObj.toJSONString())
            log.info("respAnd:>>>>$respAnd")

        }
    }

    // 群发设备
    @Async
    fun send2Device(content: String, deviceType: String) {

    }

    // 群发全部注册用户
    @Async
    fun sendAll(content: String) {

    }

    private fun leanPush(basicUrl: String, appid: String, appkey: String, body: String, charset: String = "utf-8"): String {
        var bufferResult = StringBuffer()
        val conn = URL(basicUrl).openConnection()
        conn.setRequestProperty("accept", "*/*")
        conn.setRequestProperty("connection", "Keep-Alive")
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
        conn.setRequestProperty("X-LC-Id", appid)
        conn.setRequestProperty("X-LC-Key", appkey)
        conn.setRequestProperty("Content-Type", "application/json")
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true)
        conn.setDoInput(true)

        val out = PrintWriter(OutputStreamWriter(conn.outputStream, charset))
        //发送请求
        out.print(body)
        //flush 输出流缓冲
        out.flush()
        var inStream = BufferedReader(InputStreamReader(conn.inputStream, charset))
        inStream.use { r ->
            var temp = r.readLine()
            if (temp != null) bufferResult.append(temp)
        }
        out.close()
        inStream.close()

        return bufferResult.toString()
    }

}