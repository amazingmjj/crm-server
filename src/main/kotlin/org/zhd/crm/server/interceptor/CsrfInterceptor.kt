package org.zhd.crm.server.interceptor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import org.zhd.crm.server.util.CommUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CsrfInterceptor : HandlerInterceptorAdapter() {
    private val log = LoggerFactory.getLogger(CsrfInterceptor::class.java)

    @Autowired
    private lateinit var commUtil: CommUtil

    override fun preHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?): Boolean {
        val remoteIp = getRemoteIp(request)
        log.info("client remoteIp:>>>$remoteIp")
        val whiteIps = arrayOf("0:0:0:0:0:0:0:1", "192.168.20.149", "192.168.20.99")
        log.info("zhd proxy token:>>${request?.getHeader("zhdcrm-proxy-token")}")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateStr = sdf.format(Date())
//        log.info("dateStr:>>>$dateStr")
        val encryptBkToken = commUtil.sha1("${dateStr}zhdcrm")
        log.info("validate token:>>$encryptBkToken")
        if (!whiteIps.contains(remoteIp)) {//不属于白名单ip需要校验
            if (request?.getHeader("zhdcrm-proxy-token").equals(encryptBkToken)) {
                if (request?.requestURI.equals("/login") ) {
                    if (commUtil.sha1("${dateStr}${request?.getParameter("code")}").equals(request?.getParameter("hashCode"))) {
                        log.info("用户登录验证码校验成功")
                        return true
                    } else {
                        log.error("用户登录非法操作")
                        response!!.writer.print("{\"errMsg\":\"illegal user login\", \"returnCode\": -1}")
                        return false
                    }
                } else {
                    return true
                }
//            } else if (request?.requestURI.equals("some demo url")) {
//                return true
            } else {
                response!!.writer.print("{\"errMsg\":\"ip not in whiteList, invlid request\", \"returnCode\": -1}")
                return false
            }
        } else {
            return true
        }
    }

    override fun postHandle(request: HttpServletRequest?, response: HttpServletResponse?, handler: Any?, modelAndView: ModelAndView?) {
//		modelAndView?.addObject("_base", request!!.contextPath)
        super.postHandle(request, response, handler, modelAndView)
    }

    private fun getRemoteIp(req: HttpServletRequest?): String? {
        var ip: String? = null
        val headKeys = arrayOf("x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "remote")
        for (hkey in headKeys) {
            if ("remote".equals(hkey)) {
                ip = req?.getRemoteAddr()
            } else {
                ip = req?.getHeader(hkey)
                if (ip != null && ip.length > 0) {
                    break
                }
            }
        }
        return ip
    }
}