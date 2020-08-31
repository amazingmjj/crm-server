package org.zhd.crm.server.controller.crm

import com.xyscm.erp.crm.api.dto.GoodStockDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.CustomerPurchaseFrequency
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("salesManage")
class SalesManageController : CrmBaseController() {
    @PostMapping("tracking/alert")
    fun alert(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val sort = if (req.getParameter("sort") == null) "0" else req.getParameter("sort")
        salesManageService.findSalesAlertByPage(currentPage, pageSize, req.getParameter("compName"), req.getParameter("acctName"), req.getParameter("dptName"), req.getParameter("uid"), sort.toInt(), req.getParameter("percent"), req.getParameter("warningType"), result)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("tracking/demissionStatistic")
    fun demissionStatistic(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val sort = if (req.getParameter("sort") == null) "0" else req.getParameter("sort")
        val pg = PageRequest(currentPage, pageSize)
        val page = salesManageService.findDemissionStatList(req.getParameter("compName"), req.getParameter("acctName"), req.getParameter("dptName"), req.getParameter("uid"), pg, sort.toInt())
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("tracking/demission")
    fun demission(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val sort = if (req.getParameter("sort") == null) "0" else req.getParameter("sort")
        val pg = PageRequest(currentPage, pageSize)
        val startTime = req.getParameter("startTime")
        val endTime = req.getParameter("endTime")
        val page = salesManageService.findDemissionList(startTime, endTime, req.getParameter("compName"), req.getParameter("acctName"), req.getParameter("dptName"), pg, sort.toInt())
        salesManageService.handleLineGraph(page.content, result)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("resources/goodStock")
    fun goodStock(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val goodStockDto = GoodStockDto()
        goodStockDto.sortMark = if (req.getParameter("sort").isNullOrBlank()) 0 else req.getParameter("sort").toInt()
        if (!req.getParameter("partsName").isNullOrBlank()) goodStockDto.partsnameName = req.getParameter("partsName")
        if (!req.getParameter("material").isNullOrBlank()) goodStockDto.goodsMaterial = req.getParameter("material")
        if (!req.getParameter("goodsSpec").isNullOrBlank()) goodStockDto.goodsSpec = req.getParameter("goodsSpec")
        if (!req.getParameter("length").isNullOrBlank()) goodStockDto.goodsProperty1 = req.getParameter("length")
        if (!req.getParameter("productArea").isNullOrBlank()) goodStockDto.productareaName = req.getParameter("productArea")
        if (!req.getParameter("warehouseName").isNullOrBlank()) goodStockDto.warehouseName = req.getParameter("warehouseName")
        if (!req.getParameter("toleranceRange").isNullOrBlank()) goodStockDto.goodsProperty5 = req.getParameter("toleranceRange")
        if (!req.getParameter("weightRange").isNullOrBlank()) goodStockDto.goodsProperty4 = req.getParameter("weightRange")
        goodStockDubboService.queryGoodStockList(currentPage, pageSize, goodStockDto, result)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("saleReports/goodSale")
    fun goodSale(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val sort = if (req.getParameter("sort") == null) "0" else req.getParameter("sort")
        salesManageService.findStockSalesList(result, req.getParameter("partsName"), req.getParameter("material"), req.getParameter("goodsSpec"), req.getParameter("length"), req.getParameter("toleranceRange"), req.getParameter("weightRange"), req.getParameter("uid"), sort, currentPage, pageSize)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("saleReports/cstmSale")
    fun cstmSale(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val sort = if (req.getParameter("sort") == null) "0" else req.getParameter("sort")
        val page = salesManageService.findCstmSaleList(result, sort, req.getParameter("uid"), req.getParameter("sumgoodsBatch"), req.getParameter("compName"), req.getParameter("dptName"), req.getParameter("acctName"), currentPage, pageSize)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("sinaFutures")
    fun sinaFutures(type: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val list = salesManageService.findSinaData(type)
        result["list"] = list
        result["total"] = list.size
        result["returnCode"] = 0
        return result
    }

    /**
     * 客户超频购买提醒
     * @author samy
     * @date 2020/06/18
     */
    @PostMapping("customerPurchaseFrequency")
    fun customerPurchaseFrequencyList(req: HttpServletRequest, currentPage: Int, pageSize: Int, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "id")
        val acct = settingService.acctFindById(uid)
        var acctId: String? = null
        var dptId: String? = null
        if (acct.dataLevel == "业务员") acctId = acct.id!!.toString()
        if (acct.dataLevel == "部门") dptId = acct.fkDpt.id!!.toString()
        val pg = customerService.customerPurchaseFrequencyList(req.getParameter("status"), req.getParameter("dptName"), req.getParameter("employeeName"), req.getParameter("compName"), dptId, acctId, req.getParameter("startDate"), req.getParameter("endDate"), pgRequest)
        result["returnCode"] = 0
        result["list"] = pg.content
        result["total"] = pg.totalElements
        return result
    }

    @PostMapping("customerPurchaseFrequency/save")
    fun customerPurchaseFrequencySave(obj: CustomerPurchaseFrequency, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        if (uid != obj.acctId && uid != 1L) {
            result["returnCode"] = -1
            result["errMsg"] = "非法操作"
        } else {
            result["returnCode"] = 0
            obj.id = null
            customerService.customerPurchaseFrequencySave(obj)
        }
        return result
    }
}