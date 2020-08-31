package org.zhd.crm.server.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.zhd.crm.server.util.CommUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.UndeclaredThrowableException
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@ControllerAdvice
class ExceptController {
    private val log = LoggerFactory.getLogger(ExceptController::class.java)

    @Autowired
    private lateinit var commUtil: CommUtil

    @ExceptionHandler(value = Exception::class)
    @Transactional(rollbackOn = arrayOf(Exception::class, RuntimeException::class))
    @ResponseBody
    fun exceptionHandler(req: HttpServletRequest, e: Exception): Map<String, Any> {
        log.error("request method:>>>${req.method}")
        log.error("catch exception:>>>", e)
        log.error("error toString:>>>${e.toString()}")
        var sw = StringWriter()
        val pw = PrintWriter(sw)
        try {
            pw.print(e.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            sw.close()
            pw.close()
            if (sw.toString().length == 0) sw.append("网络异常")
        }
        val result = HashMap<String, Any>()
        result.put("returnCode", -1)
        val urlArray = arrayOf("/login", "/file", "/basicData", "/setting", "/customerManage", "/callCenter", "/mobile", "/salesManage")
        val url = req.requestURI
        log.info(">>>异常路径为:$url >>>异常为:${sw.toString()}")
        urlArray.map { s ->
            if (url.indexOf(s) >= 0) {
                if (sw.toString().indexOf("DataIntegrityViolationException") > 0) result.put("errMsg", "违背数据完整性约束,请联系管理员")
                else if (sw.toString().indexOf("DataAccessResourceFailureException") > 0) result.put("errMsg", "访问资源失败,请联系管理员")
                else if (sw.toString().indexOf("TypemismatchDataAccessException") > 0) result.put("errMsg", "数据类型不匹配,请联系管理员")
                else if (sw.toString().indexOf("InvalidDataAccessResourceUsageException") > 0) result.put("errMsg", "错误使用数据访问资源,请联系管理员")
                else if (sw.toString().indexOf("SQLException") > 0) result.put("errMsg", "SQL异常,请联系管理员")
                else if (sw.toString().indexOf("IllegalAccessException") > 0) result.put("errMsg", "非法访问,请联系管理员")
                else if (sw.toString().indexOf("IndexOutOfBoundsException") > 0) result.put("errMsg", "数组越界,请联系管理员")
                else if (sw.toString().indexOf("ClassNotFoundException") > 0) result.put("errMsg", "找不到类,请联系管理员")
                else if (sw.toString().indexOf("ArithmeticException") > 0) result.put("errMsg", "数学运算错误,请联系管理员")
                else if (sw.toString().indexOf("ClassCastException") > 0) result.put("errMsg", "类型强转失败,请联系管理员")
                else if (sw.toString().indexOf("NoSuchMethodException") > 0) result.put("errMsg", "方法未找到,请联系管理员")
                else if (sw.toString().indexOf("NumberFormatException") > 0) result.put("errMsg", "转换数字类型失败,请联系管理员")
                else if (sw.toString().indexOf("IOExeption") > 0) result.put("errMsg", "IO流异常,请联系管理员")
                else if (sw.toString().indexOf("UndeclaredThrowableException") > 0) {
                    val undeclaerdErr = e as UndeclaredThrowableException
                    result.put("errMsg", undeclaerdErr.undeclaredThrowable.message!!)
                }
                else if (sw.toString().indexOf("org.springframework.validation") >= 0) {
                    val bindEx = e as BindException
                    result.put("errMsg", e.allErrors[0].defaultMessage)
                } else result.put("errMsg", sw.toString())
                log.info(">>>异常提示为:${result["errMsg"]}")
                commUtil.errLogSave("${sw.toString()}|$url")
            }
        }
        return result
    }
}