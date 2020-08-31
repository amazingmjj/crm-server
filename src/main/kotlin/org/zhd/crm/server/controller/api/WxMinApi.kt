package org.zhd.crm.server.controller.api

import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.zhd.crm.server.controller.CrmBaseController

@RestController
@RequestMapping("api/v1/wxmini")
class WxMinApi : CrmBaseController() {
    @ApiOperation(value = "获取客户列表", notes = "")
    @ApiImplicitParams(
            ApiImplicitParam(name = "name", value = "客户名称", required = false),
            ApiImplicitParam(name = "currentPage", value = "当前页数(默认0)", required = false, defaultValue = "0"),
            ApiImplicitParam(name = "pageSize", value = "每页条数(默认10)", required = false, defaultValue = "10"),
            ApiImplicitParam (name = "type", value = "查询类型(0 全部 1 型云客户)", required = false, defaultValue = "0")
    )
    @GetMapping("customer")
    fun customerList(): Map<String, Any> {
        var currentPage = if (request.getParameter("currentPage") == null) 0 else request.getParameter("currentPage").toInt()
        if (currentPage < 0) currentPage = 0
        var pageSize = if (request.getParameter("pageSize") == null) 10 else request.getParameter("pageSize").toInt()
        var type = if (request.getParameter("type") == null) 0 else request.getParameter("type").toInt()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = customerService.findCstmListInWxMini(request.getParameter("name"), pgRequest, type)
        val result = HashMap<String, Any>()
        result["returncode"] = "0"
        result["list"] = pg.content
        result["total"] = pg.totalElements
        return result
    }
}