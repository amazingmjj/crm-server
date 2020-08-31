package org.zhd.crm.server.service.erp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.projection.statistic.*
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.repository.statistic.*

@Service
class ReportService {
    @Autowired
    private lateinit var empCommSummaryRepo: EmpCommSummaryRepository

    @Autowired
    private lateinit var saleCommSummaryRepo: SaleCommSummaryRepository

    @Autowired
    private lateinit var deliveryDetailRepo: DeliveryDetailRepository

    @Autowired
    private lateinit var highSellDetailRepo: HighSellDetailRepository

    @Autowired
    private lateinit var newCustomerRepo: NewCustomerRepository

    @Autowired
    private lateinit var saleNewCustomerRepo: SaleNewCustomerRepository

    @Autowired
    private lateinit var empNewCustomerRepo: EmpNewCustomerRepository

    @Autowired
    private lateinit var salePerformanceCommRepository: SalePerformanceCommRepository

    @Autowired
    private lateinit var salePerformanceNewCustomerRepository: SalePerformanceNewCustomerRepository

    @Autowired
    private lateinit var acctRepo: AccountRepository

    fun empCommSummaryReport(pageable: Pageable,nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?):Page<EmpCommSummaryProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                empCommSummaryRepo.allQuery(nyStart,nyEnd,account.platformCode,null,pageable)
            }
            "部门" -> {
                empCommSummaryRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,pageable)
            }
            else -> {
                empCommSummaryRepo.allQuery(nyStart,nyEnd,empCode,dptName,pageable)
            }
        }
    }

    fun exportEmpCommSummaryReport(nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?,columnOrder:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                empCommSummaryRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null)
            }
            "部门" -> {
                empCommSummaryRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name)
            }
            else -> {
                empCommSummaryRepo.allQueryExport(nyStart,nyEnd,empCode,dptName)
            }
        }
    }

    fun saleCommSummaryReport(pageable: Pageable,nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?): Page<SaleCommSummaryProjection> {
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                saleCommSummaryRepo.allQuery(nyStart,nyEnd,account.platformCode,null,pageable)
            }
            "部门" -> {
                saleCommSummaryRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,pageable)
            }
            else -> {
                saleCommSummaryRepo.allQuery(nyStart,nyEnd,empCode,dptName,pageable)
            }
        }
    }

    fun exportSaleCommSummaryReport(nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?,columnOrder:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                saleCommSummaryRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null)
            }
            "部门" -> {
                saleCommSummaryRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name)
            }
            else -> {
                saleCommSummaryRepo.allQueryExport(nyStart,nyEnd,empCode,dptName)
            }
        }
    }

    fun deliveryDetailReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<DeliveryDetailProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                deliveryDetailRepo.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                deliveryDetailRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                deliveryDetailRepo.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun deliveryDetailReportSum(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val pg = when (account?.dataLevel) {
            "业务员" -> {
                deliveryDetailRepo.allQuerySum(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                deliveryDetailRepo.allQuerySum(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                deliveryDetailRepo.allQuerySum(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
        val result = HashMap<String, Any>()
        val obj = pg[0]
        result["zfBweightSum"] = obj[0]
        result["dhBweightSum"] = obj[1]
        result["tcjeSum"] = obj[2]
        return result
    }

    fun exportDeliveryDetailReport(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                deliveryDetailRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                deliveryDetailRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                deliveryDetailRepo.allQueryExport(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
    }

    fun highSellDetailReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<HighSellDetailProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                highSellDetailRepo.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                highSellDetailRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                highSellDetailRepo.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun highSellDetailReportSum(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val pg = when (account?.dataLevel) {
            "业务员" -> {
                highSellDetailRepo.allQuerySum(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                highSellDetailRepo.allQuerySum(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                highSellDetailRepo.allQuerySum(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
        val result = HashMap<String, Any>()
        result["gmSum"] = pg
        return result
    }

    fun exportHighSellDetailReport(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                highSellDetailRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                highSellDetailRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                highSellDetailRepo.allQueryExport(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
    }

    fun newCustomerReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<NewCustomerProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                newCustomerRepo.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                newCustomerRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                newCustomerRepo.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun exportNewCustomerReport(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                newCustomerRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                newCustomerRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                newCustomerRepo.allQueryExport(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
    }

    fun saleNewCustomerReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<SaleNewCustomerProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                saleNewCustomerRepo.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                saleNewCustomerRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                saleNewCustomerRepo.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun saleNewCustomerReportSum(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val pg = when (account?.dataLevel) {
            "业务员" -> {
                saleNewCustomerRepo.allQuerySum(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                saleNewCustomerRepo.allQuerySum(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                saleNewCustomerRepo.allQuerySum(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
        val result = HashMap<String, Any>()
        val obj = pg[0]
        result["weightGmSum"] = obj[0]
        result["moneyGmSum"] = obj[1]
        result["tcGmSum"] = obj[2]
        result["weightDmSum"] = obj[3]
        result["moneyDmSum"] = obj[4]
        result["tcDmSum"] = obj[5]
        result["zfBweightSum"] = obj[6]
        result["weightSumSum"] = obj[7]
        result["tcSumSum"] = obj[8]
        return result
    }

    fun exportSaleNewCustomerReport(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                saleNewCustomerRepo.allQueryExport(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                saleNewCustomerRepo.allQueryExport(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                saleNewCustomerRepo.allQueryExport(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
    }

    fun empNewCustomerReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<EmpNewCustomerProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                empNewCustomerRepo.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                empNewCustomerRepo.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                empNewCustomerRepo.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun empNewCustomerReportSum(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val pg = when (account?.dataLevel) {
            "业务员" -> {
                empNewCustomerRepo.allQuerySum(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                empNewCustomerRepo.allQuerySum(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                empNewCustomerRepo.allQuerySum(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
        val result = HashMap<String, Any>()
        val obj = pg[0]
        result["dataBweightSum"] = obj[0]
        result["gmSum"] = obj[1]
        result["gmTcSum"] = obj[2]
        return result
    }


    fun salePerformanceCommReport(pageable: Pageable,nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?):Page<SalePerformanceCommProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                salePerformanceCommRepository.queryAll(nyStart,nyEnd,account.platformCode,null,pageable)
            }
            "部门" -> {
                salePerformanceCommRepository.queryAll(nyStart,nyEnd,empCode,account.fkDpt.name,pageable)
            }
            else -> {
                salePerformanceCommRepository.queryAll(nyStart,nyEnd,empCode,dptName,pageable)
            }
        }
    }

    fun exportSalePerformanceCommReport(nyStart: String?,nyEnd: String?,uid:Long,empCode:String?,dptName:String?):List<Any>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                salePerformanceCommRepository.queryAllExport(nyStart,nyEnd,account.platformCode,null)
            }
            "部门" -> {
                salePerformanceCommRepository.queryAllExport(nyStart,nyEnd,empCode,account.fkDpt.name)
            }
            else -> {
                salePerformanceCommRepository.queryAllExport(nyStart,nyEnd,empCode,dptName)
            }
        }
    }


    fun salePerformanceNewCustomerReport(pageable: Pageable,nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Page<SalePerformanceNewCustomerProjection>{
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                salePerformanceNewCustomerRepository.allQuery(nyStart,nyEnd,account.platformCode,null,customer,pageable)
            }
            "部门" -> {
                salePerformanceNewCustomerRepository.allQuery(nyStart,nyEnd,empCode,account.fkDpt.name,customer,pageable)
            }
            else -> {
                salePerformanceNewCustomerRepository.allQuery(nyStart,nyEnd,empCode,dptName,customer,pageable)
            }
        }
    }

    fun salePerformanceNewCustomerReportSum(nyStart:String?,nyEnd:String?,customer:String?,uid: Long,empCode:String?,dptName:String?):Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val pg = when (account?.dataLevel) {
            "业务员" -> {
                salePerformanceNewCustomerRepository.allQuerySum(nyStart,nyEnd,account.platformCode,null,customer)
            }
            "部门" -> {
                salePerformanceNewCustomerRepository.allQuerySum(nyStart,nyEnd,empCode,account.fkDpt.name,customer)
            }
            else -> {
                salePerformanceNewCustomerRepository.allQuerySum(nyStart,nyEnd,empCode,dptName,customer)
            }
        }
        val result = HashMap<String, Any>()
        val obj = pg[0]
        result["dataBweightSum"] = obj[0]
        result["zfBweightSum"] = obj[1]
        result["newTcSum"] = obj[2]
        return result
    }
}