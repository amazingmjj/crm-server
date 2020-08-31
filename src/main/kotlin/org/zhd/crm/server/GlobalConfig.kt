package org.zhd.crm.server

import org.springframework.context.annotation.Profile
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest


open class GlobalConfig {

    @Profile("dev", "stage", "prod")
    object SCP {
        const val PROXY_URL="http://192.168.80.210:9901/"
//        const val PROXY_URL="http://192.168.20.40:9901/"
    }
    object Dubbo {
        const val GROUP_NAME = "Dev-mjj-erp-ldp-soa"
        //Test,Prep,Online
//        const val GROUP_NAME = "Test-erp-ldp-soa"
//        const val GROUP_NAME = "Prep-erp-ldp-soa"
        const val GROUP_VERSION = "1.0.0"
    }

    object Activemq {
        //不同的环境前缀不同，Dev,Test,Prep,Online
        private const val prefix = "Dev-mjj"
//        private const val prefix = "Test"
//        private const val prefix = "Prep"
        //接收
        const val MQ_XY2CRM_NAME = "${prefix}_xy_2_crm_custm_info"
        const val MQ_ERP2CRM_NAME = "${prefix}_erp_2_crm_custm_info"
        const val MQ_SMS_MESSAGR_NAME = "${prefix}_SMS_MESSAGE_NOTIFY"
        const val MQ_SCP2CRM_NAME = "${prefix}_scp_2_crm_info"
        //发送
        const val MQ_CRM2XY_NAME = "${prefix}_crm_2_xy_custm_info"
        const val MQ_CRM2ERP_NAME = "${prefix}_crm_2_erp_custm_info"
        //自产自销
        const val MQ_CRM_DELAY_NAME = "${prefix}_crm_delay_queue"
    }

    object api {
        fun getRemoteIp(req: HttpServletRequest?): String? {
            if (req == null) return null
            var ip: String? = null
            val headKeys = arrayOf("x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "remote")
            for (hkey in headKeys) {
                if ("remote" == hkey) {
                    ip = req.remoteAddr
                } else {
                    ip = req.getHeader(hkey)
                    if (ip != null && ip.isNotEmpty()) {
                        break
                    }
                }
            }
            return ip
        }

        fun getExceptionMsg(e: Throwable): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            sw.close()
            pw.close()
            if (sw.toString().isEmpty()) sw.append("RunTime Exception")
            return sw.toString()
        }
    }
}