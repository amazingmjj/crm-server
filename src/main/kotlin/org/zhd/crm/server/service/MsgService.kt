package org.zhd.crm.server.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder

// 短信服务
@Service
class MsgService {

    @Autowired
    private lateinit var httpService: HttpService

    private val charset = "utf-8"

    @Value("\${message.server.url}")
    private var msgServerUrl = ""

    // 发送短信
    @Throws(Exception::class)
    fun sendMsg(mobile: String, content: String, title: String? = null): String {
        val params = HashMap<String, String>()
        params.put("phone", mobile)
        params.put("content", URLEncoder.encode(content, charset))
        if (title != null) params.put("title", URLEncoder.encode(title, charset))
        params.put("source", "CRM")
        return httpService.sendPostRequest(httpService.getParamStr(params), "$msgServerUrl/sms")
    }
}