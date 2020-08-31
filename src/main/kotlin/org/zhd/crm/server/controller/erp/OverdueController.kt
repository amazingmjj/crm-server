package org.zhd.crm.server.controller.erp

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.service.erp.FeeItemService
import org.zhd.crm.server.service.statistic.OverdueService

/**
 * 超期未提
 */
@RestController
@RequestMapping("overdue")
class OverdueController : CrmBaseController(){
    @Autowired
    lateinit var overdueService: OverdueService;

    @Autowired
    lateinit var feeItemService: FeeItemService;

    /**
     * 超期未提list
     */
    @PostMapping("overdueReport")
    fun findOverdueReport(currentPage:Int,pageSize: Int,uid:Long,startTime:String?,
                          endTime:String?,source:Int?,billCode:String?,customer:String?,
                          empCode:String?,dptName:String?,goodsFlag:Int?): Map<String, Any>{
        val result = HashMap<String, Any>()
        val feePrice = feeItemService.findFeeItemByName("超期仓储费","仓储费")

        val sourceStr = when(source){
            0 -> "ERP"
            1 -> "型云"
            else -> null
        }

        val goodsFlagStr = when(goodsFlag){
            0 -> "未完成"
            1 -> "已完成"
            else -> null
        }

        val page = overdueService.overdueReportNative(uid,startTime,endTime,sourceStr,
                billCode,customer,empCode,dptName,goodsFlagStr,currentPage,pageSize)

        result["feePrice"] = feePrice.feeInPrice
        result["list"] = page["list"] as List<Any>
        result["total"] = page["total"] as Any
        result["returnCode"] = 0
        return result
    }

    /**
     * 处理超期未提
     * @param uid
     * @param billCode 单据号
     * @param dealType 0免收 1待收 2删除
     * @param overdueMoney 超期金额
     * @param remark
     */
    @PostMapping("dealOverdueReport")
    fun dealOverdueReport(uid:Long,billCode: String,dealType:Int,overdueMoney:Double?,remark:String?) : Map<String,Any>{
        val result = HashMap<String, Any>()
        val insertList=overdueService.insertUnDeliveryHistory(uid, billCode, dealType, overdueMoney, remark)
        if (1==dealType){
            //同一billCode下所需信息相同
            overdueService.syncUnDelivery2Erp(insertList)
        }
        result["returnCode"] = 0
        return result
    }

    /**
     * 查询历史
     */
    @PostMapping("findOverdueDealHistory")
    fun findOverdueDealHistory(currentPage:Int,pageSize: Int,uid:Long,startTime:String?,
                               endTime:String?,source:Int?,billCode:String?,customer:String?,
                               empCode:String?,dptName:String?,goodsFlag:Int?,dealTypeStr: String?): Map<String, Any>{
        val result = HashMap<String, Any>()

        val sourceStr = when(source){
            0 -> "ERP"
            1 -> "型云"
            else -> null
        }

        val goodsFlagStr = when(goodsFlag){
            0 -> "未完成"
            1 -> "已完成"
            else -> null
        }

        val startTimeStr = when{
            StringUtils.isNotEmpty(startTime) -> "$startTime-01"
            else -> null
        }

        val endTimeStr = when{
            StringUtils.isNotEmpty(endTime) -> "$endTime-31"
            else -> null
        }

        val pgRequest = PageRequest(currentPage, pageSize)
        val pg = overdueService.overdueDealHistory(uid,startTimeStr,endTimeStr,sourceStr,billCode,
                        customer,empCode,dptName,goodsFlagStr,dealTypeStr,pgRequest)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0

        return result
    }


}