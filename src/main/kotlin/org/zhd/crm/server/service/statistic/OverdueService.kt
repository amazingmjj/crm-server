package org.zhd.crm.server.service.statistic

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.erp.UnDeliveryRecord
import org.zhd.crm.server.model.projection.erp.UndeliveryProjection
import org.zhd.crm.server.model.projection.statistic.OverdueProjection
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.repository.erp.UnDeliveryRepository
import org.zhd.crm.server.repository.statistic.OverdueRepository
import org.zhd.crm.server.service.ActiveMqSenderService
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
@Transactional
class OverdueService {

    @Autowired
    lateinit var overdueRepository: OverdueRepository;

    @Autowired
    lateinit var acctRepo:AccountRepository

    @Autowired
    lateinit var unDeliveryRepository: UnDeliveryRepository

    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService

    @PersistenceContext
    private lateinit var entityManager: EntityManager //实体管理对象


    fun overdueReportNative(uid:Long?,startTime:String?,endTime:String?,sourceStr:String?,billCode:String?,customer:String?,
                      empCode:String?,dptName:String?,goodsFlagStr:String?,currentPage:Int,pageSize: Int): Map<String, Any> {
        val account =  acctRepo.findOne(uid)
        val countStr = "select count(*)"
        val resultStr = "select wsFlag,billcode,datas_customername,linkman,linkmobile,goods_flag,"+
        "case when isckflag=0 then to_char(real_undelivery,'FM999990.999')||'(未提)' else to_char(real_undelivery,'FM999990.999') end as real_undelivery," +
                "start_date,end_date,overdue_date,undelivery_money,employee_name,dept_name "
        var whereStr  =" from V_OVERDUE_UNDELIVERY@CRM_CRMSTAT where 1=1 "
        if (StringUtils.isNotEmpty(startTime)){
            whereStr+= " and start_date >= '$startTime-01'"
        }
        if (StringUtils.isNotEmpty(endTime)){
            whereStr+= " and end_date <= '$endTime-31'"
        }
        if (StringUtils.isNotEmpty(sourceStr)){
            whereStr+= " and wsFlag = '$sourceStr'"
        }
        if (StringUtils.isNotEmpty(billCode)){
            whereStr+= " and billcode like '%$billCode%'"
        }
        if (StringUtils.isNotEmpty(customer)){
            whereStr+= " and datas_customername like '%$customer%'"
        }
        if (StringUtils.isNotEmpty(goodsFlagStr)){
            whereStr+= " and goods_flag = '$goodsFlagStr'"
        }

        when (account?.dataLevel) {
            "业务员" -> {
                if (StringUtils.isNotEmpty(account.platformCode)) {
                    whereStr += " and employee_code = '" + account.platformCode + "'"
                }
            }
            "部门" -> {
                if (StringUtils.isNotEmpty(empCode)) {
                    whereStr += " and employee_code = '$empCode'"
                }
                if (StringUtils.isNotEmpty(account.fkDpt.name)) {
                    whereStr += " and dept_name = '" + account.fkDpt.name + "'"
                }
                if (StringUtils.isNotEmpty(dptName)) {
                    whereStr += " and dept_name = '$dptName'"
                }
            }
            else -> {
                if (StringUtils.isNotEmpty(empCode)) {
                    whereStr += " and employee_code = '$empCode'"
                }
                if (StringUtils.isNotEmpty(dptName)) {
                    whereStr += " and dept_name = '$dptName'"
                }
            }
        }
        var query=this.entityManager.createNativeQuery(resultStr+whereStr)
        query.firstResult = currentPage*pageSize
        query.maxResults = pageSize
        val mainResult = query.resultList as List<Array<Any>>
        //将array封装进bean对象
        var returnList = ArrayList<Map<String,Any>>()
        val resultArray =arrayOf("wsFlag", "billcode", "datasCustomername",
                "linkman", "linkmobile", "goodsFlag", "realUndelivery",
                "startDate", "endDate", "overdueDate", "undeliveryMoney", "employeeName",
                "deptName")

        mainResult.map {item ->
            val thisOverdue = HashMap<String,Any>()
            for (index in resultArray.indices){
                thisOverdue[resultArray[index]]=item[index]
            }
            returnList.add(thisOverdue)
        }
        var ctQuery  = this.entityManager.createNativeQuery(countStr+whereStr)
        val total = ctQuery.singleResult.toString().toInt()
        entityManager.close()

        val resultMap = HashMap<String, Any>()
        resultMap["list"] = returnList
        resultMap["total"] = total
        return resultMap
    }


