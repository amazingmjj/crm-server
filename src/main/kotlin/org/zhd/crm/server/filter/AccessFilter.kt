package org.zhd.crm.server.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

//@Component
// FIXME 暂时保留
open class AccessFilter : Filter {
    private val log = LoggerFactory.getLogger(AccessFilter::class.java)

    override fun init(filterConfig: FilterConfig) {
    }

    override fun destroy() {
    }

    @Throws(Exception::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req: HttpServletRequest = request as HttpServletRequest
        val startTime = System.currentTimeMillis()
        if (shouldPrintLog(req.requestURI)) {
            val params: Map<String, Array<String>> = req.parameterMap
            var strBuff = StringBuffer().append("[${req.method}]\nParameters:\n ${req.method.capitalize()}, ${req.requestURI}")
            for (key in params.keys) {
                strBuff.append("\t$key = ${req.getParameter(key)}")
            }
            log.info(strBuff.toString())
        }
        val diffTime = System.currentTimeMillis() - startTime
        log.info("request spend time >> $diffTime ms")
        chain.doFilter(request, response);
    }

    //FIXME 正则表达式
//	private val x = "/^/"

    private fun shouldPrintLog(url: String): Boolean {
        arrayOf("/js", "/img", "/css", "/lib", "/favicon.ico").map { item ->
            if (url.startsWith(item)) return false
        }
        return true
    }
}