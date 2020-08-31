package org.zhd.crm.server.controller.erp

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.statistic.SaleOtherWeight
import org.zhd.crm.server.service.erp.ReportService
import org.zhd.crm.server.service.statistic.SaleOtherWeightService
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

/**
 * Erp报表
 */
@RestController
@RequestMapping("erpReport")
class ReportController(): CrmBaseController() {
    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    lateinit var saleOtherWeightService: SaleOtherWeightService
    /**
     * 业务员提成汇总报表
     * @param ny 年月
     * @param acct crm账号
     */
    @GetMapping("empCommSummary")
    fun empCommSummaryReport(currentPage:Int,pageSize: Int,ny: String?,uid: Long,empCode:String?,dptName:String?): Map<String, Any> {
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = reportService.empCommSummaryReport(pgRequest,nyStart,nyEnd,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }

    @PostMapping("export/empCommSummary")
    fun exportEmpCommSummary(ny: String?,uid: Long,empCode:String?,dptName:String?,columnOrder:String?): Map<String, Any>{
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pg = reportService.exportEmpCommSummaryReport(nyStart,nyEnd,uid,empCode,dptName,columnOrder)
        result["list"] = pg
        result["returnCode"] = 0
        return result
    }

    /**
     * 销售专员员提成汇总报表
     * @param ny 年月
     * @param acct crm账号
     */
    @GetMapping("saleCommSummary")
    fun saleCommSummaryReport(currentPage:Int,pageSize: Int,ny: String?,uid: Long,empCode:String?,dptName:String?): Map<String, Any> {
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = reportService.saleCommSummaryReport(pgRequest,nyStart,nyEnd,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }

    @PostMapping("export/saleCommSummary")
    fun exportSaleColumnSummary(ny: String?,uid: Long,empCode:String?,dptName:String?,columnOrder:String?): Map<String, Any> {
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pg = reportService.exportSaleCommSummaryReport(nyStart,nyEnd,uid,empCode,dptName,columnOrder)
        result["list"] = pg
        return result
    }
    /**
     * 直发调货明细表
     */
    @GetMapping("deliveryDetail")
    fun deliveryDetailReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                             customer:String?,nameType:Int,uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
//        val sort = when (orderType) {
//            0-> Sort.Direction.ASC
//            else -> Sort.Direction.DESC
//        }
//        val sortColumn = when (orderColumn){
//            "直发重量"-> "zf_bweight"
//            "调货重量"-> "dh_bweight"
//            "高卖金额"-> "gm"
//            "提成金额"-> "tcje"
//            else -> "employee_name"
//        }
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = reportService.deliveryDetailReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["summary"] = reportService.deliveryDetailReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }

    /**
     * 直发调货明细表
     */
    @PostMapping("export/deliveryDetail")
    fun exportDeliveryDetailReport(nyStart: String?,nyEnd: String?,customer:String?,nameType:Int,
                             uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pg = reportService.exportDeliveryDetailReport(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg
        result["total"] = pg.size
        result["summary"] = reportService.deliveryDetailReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }


    /**
     * 高卖明细表
     */
    @GetMapping("highSellDetail")
    fun highSellDetailReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                             orderColumn:String,orderType:Int,customer:String?,nameType:Int,
                             uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val sort = when (orderType) {
            0-> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }
        val sortColumn = when (orderColumn){
            "高卖"-> "gm"
            else -> "employee_name"
        }
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }
        val pgRequest = PageRequest(currentPage, pageSize,sort,sortColumn)
        val pg = reportService.highSellDetailReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["summary"] = reportService.highSellDetailReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }

    /**
     * 高卖明细表
     */
    @PostMapping("export/highSellDetail")
    fun exportHighSellDetailReport(nyStart: String?,nyEnd: String?,customer:String?,nameType:Int,
                             uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }
        val pg = reportService.exportHighSellDetailReport(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg
        result["total"] = pg.size
        result["summary"] = reportService.highSellDetailReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }



