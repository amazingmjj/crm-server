package org.zhd.crm.server.controller.erp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zhd.crm.server.service.erp.CommService
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("erpComm")
class ErpCommController {
    @Autowired
    private lateinit var commService: CommService

    @GetMapping("area")
    fun areaList(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = commService.findByAreaName(req.getParameter("name"))
        return result
    }

    @GetMapping("partsname")
    fun partsnameList(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = commService.findByPartsName(req.getParameter("name"))
        return result
    }
}