    fun overdueDealHistory(uid:Long?,startTime:String?,endTime:String?,sourceStr:String?,billCode:String?,customer:String?,
                      empCode:String?,dptName:String?,goodsFlagStr:String?,dealTypeStr: String?,pageable: Pageable): Page<UndeliveryProjection> {
        val account =  acctRepo.findOne(uid)
        return when (account?.dataLevel) {
            "业务员" -> {
                unDeliveryRepository.findUnDeliveryRecord(startTime,endTime,sourceStr,billCode,customer,
                        account.platformCode,null,goodsFlagStr,dealTypeStr,pageable)
            }
            "部门" -> {
                unDeliveryRepository.findUnDeliveryRecord(startTime,endTime,sourceStr,billCode,customer,
                        empCode,account.fkDpt.name,goodsFlagStr,dealTypeStr,pageable)
            }
            else -> {
                unDeliveryRepository.findUnDeliveryRecord(startTime,endTime,sourceStr,billCode,customer,
                        empCode,dptName,goodsFlagStr,dealTypeStr,pageable)
            }
        }
    }

    fun insertUnDeliveryHistory(uid:Long,billCode: String,dealType:Int,overdueMoney:Double?,remark:String?):List<UnDeliveryRecord>{
        val account =  acctRepo.findOne(uid)
        val dataList = overdueRepository.queryByBillCode(billCode)
        val returnList = ArrayList<UnDeliveryRecord>()
        if (dataList.isNotEmpty()){
            dataList.map { overdue->
                val thisRecord = UnDeliveryRecord()
                thisRecord.dataSource= overdue.getWsFlag()
                thisRecord.billcode=overdue.getBillcode()
                thisRecord.datasCustomername=overdue.getDatasCustomername()
                thisRecord.linkman=overdue.getLinkman()
                thisRecord.linkmobile=overdue.getLinkmobile()
                thisRecord.deleveryState=overdue.getGoodsFlag()
                thisRecord.overdueUndelivery=overdue.getRealUndelivery().toDouble()
                thisRecord.isckflag=overdue.getIsckflag().toInt()
                thisRecord.starttime=Timestamp(SimpleDateFormat("yyyy-MM-dd").parse(overdue.getStartDate()).time)
                thisRecord.lifttime=Timestamp(SimpleDateFormat("yyyy-MM-dd").parse(overdue.getEndDate()).time)
                thisRecord.overdueDays=overdue.getOverdueDate().toInt()
                thisRecord.overdueMoney=overdue.getUndeliveryMoney().toDouble()
                if (dealType==1){
                    thisRecord.realOverdueMoney=overdueMoney
                }else{
                    thisRecord.realOverdueMoney=0.00
                }
                thisRecord.employeeCode=overdue.getEmployeeCode()
                thisRecord.employeeName=overdue.getEmployeeName()
                thisRecord.deptName=overdue.getDeptName()
                thisRecord.dealType=when(dealType){
                    0 -> "已免收"
                    1 -> "已收款"
                    else -> "已删除"
                }
                thisRecord.dealDate= Date()
                thisRecord.operatorCode=account.platformCode
                thisRecord.operatorName=account.name
                thisRecord.remark=remark
                unDeliveryRepository.save(thisRecord)
                returnList.add(thisRecord)
            }

        }
        return returnList
    }

    fun syncUnDelivery2Erp(unDeliveryList: List<UnDeliveryRecord>){
        val syncMap = HashMap<String, String>()

        //超期吨位求和
        var sumOverWeight = 0.00
        unDeliveryList.map { item->
            sumOverWeight+=item.overdueUndelivery
        }
        val newRecord = UnDeliveryRecord()
        BeanUtils.copyProperties(unDeliveryList[0],newRecord)
        newRecord.overdueUndelivery=sumOverWeight

        val overdueStr =JSON.toJSON(newRecord).toString()
        syncMap["mq_type"] = "9"
        syncMap["unDeliveryStr"] = overdueStr
        mqSenderService.sendMsg(syncMap, "1")
    }

}