    /**
     * 新增客户明细表
     */
    @GetMapping("newCustomer")
    fun newCustomerReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                             customer:String?,nameType:Int,uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pgRequest = PageRequest(currentPage, pageSize,Sort.Direction.DESC,"startdate")
        val pg = reportService.newCustomerReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }

    /**
     * 新增客户明细表
     */
    @PostMapping("export/newCustomer")
    fun exportNewCustomerReport(nyStart: String?,nyEnd: String?,
                          customer:String?,nameType:Int,uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pg = reportService.exportNewCustomerReport(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg
        result["total"] = pg.size
        result["returnCode"] = 0
        return result
    }


    /**
     * 销售专员新增客户明细表
     */
    @GetMapping("saleNewCustomer")
    fun saleNewCustomerReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                             orderColumn:String,orderType:Int,customer:String?,nameType:Int,
                             uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val sort = when (orderType) {
            0-> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }
        val sortColumn = when (orderColumn){
            "高卖销量"-> "weight_gm"
            "高卖"-> "money_gm"
            "高卖提成"-> "tc_gm"
            "低卖销量"-> "weight_dm"
            "低卖"-> "money_dm"
            "低卖提成"-> "tc_dm"
            "销量合计"-> "weight_sum"
            "提成合计"-> "tc_sum"
            else -> "employee_name"
        }
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pgRequest = PageRequest(currentPage, pageSize,sort,sortColumn)
        val pg = reportService.saleNewCustomerReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["summary"] = reportService.saleNewCustomerReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }

    /**
     * 导出销售专员新增客户明细表
     */
    @PostMapping("export/saleNewCustomer")
    fun exportSaleNewCustomerReport(nyStart: String?,nyEnd: String?,customer:String?,nameType:Int,
                              uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pg = reportService.exportSaleNewCustomerReport(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg
        result["total"] = pg.size
        result["summary"] = reportService.saleNewCustomerReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }

    /**
     * 业务员新增客户明细表
     */
    @GetMapping("empNewCustomer")
    fun empNewCustomerReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                              orderColumn:String,orderType:Int,customer:String?,nameType:Int,
                              uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val sort = when (orderType) {
            0-> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }
        val sortColumn = when (orderColumn){
            "销量"-> "data_bweight"
            "高卖"-> "gm"
            "高卖提成"-> "gm_tc"
            else -> "employee_name"
        }
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pgRequest = PageRequest(currentPage, pageSize,sort,sortColumn)
        val pg = reportService.empNewCustomerReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["summary"] = reportService.empNewCustomerReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("saveOtherWeight")
    fun saveOtherWeight(saleOtherWeight: SaleOtherWeight,req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        var exec = true
        var msg = ""
        if (null==(saleOtherWeight.empId)){
            msg += "业务员编号为空!"
        }
        if (StringUtils.isEmpty(saleOtherWeight.empName)){
            msg += "业务员名称为空!"
        }
        if (StringUtils.isNotEmpty(msg)){
            exec = false
            result["errMsg"] = msg
        }
        if (exec){
            val newObj = saleOtherWeightService.saveOtherWeight(saleOtherWeight)
            if (newObj.id!! > 0) {
                result["newObj"] = newObj
            } else {
                exec = false
                result["errMsg"] = "保存失败"
            }
        }
        result["returnCode"] = if (exec) 0 else -1
        return result
    }

    @PostMapping("updateOtherWeight")
    fun updateOtherWeight(saleOtherWeight: SaleOtherWeight,req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        var exec = true
        if (null == saleOtherWeight.id){
            exec = false
            result["errMsg"] == "单据不存在!"
        }else{
            val oldSaleOtherWeight = saleOtherWeightService.findById(saleOtherWeight.id!!)
            if (null == oldSaleOtherWeight){
                exec = false
                result["errMsg"] == "单据不存在!"
            }
        }
        if (exec){
            saleOtherWeightService.updateWeight(saleOtherWeight.id!!,saleOtherWeight.weight!!)
        }
        result["returnCode"] = if (exec) 0 else -1
        return result
    }

    @DeleteMapping("deleteOtherWeight/{id}")
    fun deleteOtherWeight(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        saleOtherWeightService.deleteById(id)
        result["returnCode"] = 0
        return result
    }

    @GetMapping("otherWeightList")
    fun findOtherWeightList(currentPage:Int,pageSize:Int,ny: String?,empCode: String?):Map<String,Any>{
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = saleOtherWeightService.findByPage(ny,empCode,pgRequest)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }

    /**
     * 销售绩效提成报表 2020-06开始实施
     */
    @GetMapping("salePerformanceComm")
    fun salePerformanceCommReport(currentPage:Int,pageSize: Int,ny: String?,uid: Long,empCode:String?,dptName:String?): Map<String, Any> {
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = reportService.salePerformanceCommReport(pgRequest,nyStart,nyEnd,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }

    @GetMapping("export/salePerformanceComm")
    fun exportSalePerformanceComm(ny: String?,uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        //默认查询当年数据
        val ca = Calendar.getInstance()
        ca.set(Calendar.MONTH,0)
        val nyStart = when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> SimpleDateFormat("yyyyMM").format(ca.time)
        }
        val nyEnd= when{
            StringUtils.isNotEmpty(ny)-> ny!!.replace("-","")
            else -> ""
        }
        val result = HashMap<String, Any>()
        val pg = reportService.exportSalePerformanceCommReport(nyStart,nyEnd,uid,empCode,dptName)
        result["list"] = pg
        result["returnCode"] = 0
        return result
    }

    /**
     * 销售新增客户明细表
     */
    @PostMapping("salePerformanceNewCustomer")
    fun salePerformanceNewCustomerReport(currentPage:Int,pageSize:Int,nyStart: String?,nyEnd: String?,
                             orderColumn:String,orderType:Int,customer:String?,nameType:Int,
                             uid: Long,empCode:String?,dptName:String?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val sort = when (orderType) {
            0-> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }
        val sortColumn = when (orderColumn){
            "销量"-> "data_bweight"
            "新客户提成"-> "new_tc"
            else -> "employee_name"
        }
        val customerName = when {
            StringUtils.isEmpty(customer) -> null
            nameType==1 -> customer
            else -> "%$customer%"
        }

        //默认查询当年数据
        var nyStartStr = nyStart
        if(StringUtils.isEmpty(nyStart)&&StringUtils.isEmpty(nyEnd)){
            val ca = Calendar.getInstance()
            ca.set(Calendar.MONTH,0)
            nyStartStr = SimpleDateFormat("yyyy-MM").format(ca.time)
        }

        val pgRequest = PageRequest(currentPage, pageSize,sort,sortColumn)
        val pg = reportService.salePerformanceNewCustomerReport(pgRequest,nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["summary"] = reportService.salePerformanceNewCustomerReportSum(nyStartStr,nyEnd,customerName,uid,empCode,dptName)
        result["returnCode"] = 0
        return result
    }
}