package org.zhd.crm.server.service.crm

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.google.common.base.Joiner
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.model.projection.CityProvProjection
import org.zhd.crm.server.model.projection.CustomerEvaluationProjection
import org.zhd.crm.server.model.projection.CustomerPurchaseFrequencyProjection
import org.zhd.crm.server.model.projection.WxMiniCustomer
import org.zhd.crm.server.model.statistic.*
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.repository.erp.WarehouseSbillRepository
import org.zhd.crm.server.repository.statistic.*
import org.zhd.crm.server.service.AVPushService
import org.zhd.crm.server.service.ActiveMqSenderService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.lang.Thread.sleep
import java.lang.reflect.Method
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Service
@Transactional
class CustomerService {
    @Autowired
    private lateinit var customerRepo: CustomerRepository
    @Autowired
    private lateinit var linkerRepo: LinkerRepository
    @Autowired
    private lateinit var cstmRedPepo: CustomerRecordRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var acctRepo: AccountRepository
    @Autowired
    private lateinit var cmutRepo: CommunicateRepository
    @Autowired
    private lateinit var bkIfRepo: BankInfoRepository
    @Autowired
    private lateinit var cstMdRepo: CustomerModifyRepository
    @Autowired
    private lateinit var cstCallRepo: CustomerCallRepository
    @Autowired
    private lateinit var cstOrderRepo: CustomerOrderRepository
    @Autowired
    private lateinit var linkRcdRepo: LinkerRecordRepository
    @Autowired
    private lateinit var linkMdyRepo: LinkerModifyRepository
    @Autowired
    private lateinit var behaviorRecordRepo: BehaviorRecordRepository
    @Autowired
    private lateinit var goodsSaleRepo: GoodsSalesRepository
    @Autowired
    private lateinit var dptRepo: DptRepository
    @Autowired
    private lateinit var busiRelationRepo: BusiRelationRepository
    @Autowired
    private lateinit var customPropertyRepo: CustomPropertyRepository
    @Autowired
    private lateinit var gradeRepo: GradeCoefficientRepository
    @Autowired
    private lateinit var saleGoodsRepo: SalesmanGoodsRepository
    @Autowired
    private lateinit var highSaleRepo: SalesmanHighSellRepository
    @Autowired
    private lateinit var taskRepo: TaskPlanningRepository
    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService
    @Autowired
    private lateinit var msgRepo: MessageRepository
    @Autowired
    private lateinit var gradeSummaryRepo: GradeSummaryRepository
    @Autowired
    private lateinit var busiOpptyRepo: BusiOpptyRepository
    @Autowired
    private lateinit var mobInfoRepo: MobileInfoRepository
    @Autowired
    private lateinit var combRcdRepo: CombineRecordRepository
    @Autowired
    private lateinit var avPushService: AVPushService
    @Autowired
    private lateinit var entityManage: EntityManageService
    @Autowired
    private lateinit var commonCstmLinkerRepo: CommonCstmLinkerRepository
    @Autowired
    private lateinit var wxLinkerRepo: WxLinkerRepository
    @Autowired
    private lateinit var customerWxTickerRepo: CustomerWxTicketRepository
    @Autowired
    private lateinit var customerEvaluationRepo: CustomerEvaluationRepository
    @Autowired
    private lateinit var customerEvaluationDataRepo: CustomerEvaluationDataRepository
    @Autowired
    private lateinit var customerEvaluationRelationRepo: CustomerEvaluationRelationRepository
    @Autowired
    private lateinit var customerAreaRecordRepo: CustomerAreaRecordRepository
    @Autowired
    private lateinit var customerPurchaseFrequencyRepo: CustomerPurchaseFrequencyRepository
    @Autowired
    private lateinit var addressRepository: AddressRepository
    @Autowired
    private lateinit var warehouseSbillRepository: WarehouseSbillRepository

    private val log = LoggerFactory.getLogger(CustomerService::class.java)
    private val shortSdf = SimpleDateFormat("yyyy-MM-dd")
    /**
     * 客户超频购买保存
     * @author samy
     * @date 2020/06/23
     */
    fun customerPurchaseFrequencySave(obj: CustomerPurchaseFrequency) = customerPurchaseFrequencyRepo.save(obj)

    /**
     * 客户超频购买提醒
     * @author samy
     * @date 2020/06/18
     */
    fun customerPurchaseFrequencyList(status: String?, dptName: String?, employeeName: String?, compName: String?, dptId: String?, acctId: String?, startDate: String?, endDate: String?, pageable: Pageable): Page<CustomerPurchaseFrequencyProjection> {
        var sd: String? = startDate
        var ed: String? = endDate
        if (sd != null) sd += " 00:00:00"
        if (ed != null) ed += " 23:59:59"

        return when (status) {
            "1" -> {
                customerPurchaseFrequencyRepo.findAllViewUnRecordByPg(dptName, employeeName, compName, dptId, acctId, sd, ed, pageable)
            }
            "2" -> {
                customerPurchaseFrequencyRepo.findAllViewRecordByPg(dptName, employeeName, compName, dptId, acctId, sd, ed, pageable)
            }
            else -> {
                customerPurchaseFrequencyRepo.findAllViewByPg(dptName, employeeName, compName, dptId, acctId, sd, ed, pageable)
            }
        }
    }

    /**
     * 客户评估列表
     * @author samy
     * @date 2020/05/21
     */
    fun customerEvaluationList(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable) = customerEvaluationRepo.findByPg(compName, linkName, dptName, employeeName, propertyMark, startDate, endDate, mark, acctId, dptId, pageable)

    //    fun customerEvaluationList(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, mark: String?, pageable: Pageable) = customerEvaluationRepo.findByTestPg(compName, linkName, dptName, employeeName, propertyMark, mark, pageable)
//    fun customerEvaluationList(compName: String?, linkName: String?, dptName: String?, employeeName: String?, startDate: Timestamp?, endDate: Timestamp?, pageable: Pageable) = customerEvaluationRepo.findByTestPg(compName, linkName, dptName, employeeName, startDate, endDate, pageable)
//    fun customerEvaluationList(compName: String?, linkName: String?, dptName: String?, employeeName: String?, pageable: Pageable) = customerEvaluationRepo.findByTestPg(compName, linkName, dptName, employeeName, pageable)
    fun customerEvaluationListNeedUpdate(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable) = customerEvaluationRepo.findByNeedUpdatePg(compName, linkName, dptName, employeeName, propertyMark, startDate, endDate, mark, acctId, dptId, pageable)

    fun customerEvaluationListUpdated(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable) = customerEvaluationRepo.findByUpdatedPg(compName, linkName, dptName, employeeName, propertyMark, startDate, endDate, mark, acctId, dptId, pageable)

    fun customerEvaluationObj(cstmId: Long) = customerEvaluationRepo.findByCstmId(cstmId)
    fun customerEvaluationOne(id: Long) = customerEvaluationRepo.findOneCstm(id)
    fun customerEvaluationDataList(pid: Long) = customerEvaluationDataRepo.findByParentId(pid)

    /**
     * 客户评估保存或修改
     * @author samy
     * @date 2020/05/21
     */
    @Throws(Exception::class)
    fun customerEvaluationSaveOrUpdate(customerEvaluation: CustomerEvaluation, dataStr: String? = null) {
        val cstm = customerRepo.findOne(customerEvaluation.cstmId)
        if (cstm == null) {
            throw Exception("客户不存在")
        } else {
            var cstmEval = customerEvaluationRepo.findByCstmId(customerEvaluation.cstmId!!)
            if (cstmEval == null) {
                // 新增
                cstmEval = customerEvaluation
                cstmEval.erpCode = cstm.erpCode
            } else {
                // 修改
                var erpCode = cstmEval.erpCode
                cstmEval = commUtil.autoSetClass(customerEvaluation, cstmEval, arrayOf("id", "createAt", "updateAt", "cstmId", "erpCode")) as CustomerEvaluation
                if (erpCode != cstmEval.erpCode) cstmEval.erpCode = erpCode
            }
            customerEvaluationRepo.save(cstmEval)
            if (dataStr != null) {
                val jsonArray = JSONArray.parseArray(dataStr)
                if (jsonArray.size == 0) {
                    customerEvaluationDataRepo.deleteByParentId(cstmEval.id!!)
                } else {
                    if (jsonArray.size > 5) throw Exception("最多5条记录")
                    val effectId = ArrayList<Long>()
                    for (item in jsonArray) {
                        val obj = item as JSONObject
                        val subId = obj.getLong("id")
                        var temp: CustomerEvaluationData = CustomerEvaluationData()
                        if (subId == null) {
                            // 新增
                            temp.parentId = cstmEval.id
                            temp.supplyName = obj.getString("supplyName")
                            temp.supplyPrefer = obj.getString("supplyPrefer")
                            customerEvaluationDataRepo.save(temp)
                            effectId.add(temp.id!!)
                        } else {
                            temp = customerEvaluationDataRepo.findOne(subId)
                            if (temp == null) {
                                throw Exception("非法评估数据")
                            } else {
                                temp.supplyName = obj.getString("supplyName")
                                temp.supplyPrefer = obj.getString("supplyPrefer")
                            }
                            customerEvaluationDataRepo.save(temp)
                            effectId.add(temp.id!!)
                        }
                    }
                    customerEvaluationDataRepo.deleteIdByIds(effectId, cstmEval.id!!)
                    val subCount = customerEvaluationDataRepo.countByParentId(cstmEval.id!!)
                    if (subCount > 5) {
                        throw Exception("最多5条记录")
                    }
                }
            }
        }
    }

    /**
     * 客户评估年度销量
     * @author samy
     * @date 2020/05/22
     */
    fun customerEvaluationYearSale(erpCode: String, startYear: String, endYear: String) = customerEvaluationRelationRepo.customerYearSale(erpCode, startYear, endYear)

    /**
     * 客户评估开单时间
     * @author samy
     * @date 2020/05/22
     */
    fun customerEvaluationBillTime(erpCode: String) = customerEvaluationRelationRepo.customerBillTime(erpCode)

    /**
     * 客户地区评估列表
     */
    fun customerEvaluationAreaList(areaName: String?, dptName: String?, employeeName: String?, dptId: String?, acctId: String?, pageable: Pageable) = customerAreaRecordRepo.findByPg(areaName, dptName, employeeName, dptId, acctId, pageable)

    /**
     * 客户地区评估保存或修改
     * @author samy
     * @date 2020/05/22
     */
    @Throws(Exception::class)
    fun customerEvaluationAreaSaveOrUpdate(customerAreaRecord: CustomerAreaRecord) {
        if (customerAreaRecord.id == null || customerAreaRecord.id!! == 0L) {
            // 新增
            customerAreaRecord.id = null
            customerAreaRecordRepo.save(customerAreaRecord)
        } else {
            var obj = customerAreaRecordRepo.findOne(customerAreaRecord.id!!)
            obj.deliveryStatusInfo = customerAreaRecord.deliveryStatusInfo
            obj.effectForSale = customerAreaRecord.effectForSale
            obj.saleEvaluationWeight = customerAreaRecord.saleEvaluationWeight
            customerAreaRecordRepo.save(obj)
        }
    }

    /**
     * 客户评估年度品类排名
     * @author samy
     * @date 2020/05/26
     */
    fun customerEvaluationYearGoods(year: String, erpCode: String, pageable: Pageable) = customerEvaluationRelationRepo.customerYearGoods(year, erpCode, pageable)

    /**
     * 当前年客户主要购买物资
     * @author samy
     * @date 2020/06/13
     */
    fun customerEvaluationCurrentYearMainPurchaseGoods(erpCode: String) = customerEvaluationRelationRepo.currentYearMainPurchaseGoods(erpCode)

    /**
     * 根据型云电商编号返回客户对象(仅含客户单位性质相关内容)
     * @author samy
     * @date 2019/10/14
     */
    fun findUnitPropertyInEbusiMemberCode(esCodes: List<String>) = customerRepo.findUnitPropertyInEbusiMemberCode(esCodes)

    /**
     * 返回型云所有白条客户
     * @author samy
     * @date 2019/10/14
     */
    fun findEsIousCstms() = customerRepo.findEsIousCstms()

    /**
     * 同一个客户下手机号不能重复
     * @date 2019-07-11
     * @author samy
     *
     */
    fun findCstmLinkRepeat(phone: String, cstmId: Long) = linkerRepo.findCstmLinkRepeat(phone, cstmId)

    /**
     *  根据客户电商会员编号查询对应联系人列表
     *  @date 2019-07-11
     *  @author samy
     *  @remark 需求来源 型云
     */
    fun findCstmLinkInBusiCode(codes: List<String>) = linkerRepo.findCstmLinkInBusiCode(codes)

    /**
     * 根据客户ERP编号查询对应联系人列表
     * @author samy
     * @date 2019-07-23
     * @remark 需求来源 ERP
     */
    fun findCstmLinkInErpCode(codes: List<String>) = linkerRepo.findCstmLinkInErpCode(codes)


    //下拉查询所有客户
    fun findCustomerAll(compName: String?, uid: Long, pg: Pageable): Page<Customer> {
        val uAcct = acctRepo.findOne(uid)
        if ("业务员".equals(uAcct.dataLevel)) return customerRepo.findComboByAcctId(compName, uAcct.id, pg)
        if ("部门".equals(uAcct.dataLevel)) return customerRepo.findComboByDptId(compName, uAcct.fkDpt.id, pg)
        if ("机构".equals(uAcct.dataLevel)) return customerRepo.findComboyOrgId(compName, uAcct.fkDpt.fkOrg.id, pg)
        return customerRepo.findCombo(compName, pg)
    }

    //下拉查询所有正式和公共客户
    fun findCustomerAll(compName: String?, pg: Pageable) = customerRepo.findAll(compName, pg)

    //下拉查询正式客户所在地区,剔除空格和已使用地区
    fun findRegionList(region: String?, pg: Pageable): Page<String> {
        val exList = ArrayList<String>()
        gradeRepo.findRegion().map { s ->
            s.split(",").map { name ->
                exList.add(name)
            }
        }
        exList.add(" ")
        return customerRepo.findRegionList(region, exList, pg)
    }

    //下拉查询所有品名大类
    fun findSupplyCatalog(): List<String> {
        val exList = gradeRepo.findCategory()
        return goodsSaleRepo.findSupplyCatalog(exList)
    }

    //查询单个客户
    fun findCustomerById(id: Long?) = customerRepo.findOne(id)

    fun findCustomerByCompName(compName: String) = customerRepo.findByCompName(compName)

    // 后结算客户数量
    fun settleCountByCompName(names: List<String>) = customerRepo.settleCountByCompName(names)

    fun findCustomerByEbusiAdminAcctNo(eAcctNo: String) = customerRepo.findByEbusiAdminAcctNo(eAcctNo)

    fun findCstmByErpCode(erpCode: String) = customerRepo.findByErpCode(erpCode)

    fun findCstmByEbusiMemberCode(eMemberCode: String) = customerRepo.findByEbusiMemberCode(eMemberCode)

    //保存客户
    fun customerSave(customer: Customer) = customerRepo.save(customer)

    //潜在客户转为正式客户
    fun customerTransform(cstmId: Long, uid: Long) {
        val originObj = customerRepo.findOne(cstmId)
        originObj.mark = 2 //修改客户标志
        originObj.transType = 2 //标记手动转化
        originObj.convertDate = Timestamp(Date().time)//转化时间
        //更新开单时间,使正式客户的未开单时间显示合理 mjj 2020-04-23
        originObj.billDate = Timestamp(Date().time)
        if (originObj.region.isNullOrBlank() && !originObj.compCity.isNullOrBlank()) originObj.region = commUtil.handleRegion(originObj.compCity!!) //取公司所在市
        originObj.publicCompName = originObj.compName //默认和客户名称一致
        val obj = customerRepo.save(originObj)
        //保存到记录表
        val record = CustomerRecord()
        record.fkCustom = originObj
        record.type = 2
        record.fkAcct = acctRepo.findOne(uid)
        record.reason = "潜在转正式"
        cstmRedPepo.save(record)
        //crm新建正式客户需要同步erp
        // 如果主体客户为空，赋值本身
        if (originObj.mainCstm == null) {
            originObj.mainCstm = originObj
        } else {
            originObj.cstmType = originObj.mainCstm!!.cstmType
            originObj.startTime = originObj.mainCstm!!.startTime
        }
        customerRepo.save(originObj)

        val erpArr = arrayOf("CompName", "CompNameAb", "EbusiMemberCode", "EbusiAdminAcctNo", "MemberCode", "LegalRept", "FaxNum", "Region", "Tfn", "OpenBank", "OpenAcct", "CompAddr", "CompProv", "CompCity", "CompArea", "WorkgroupName")
        val map = handelCstmSyncMap(obj, erpArr, 1)
        map["mq_type"] = "5"
        map["erp_code"] = ""
        map["linkCompCode"] = ""
        map["bill_addr"] =  if (obj.billAddr == null) "" else obj.billAddr!!
        map["id"] = obj.id.toString()
        mqSenderService.sendMsg(map, "1")
    }

    //更新客户
    fun customerUpdate(customer: Customer, uid: Long): Customer {
        val originObj = customerRepo.findOne(customer.id)
        //处理客户更新时的同步修改
        if (originObj.publicCompName == originObj.compName) originObj.publicCompName = customer.compName //统计名称如果和原名称一致，则需要同步修改
        if (originObj.compName != customer.compName) busiOpptyRepo.updateByCstmCode(customer.id.toString(), customer.compName) //如果名称变更则要同步修改商机
        //更新
        val cstmMap: Map<String, String> = mapOf("CompName" to "公司名称", "BusiLicenseCode" to "工商证照编码", "CompAddr" to "公司地址-详情", "CompProv" to "公司地址-省", "CompCity" to "公司地址-市", "CompArea" to "公司地址-区", "Tfn" to "税号", "BillAddr" to "开票地址")
        val excludeArray: Array<String> = arrayOf("id", "updateAt", "linkers", "createAt", "createAcct", "customerType", "convertDate", "compNameInitial", "publicCompName", "createAcct", "billDate", "mainCstm", "firstBillDate", "firstDeliveryDate")
        val newObj = autoSetClass(customer, originObj, excludeArray, cstmMap, uid, "客户更新") as Customer
        // 2020/01/08 添加主体客户
        if (originObj.mainCstm == null || originObj.mainCstm!!.id != customer.mainCstm!!.id) {
            newObj.mainCstm = customer.mainCstm
        }
        if (null != customer.mainCstm) {
            if (customer.mainCstm != customer){
                log.info(">>同步主单位mainCstm:" + customer.mainCstm!!.id + ",名称" + customer.mainCstm!!.compName
                        + ",开始时间" + customer.mainCstm!!.startTime)
                val mainChange = linkMainCstRules(customer.mainCstm!!,newObj)
                if (mainChange) crmSync2XyOrErp(customer.mainCstm!!)
            }else if(StringUtils.isNotEmpty(newObj.erpCode)){
                val lastBillDate = warehouseSbillRepository.findLastBillDate(newObj.erpCode!!)
                newObj.billDate = Timestamp(lastBillDate.time)
                if (Date().time-lastBillDate.time>(90*24*3600*1000L)) newObj.mark = 3 else newObj.mark = 2
            }
        } else {
            log.info(">>无需同步主单位")
        }
        //同步erp或xy
        crmSync2XyOrErp(newObj)
        customerRepo.save(newObj)
        return newObj
    }

    //关联主体规则
    fun linkMainCstRules(mainObj: Customer, childObj: Customer):Boolean{
        var mainChange = false
        //客户性质存在老客户则取老客户
        if (mainObj.cstmType==1&&childObj.cstmType==0){
            childObj.cstmType=1
        }else if (mainObj.cstmType==0&&childObj.cstmType==1){
            mainObj.cstmType=1
            mainChange = true
        }
        //取正式客户
        if (2!= childObj.mark&&2==mainObj.mark){
            childObj.mark = 2
        }else if (2== childObj.mark&&2!=mainObj.mark){
            mainObj.mark = 2
            mainChange = true
        }
        //起始时间取早的
        if (null==mainObj.startTime||
                (null!=childObj.startTime&&mainObj.startTime!!.time<childObj.startTime!!.time)){
            childObj.startTime = mainObj.startTime
        }else{
            mainObj.startTime = childObj.startTime
            mainChange = true
        }
        //首张有效提单时间取早的
        if (null!=mainObj.firstBillDate&&null!=childObj.firstBillDate){
            if (mainObj.firstBillDate!!.time<childObj.firstBillDate!!.time){
                childObj.firstBillDate = mainObj.firstBillDate
            }else{
                mainObj.firstBillDate = childObj.firstBillDate
                mainChange = true
            }
        } else if(null!=mainObj.firstBillDate&&null==childObj.firstBillDate){
            childObj.firstBillDate = mainObj.firstBillDate
        } else if(null==mainObj.firstBillDate&&null!=childObj.firstBillDate){
            mainObj.firstBillDate = childObj.firstBillDate
            mainChange = true
        }
        //首张实提出库时间取早的
        if (null!=mainObj.firstDeliveryDate&&null!=childObj.firstDeliveryDate){
            if (mainObj.firstDeliveryDate!!.time<childObj.firstDeliveryDate!!.time){
                childObj.firstDeliveryDate = mainObj.firstDeliveryDate
            }else{
                mainObj.firstDeliveryDate = childObj.firstDeliveryDate
                mainChange = true
            }
        } else if(null!=mainObj.firstDeliveryDate&&null==childObj.firstDeliveryDate){
            childObj.firstDeliveryDate = mainObj.firstDeliveryDate
        } else if(null==mainObj.firstDeliveryDate&&null!=childObj.firstDeliveryDate){
            mainObj.firstDeliveryDate = childObj.firstDeliveryDate
            mainChange = true
        }
        //取最近的开单时间
        if ((null!=mainObj.billDate&&null==childObj.billDate)||
                (null!=childObj.billDate&&null!=mainObj.billDate&&mainObj.billDate!! >childObj.billDate)){
            childObj.billDate = mainObj.billDate
        }else{
            mainObj.billDate = childObj.billDate
            mainChange = true
        }
        return mainChange
    }


    //分页查询删除或转化记录
    fun findRecordList(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, dptName: String?, acctName: String?, type: String, pg: Pageable) = cstmRedPepo.findAll(compName, name, phone, startTime, endTime, dptName, acctName, type, pg)

    //查询联系人
    fun linkFindById(id: Long) = linkerRepo.findOne(id)

    //查询客户下主联系人
    fun findMainLink(cstmId: Long?) = linkerRepo.findMainLink(cstmId)

    fun linkFindByPhone(phone: String) = linkerRepo.findByPhone(phone)

    //删除联系人
    fun linkerDelete(obj: Linker, cstmId: Long, reason: String, uid: Long) {
        //删除客户联系人关系
        val cstmObj = customerRepo.findOne(cstmId)
        val linkerSet = cstmObj.linkers as MutableSet<Linker>
        val it = linkerSet.iterator()
        while (it.hasNext()) {
            val link = it.next()
            if (link.id == obj.id) it.remove()
        }
        cstmObj.linkers = linkerSet
        customerRepo.save(cstmObj)
        //保存删除记录
        val linkRcd = LinkerRecord()
        val newObj = commUtil.autoSetClass(obj, linkRcd, arrayOf("id", "creator", "createAt", "updateAt", "customers", "commMark")) as LinkerRecord
        newObj.delName = acctRepo.findOne(uid).name
        newObj.delReason = reason
        newObj.delDate = Timestamp(Date().time)
        newObj.cstmName = cstmObj.compName
        linkRcdRepo.save(newObj)
    }

    //客户更新时，更新联系人
    fun linkerUpdate(linker: Linker): Linker {
        val originObj = linkerRepo.findOne(linker.id)
        val newObj = commUtil.autoSetClass(linker, originObj, arrayOf("id", "customers", "createAt", "updateAt", "creator", "mainStatus", "commMark")) as Linker
        linkerRepo.save(newObj)
        return newObj
    }

    //分页查询所有联系人
    fun findLinkerList(xyMark: String?, compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?, dptName: String?, acctName: String?, uid: Long, pg: Pageable): Page<Any> {
        val uAcct = acctRepo.findOne(uid)
        val id: String? = if ("业务员".equals(uAcct.dataLevel)) uAcct.id.toString() else null
        val dptId: String? = if ("部门".equals(uAcct.dataLevel)) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构".equals(uAcct.dataLevel)) uAcct.fkDpt.fkOrg.id.toString() else null
        log.info(">>>acctId:$id,dptId:$dptId,orgId:$orgId")
        if (xyMark == null) return linkerRepo.findAll(compName, name, phone, startTime, endTime, mainStatus, sex, position, dptName, acctName, id, dptId, orgId, pg)
        else if (xyMark == "1") return linkerRepo.findXyAll(compName, name, phone, startTime, endTime, mainStatus, sex, position, dptName, acctName, id, dptId, orgId, pg)
        else return linkerRepo.findErpAll(compName, name, phone, startTime, endTime, mainStatus, sex, position, dptName, acctName, id, dptId, orgId, pg)
    }

    //联系人更新
    fun linkerSave(uid: Long, linker: Linker, newCstmId: Long, originCstmId: String?, source: String? = null): Linker {
        var obj = linker
        if (obj.id != null) {//有id就更新
            val originObj = linkerRepo.findOne(linker.id)
            //创建人
            linker.creator = originObj.creator
            if (originCstmId != null && newCstmId != originCstmId.toLong()) {//所属客户改了
                //客户名称、联系人姓名、联系方式修改后保存到linkerModify
                handleLinkModify(originCstmId, newCstmId, originObj, obj, uid)
                linkerDelete(originObj, originCstmId.toLong(), "联系人所属客户被修改，删除旧的联系人", uid) //删除旧的联系人和客户的关系
                obj = saveLinker(linker, newCstmId)
            } else {
                val count = linkerRepo.cstmCountByLinkId(linker.id!!)
                log.info(">>>count:$count")
                if (count > 1) {//存在一条主联系人关联几个客户的情况
                    linkerDelete(originObj, newCstmId, "主联系人与多个客户关联", uid)
                    linker.id = null
                    linker.mainStatus = originObj.mainStatus
                    obj = saveLinker(linker, newCstmId)
                    //联系人姓名、联系方式修改后保存到linkerModify和cstmModify
                    handleLinkModify(null, newCstmId, originObj, obj, uid, source)
                } else {
                    //联系人姓名、联系方式修改后保存到linkerModify和cstmModify
                    handleLinkModify(null, newCstmId, originObj, obj, uid, source)
                    val newObj = commUtil.autoSetClass(linker, originObj, arrayOf("id", "customers", "createAt", "updateAt", "mainStatus", "creator")) as Linker
                    obj = linkerRepo.save(newObj)
                }
            }
        } else {//新增
            linker.creator = acctRepo.findOne(uid.toLong())
            obj = saveLinker(linker, newCstmId)
        }
        return obj
    }

    /**
     * 添加常用联系人相关操作
     * @author samy
     * @date 2019/08/20
     */
    fun existCommonLinker(cstmId: Long, linkerId: Long) = commonCstmLinkerRepo.uniqueOne(cstmId, linkerId)

    fun batchUpdateCommonLinker(cstmId: Long) = commonCstmLinkerRepo.batchUpdateMark(cstmId)

    fun commonLinkerSave(commLinker: CommonCstmLinker) = commonCstmLinkerRepo.save(commLinker)

    fun commonLinkerByCstmId(cstmId: Long) = commonCstmLinkerRepo.commonLinkerByCstmId(cstmId)

    //保存联系人
    fun linkerSave(linker: Linker) = linkerRepo.save(linker)

    //查询当前客户所有联系人
    fun findLinkerAll(cstmId: Long) = linkerRepo.findAll(cstmId)

    //分页查询联系人删除记录
    fun findLinkRcdList(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?, uid: Long, pg: Pageable): Page<LinkerRecord> {
        val uAcct = acctRepo.findOne(uid)
        val id: String? = if ("业务员".equals(uAcct.dataLevel)) uAcct.id.toString() else null
        val dptId: String? = if ("部门".equals(uAcct.dataLevel)) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构".equals(uAcct.dataLevel)) uAcct.fkDpt.fkOrg.id.toString() else null
        return linkRcdRepo.findAll(compName, name, phone, startTime, endTime, mainStatus, sex, position, id, dptId, orgId, pg)
    }

    //分页查询联系人修改记录
    fun findLinkMdyList(linkId: Long, pg: Pageable) = linkMdyRepo.findAll(linkId, pg)

    //查询当前客户所有沟通信息
    fun findCmutAll(cstmId: Long) = cmutRepo.findAll(cstmId)

    //新增沟通信息
    fun cmutSave(cmut: Communicate) = cmutRepo.save(cmut)

    //删除沟通信息
    fun cmutDelete(id: Long) = cmutRepo.delete(id)

    //查询当前客户下的主银行信息
    fun findMainBankInfo(cstmId: Long?) = bkIfRepo.findBankInfo(cstmId)

    //查询当前客户所有银行信息
    fun findBankInfoAll(cstmId: Long) = bkIfRepo.findAll(cstmId)

    //修改银行信息
    fun bankInfoSave(bkIf: BankInfo, cstmId: Long, uid: Long): BankInfo {
        var obj = bkIf
        val cstm = customerRepo.findOne(cstmId)
        //有id就更新，没有新增
        if (obj.id != null) {
            val originObj = bkIfRepo.findOne(obj.id)
            handleBankMdy(bkIf, originObj, cstm, uid)
            val newObj = commUtil.autoSetClass(bkIf, originObj, arrayOf("id", "createAt", "updateAt", "fkCstm", "mainAcct")) as BankInfo
            obj = bkIfRepo.save(newObj)
        } else {
            bkIf.fkCstm = cstm
            obj = bkIfRepo.save(bkIf)
        }
        return obj
    }

    fun tsfmBank(name: String?): String {
        if (name.isNullOrBlank()) return " "
        return name!!
    }

    //删除银行信息
    fun bankInfoDelete(id: Long) = bkIfRepo.delete(id)

    //检查当前客户下银行账号是否唯一cstmId+bankAcct,true不唯一
    fun checkBankAcct(bkIf: BankInfo, cstmId: Long): Boolean {
        val count = bkIfRepo.bankCount(bkIf.bankAcct, cstmId)
        if (bkIf.id != null) {
            val originObj = bkIfRepo.findOne(bkIf.id)
            //银行账号变更则检查唯一性
            if (originObj.bankAcct != bkIf.bankAcct && count != 0) {
                return true
            }
        } else {
            if (count != 0) return true
        }
        return false
    }

    //检查业务员是否为erp用户，true为不是
    fun checkAccount(id: Long): Boolean {
        val acct = acctRepo.findOne(id)
        if (acct.platformCode == null) {
            return true
        }
        return false
    }

    //有供应商且工作组不填则提示
    fun checkSupplier(customer: Customer): Boolean {
        customer.busiRelation.map { busi ->
            if (busi.name == CrmConstants.BUSI_RELATION_SUPPLIER && customer.workgroupName.isNullOrBlank()) return true
        }
        return false
    }

    //检查客户下主联系人个数不能超过1个,true表示不能再增加
    fun checkMainLinker(linker: Linker, cstmId: Long): Boolean {
        //传 mainStatus = 1 时检查
        if (linker.mainStatus == 1) {
            if (linker.id != null) return false //更新时
            val count = linkerRepo.linkCountByCstmId(cstmId)
            log.info(">>>mainLinker size:$count")
            if (count == 0) return false
            return true
        }
        return false
    }

    //检查修改电话时是否为型云客户主联系人，true表示是
    fun checkModifyPhone(linker: Linker, cstmId: Long): Boolean {
        val oldLink = linkerRepo.findOne(linker.id)
        if (oldLink.phone != linker.phone && oldLink.mainStatus == 1) {//主联系方式变更
            val cstm = customerRepo.findOne(cstmId)
            if (cstm.ebusiAdminAcctNo != null) {//型云客户
                return true
            }
        }
        return false
    }

    /**
     * 保存/更新属于客户的微信联系人
     * @author samy
     * @date 2019/12/30
     */
    @Throws(Exception::class)
    fun cstmWxLinker(cstmId: String, name: String, openId: String, appName: String, appKey: String, avatar: String, subscribe: String) {
        val cstm = customerRepo.findOne(cstmId.toLong())
        if (cstm == null) {
            throw Exception("valid customer id")
            return
        }
        var wxLinker = wxLinkerRepo.findByOpenIdAndTypeAndFkCstm(openId, 1, cstm)
        if (wxLinker == null) {
            wxLinker = WxLinker()
        }
        wxLinker.fkCstm = cstm
        wxLinker.appKey = appKey
        wxLinker.appName = appName
        wxLinker.name = name
        wxLinker.openId = openId
        wxLinker.avatar = avatar
        wxLinker.subscribe = subscribe
        wxLinker.type = 1
        wxLinkerRepo.save(wxLinker)
    }

    /**
     * 绑定微信授权型云客户联系人
     * @author samy
     * @date 2020/03/27
     */
    @Throws(Exception::class)
    fun bindWxAuthXyLinker(wxLinker: WxLinker, xyNo: String): String? {
        val cstm = customerRepo.findByEbusiAdminAcctNo(xyNo)
        if (cstm == null) {
            return "用户不存在"
        } else if (wxLinker.openId.isNullOrEmpty()) {
            return "微信授权id为空"
        } else {
            var originLinker = wxLinkerRepo.findByOpenIdAndTypeAndFkCstm(wxLinker.openId!!, 1, cstm)
            if (originLinker == null) {
                originLinker = wxLinker
                originLinker!!.fkCstm = cstm
                originLinker!!.id = null
                originLinker!!.type = 1
                wxLinkerRepo.save(originLinker)
            } else {
                var change = false
                if (originLinker.subscribe.isNullOrEmpty() && !wxLinker.subscribe.isNullOrEmpty()) {
                    originLinker.subscribe = wxLinker.subscribe
                    change = true
                }
                if (originLinker.name != wxLinker.name) {
                    originLinker.name = wxLinker.name
                    change = true
                }
                if (change) wxLinkerRepo.save(originLinker)
                log.info("该微信客户【${originLinker.name}】已经与型云客户【${cstm.compName}】绑定")
            }
            return null
        }
    }


    /**
     * 更新客户微信联系人取关状态
     * @author samy
     * @date 2019/12/30
     */
    @Throws(Exception::class)
    fun cstmWxLinkerUnsubscribe(openId: String) {
        var cnt = wxLinkerRepo.countByOpenId(openId)
        if (cnt == 0) log.error("invalid unsubscribe openid:>>$openId")
        else {
            wxLinkerRepo.batchUpdateBySubscribe(openId)
        }
    }

    /**
     * 删除微信联系人
     * @author samy
     * @date 2020/01/02
     */
    fun wxLinkerDel(id: Long) = wxLinkerRepo.delete(id)

    /**
     * 查询单个客户下的微信用户列表
     * @author samy
     * @date 2020/01/02
     */
    fun cstmWxLinkers(cstmId: Long, pageable: Pageable) = wxLinkerRepo.selectByCstmId(cstmId, pageable)

    /**
     * 客户的微信用户列表(包含员工的联系人)
     * @author samy
     * @date 2020/01/17, 2020/01/19(修改)
     */
    fun cstmWxLinkers(cstmId: Long, acctId: Long) = wxLinkerRepo.cstmWxLinkers(cstmId, acctId)

    /**
     * 型云客户的微信用户列表
     * @author samy
     * @date 2020/01/17
     */
//    fun xyCstmWxLinkers(xyCode: String) = wxLinkerRepo.xyCstmWxLinkers(xyCode)

    /**
     * 查询客户公众号二维码ticket
     * @author samy
     * @date 2020/01/02
     */
    fun cstmWxTicket(cstm: Customer, appKey: String) = customerWxTickerRepo.findByFkCstmAndAppKey(cstm, appKey)

    fun cstmWxTicketSave(obj: CustomerWxTicket) = customerWxTickerRepo.save(obj)

    /**
     * 根据联系人手机号返回对应客户所属部门
     * @author samy
     * @date 2020/03/28
     */
    fun linkerDptNameByPhone(phone: String) = linkerRepo.getLinkerDptNameByPhone(phone)

    //修改所属客户时，需要检查
    fun checkCstmForLink(linker: Linker, newCstmId: Long, originCstmId: String? = null): Int {
        var type = 0
        //联系人更新，且所属客户改了
        if (linker.id != null && originCstmId != null && newCstmId != originCstmId.toLong()) {
            //是主联系人,不能修改所属客户
            if (linker.mainStatus == 1) type = 1
            else {
                //是普通联系人，需要检查修改的所属客户是否已存在
                val originObj = linkerRepo.findOne(linker.id)
                originObj.customers.map { s ->
                    if (s.id == newCstmId) type = 2
                }
            }
        }
        return type
    }

    //保存客户修改信息
    fun cstModifySave(cstMd: CustomerModify) = cstMdRepo.save(cstMd)

    //查询当前客户所有修改信息
    fun findCstmModifyList(startTime: String?, endTime: String?, acctName: String?, columnName: String?, cstmId: String, pg: Pageable): Page<CustomerModify> = cstMdRepo.findAll(startTime, endTime, acctName, columnName, cstmId, pg)

    //查询客户拜访信息,根据uid限制
    fun findCstmCallAll(compName: String?, callResult: String?, startTime: String?, endTime: String?, uid: String, mark: String, pg: Pageable): Page<CustomerCall> {
        //超时改为定时任务处理
        //0表示历史记录,1表示查拜访中的,2表示查全部
        if (mark == "0") return cstCallRepo.findHistoryAll(compName, callResult, startTime, endTime, uid, pg)
        if (mark == "2") return cstCallRepo.findAll(compName, null, startTime, endTime, uid, pg)
        return cstCallRepo.findAll(compName, "0", startTime, endTime, uid, pg)
    }

    //根据uid查询某一客户的拜访信息
    fun findCstmCallList(uid: Long, cstmId: Long, status: Int, pg: Pageable) = cstCallRepo.findCallList(uid, cstmId, status, pg)

    //修改客户拜访信息
    fun cstmCallUpdate(cstmCall: CustomerCall) {
        val originObj = cstCallRepo.findOne(cstmCall.id)
        val newObj = commUtil.autoSetClass(cstmCall, originObj, arrayOf("id", "createAt", "updateAt", "customer", "creator", "clockTime", "planDate")) as CustomerCall
        cstCallRepo.save(newObj)
    }

    //签到
    fun cstmCallSignIn(cstmCall: CustomerCall) {
        val originObj = cstCallRepo.findOne(cstmCall.id)
        originObj.longitude = cstmCall.longitude
        originObj.latitude = cstmCall.latitude
        originObj.locateAddr = cstmCall.locateAddr
        originObj.remark = cstmCall.remark
        originObj.clockTime = Timestamp(Date().time)//成功的打卡时间取当前时间
        originObj.status = 1
        cstCallRepo.save(originObj)
    }

    //保存客户拜访信息
    fun cstmCallSave(cstmId: Long, uid: Long, planDate: Timestamp, planVisitTime: Timestamp, callType: String?): CustomerCall {
        val cstmCall = CustomerCall()
        cstmCall.creator = acctRepo.findOne(uid)
        cstmCall.customer = customerRepo.findOne(cstmId)
        cstmCall.planDate = planDate
        cstmCall.planVisitTime = planVisitTime
        if (callType != null) cstmCall.callType = callType
        return cstCallRepo.save(cstmCall)
    }

    //校验客户拜访重复性 cstmId+acctId+visitTime同一天+status为0,true为不唯一
    fun checkRepeatCall(cstmId: Long, uid: Long, planVisitTime: Timestamp): Boolean {
        val visitTime = SimpleDateFormat("yyyy-MM-dd").format(planVisitTime)
        val count = cstCallRepo.repeatCallCount(cstmId, uid, visitTime)
        log.info(">>>RepeatCall count is:$count")
        if (count == 0) return false
        return true
    }

    //获取单个客户拜访信息
    fun findCallById(id: Long) = cstCallRepo.findOne(id)

    //统计拜访历史
    fun callCount(uid: Long) = cstCallRepo.callCount(uid)

    //按照日期查询用户订单情况
    fun cstmOrderCount(acctNo: String, startTime: Timestamp, endTime: Timestamp) = cstOrderRepo.orderCount(acctNo, startTime, endTime)

    // 物资品类统计列表
    fun goodsSalesList(memCode: String, startTime: Timestamp, endTime: Timestamp, category: String?) = goodsSaleRepo.findList(memCode, startTime, endTime, category)

    // 行为数据查询
    fun behaviorSearch(userId: String, event: String?, time: String?, source: String?, orderId: String?, orderNo: String?, goodsName: String?, standard: String?, length: String?, supply: String?, warehouse: String?, material: String?, measure: String?, toleranceRange: String?, weightRange: String?, toleranceStart: String?, toleranceEnd: String?, search: String?, pageable: Pageable) = behaviorRecordRepo.findAll(userId, event, time, source, orderId, orderNo, goodsName, standard, length, supply, warehouse, material, measure, toleranceRange, weightRange, toleranceStart, toleranceEnd, search, pageable)

    // 客户画像加入购物车品类次数
    fun goodsAddCartCount(userId: String, startTime: Timestamp, endTime: Timestamp) = behaviorRecordRepo.goodsAddCart(userId, startTime, endTime)

    // 加入购物车数量
    fun goodsCartCount(userId: String, startTime: Timestamp, endTime: Timestamp) = behaviorRecordRepo.goodsCartCount(userId, startTime, endTime)

    // 生成订单数量
    fun goodsOrderCount(userId: String, startTime: Timestamp, endTime: Timestamp) = behaviorRecordRepo.goodsOrderCount(userId, startTime, endTime)

    // 行为数据查询
    fun behaviorSearchByXy(userId: String, event: String?, time: String?, source: String?, orderId: String?, orderNo: String?, goodsName: String?, standard: String?, length: String?, supply: String?, warehouse: String?, material: String?, measure: String?, toleranceRange: String?, weightRange: String?, toleranceStart: String?, toleranceEnd: String?, search: String?, pageable: Pageable) = behaviorRecordRepo.findAllByXy(userId, event, time, source, orderId, orderNo, goodsName, standard, length, supply, warehouse, material, measure, toleranceRange, weightRange, toleranceStart, toleranceEnd, search, pageable)

    // 客户画像加入购物车品类次数
    fun goodsAddCartCountByXy(userId: String, startTime: Long, endTime: Long) = behaviorRecordRepo.goodsAddCartByXy(userId, startTime, endTime)

    // 加入购物车数量
    fun goodsCartCountByXy(userId: String, startTime: Long, endTime: Long) = behaviorRecordRepo.goodsCartCountByXy(userId, startTime, endTime)

    // 生成订单数量
    fun goodsOrderCountByxy(userId: String, startTime: Long, endTime: Long) = behaviorRecordRepo.goodsOrderCountByXy(userId, startTime, endTime)

    // 获取客户ERP物资品类(用于下拉选择)
    fun erpCustGoodsType(memberCode: String) = goodsSaleRepo.goodsType(memberCode)

    // 客户分级列表
    fun cstmRatingList(xyMark: String?, summaryDate: String, compName: String?, transDateStart: Timestamp?, transDateEnd: Timestamp?, dptName: String?, acctName: String?, min: Double?, max: Double?, level: String?, pg: Pageable): Page<GradeSummary> {
        val xyCondition = if (xyMark == null) null else if (xyMark == "1") "1" else "2"
        return gradeSummaryRepo.findDayStatistic(summaryDate, compName, transDateStart, transDateEnd, dptName, acctName, min, max, level, xyCondition, pg)
    }

    // 单个客户分级明细
    fun cstmRatingDetailList(compName: String, startDate: Timestamp?, endDate: Timestamp?, pg: Pageable) = gradeSummaryRepo.findCstmStatistic(compName, startDate, endDate, pg)

    // 客户分级下拉列表
    fun cstmRatingLevelComb(dateStr: String) = gradeSummaryRepo.queryLevelComb(dateStr)

    //联系人修改保存记录表
    fun handleLinkerMdy(oldCstmId: Long, newCstm: Customer, uid: Long, newLinker: Linker, remark: String) {
        val oldLinker = findMainLink(oldCstmId)
        if (oldLinker.name != newLinker.name) {
            log.info("handle linker name>>>>oldName:${oldLinker.name}>>>newName:${newLinker.name}")
            saveCstModify(oldLinker.name, newLinker.name, "主要联系人", newCstm, uid, remark)
            saveLinkModify(oldLinker, oldLinker.name, newLinker.name, "联系人姓名", uid, remark)
        }
        if (oldLinker.phone != newLinker.phone) {
            log.info("handle linker phone>>>>oldPhone:${oldLinker.phone}>>>newPhone:${newLinker.phone}")
            saveCstModify(oldLinker.phone, newLinker.phone, "联系电话", newCstm, uid, remark)
            saveLinkModify(oldLinker, oldLinker.phone, newLinker.phone, "联系方式", uid, remark)
        }
    }

    //起始日期修改保存记录表
    fun handleStartTime(oldCstm: Customer, newCstm: Customer, uid: Long, remark: String) {
        val newTime = if (newCstm.startTime == null) "" else shortSdf.format(newCstm.startTime)
        val oldTime = if (oldCstm.startTime == null) "" else shortSdf.format(oldCstm.startTime)
        if (newTime != oldTime) saveCstModify(oldTime, newTime, "起始日期", newCstm, uid, remark)
    }

    //状态修改保存记录表
    fun handleStatus(oldCstm: Customer, newCstm: Customer, uid: Long, remark: String) {
        val newStatus = if (newCstm.status == 0) "停用" else if (newCstm.status == 1) "启用" else "删除"
        val oldStatus = if (oldCstm.status == 0) "停用" else if (oldCstm.status == 1) "启用" else "删除"
        if (newStatus != oldStatus) saveCstModify(oldStatus, newStatus, "客户状态", newCstm, uid, remark)
    }

    //关联主体修改保存记录表
    fun handleMainCst(oldCstm: Customer, newCstm: Customer, uid: Long, remark: String) {
        val newMainCst = if (null!=newCstm.mainCstm) newCstm.mainCstm!!.compName else ""
        val oldMainCst = if (null!=oldCstm.mainCstm) oldCstm.mainCstm!!.compName else ""
        if (newMainCst != oldMainCst) saveCstModify(oldMainCst, newMainCst, "客户主体", newCstm, uid, remark)
    }

    //业务员修改保存记录表
    fun handleAcctMdy(oldCstm: Customer, id: Long?, newCstm: Customer, uid: Long, remark: String) {
        val newAcct = acctRepo.findOne(id)
        if (id != oldCstm.fkAcct.id) saveCstModify(oldCstm.fkAcct.name, newAcct.name, "业务员", newCstm, uid, remark)
    }

    //部门修改保存记录表
    fun handleDptMdy(oldCstm: Customer, id: Long?, newCstm: Customer, uid: Long, remark: String) {
        val newDpt = dptRepo.findOne(id)
        if (id != oldCstm.fkDpt.id) saveCstModify(oldCstm.fkDpt.name, newDpt.name, "业务部门", newCstm, uid, remark)
    }

    //客户性质修改保存记录表
    fun handleCstmProMdy(oldCstm: Customer, id: Long?, newCstm: Customer, uid: Long, remark: String) {
        val newPro = customPropertyRepo.findOne(id)
        if (id != oldCstm.fkCustomProperty.id) saveCstModify(oldCstm.fkCustomProperty.name, newPro.name, "客户性质", newCstm, uid, remark)
    }

    //业务关系修改保存记录表
    fun handleBusiRelMdy(oldCstm: Customer, busiRelations: Array<String>, newCstm: Customer, uid: Long, remark: String) {
        if (checkBusi(busiRelations, oldCstm)) {
            var oldBusi: String = ""
            var newBusi: String = ""
            for (busi in busiRelations) {
                val obj = busiRelationRepo.findOne(busi.toLong())
                newBusi = newBusi + obj.name + " "
            }
            if (oldCstm.busiRelation.isEmpty()) oldBusi = " "
            else {
                for (busi in oldCstm.busiRelation) {
                    val obj = busiRelationRepo.findOne(busi.id)
                    oldBusi = oldBusi + obj.name + " "
                }
            }
            log.info("handle busiRelation>>>>oldBusi:$oldBusi>>>newBusi:$newBusi")
            saveCstModify(oldBusi, newBusi, "业务关系", newCstm, uid, remark)
        }
    }

    //判断业务关系是否变更,true表示有变化
    fun checkBusi(busiRelations: Array<String>, oldCstm: Customer): Boolean {
        if (busiRelations.size == oldCstm.busiRelation.size) {
            oldCstm.busiRelation.map { s ->
                if (!busiRelations.contains(s.id.toString())) {
                    return true
                }
            }
            return false
        }
        return true
    }

    fun handleLinkCreate(customer: Customer, linker: Linker, uid: Long) {
        log.info("start handling linker>>${linker.phone}")
        val linkerSet = HashSet<Linker>()
        val customers = HashSet<Customer>()
        customers.add(customer)
        linker.customers = customers
        linker.mainStatus = 1
        linker.creator = acctRepo.findOne(uid)
        linkerSet.add(linker)
        customer.linkers = linkerSet
    }

    fun handleBankInfo(obj: Customer): BankInfo {
        val bankInfo = BankInfo()
        if (obj.openAcctName != null) bankInfo.name = obj.openAcctName!!
        if (obj.openBank != null) bankInfo.openBank = obj.openBank!!
        if (obj.openAcct != null) bankInfo.bankAcct = obj.openAcct!!
        bankInfo.mainAcct = 1
        return bankInfo
    }

    //检查客户信息完善度
    fun checkCstmIntegrity(customer: Customer, checkMap: Map<String, String>): String {
        var msg = ""
        var mark = false
        checkMap.keys.map { key ->
            val m = customer.javaClass.getMethod("get$key")
            val value = if (m.invoke(customer) == null) "" else m.invoke(customer).toString()
            if (!mark && value.isBlank()) {
                mark = true
                msg = "${checkMap[key]}未填写,请完善客户信息"
            }
        }
        return msg
    }

    //首页任务完成情况查询
    fun taskCompletion(acctId: Long, dateType: Int, type: Int): List<Double> {
        val obj = acctRepo.findOne(acctId)
        //dateType 0 本月度 1 本季度 2 本年度
        val startDate = if (dateType == 0) commUtil.getMonthStartTime(0) else if (dateType == 1) commUtil.getQuarterStartTime(0) else commUtil.getYearStartTime(0)
        val endDate = if (dateType == 0) commUtil.getMonthEndTime(0) else if (dateType == 1) commUtil.getQuarterEndTime(0) else commUtil.getYearEndTime(0)
        if (type == 0) {//0 公司
            return handleCount(null, startDate, endDate, dateType, 0, null, null)
        } else if (type == 1) {//1 机构
            val includeList = acctRepo.findAcctByOrg(obj.fkDpt.fkOrg.id!!)
            return handleCount(includeList, startDate, endDate, dateType, 1, obj.fkDpt.fkOrg.name, null)
        } else if (type == 2) {//2 部门
            val includeList = acctRepo.findAcctByDpt(obj.fkDpt.id!!)
            return handleCount(includeList, startDate, endDate, dateType, 2, null, obj.fkDpt.name)
        } else {//3 个人
            val employeeCode = if (obj.platformCode == null) " " else obj.platformCode
            return handleCount(employeeCode, startDate, endDate, dateType, 3, null, null)
        }
    }

    //首页产品销量情况查询
    fun productSales(acctId: Long, dateType: Int, type: Int): List<Double> {
        val obj = acctRepo.findOne(acctId)
        if (dateType == 0) {//天
            return handleType(type, obj, commUtil.getDay(-2), commUtil.getDay(-2), commUtil.getDay(-1), commUtil.getDay(-1))
        } else if (dateType == 1) {//周
            return handleType(type, obj, commUtil.getWeekStartTime(-1), commUtil.getWeekEndTime(-1), commUtil.getWeekStartTime(0), commUtil.getWeekEndTime(0))
        } else if (dateType == 2) {//月
            return handleType(type, obj, commUtil.getMonthStartTime(-1), commUtil.getMonthEndTime(-1), commUtil.getMonthStartTime(0), commUtil.getMonthEndTime(0))
        } else if (dateType == 3) {//季度
            return handleType(type, obj, commUtil.getQuarterStartTime(-1), commUtil.getQuarterEndTime(-1), commUtil.getQuarterStartTime(0), commUtil.getQuarterEndTime(0))
        } else {//年
            return handleType(type, obj, commUtil.getYearStartTime(-1), commUtil.getYearEndTime(-1), commUtil.getYearStartTime(0), commUtil.getYearEndTime(0))
        }
    }

    //首页查询业务员汇总数据
    fun summary(acctId: Long): List<String> {
        val obj = acctRepo.findOne(acctId)
        val list = ArrayList<String>()
        val position = if (obj.position.isNullOrBlank()) "" else obj.position!!
        list.add(position)
        if (obj.platformCode == null) {
            for (i in 1..3) list.add("0")
        } else {
            list.add(customerRepo.cstmCounting(obj.platformCode!!).toString())
            val goodsCount = saleGoodsRepo.goodsCounting(obj.platformCode!!)
            if (goodsCount.isEmpty()) {
                for (i in 1..2) list.add("0")
            } else {
                val tempGoodsArr = goodsCount[0] as Array<Any>
                val amount = tempGoodsArr[1] as Double
                list.add(String.format("%.3f", tempGoodsArr[0]))
                list.add(String.format("%.2f", amount / 10000))
            }
        }
        return list
    }

    //公司或个人数据
    fun handleCount(employeeCode: String?, startDate: Timestamp, endDate: Timestamp, dateType: Int, type: Int, orgName: String?, dptName: String?): List<Double> {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthIntList = currentQuarterInt(month.toDouble())
        val list = ArrayList<Double>()
        val goodsList = saleGoodsRepo.goodsCount(employeeCode, startDate, endDate) as ArrayList<Double>
        val highsale = highSaleRepo.highSellCount(employeeCode, startDate, endDate)
        val newCstm = customerRepo.newCstmCount(employeeCode, startDate, endDate)
        val secondCstm = cstmRedPepo.secondCstmCount(employeeCode, startDate, endDate)
        val taskList = if (dateType == 0) taskRepo.taskCount(year, month.toString(), type, orgName, dptName, employeeCode) else if (dateType == 1) taskRepo.taskCount(year, monthIntList, type, orgName, dptName, employeeCode) else taskRepo.taskCount(year, null, type, orgName, dptName, employeeCode)
        handleTaskList(list, goodsList, highsale, newCstm, secondCstm, taskList)
        return list
    }

    fun handleTaskList(list: ArrayList<Double>, goodsList: List<Double>, highsale: Double, newCstm: Double, secondCstm: Double, taskList: List<Double>) {
        if (goodsList.isEmpty()) {
            for (i in 1..4) {
                list.add(0.0)
            }
        } else {
            val tempGoodsArr = goodsList[0] as Array<Any>
            tempGoodsArr.map { s ->
                val count = s as Double
                list.add(count)
            }
        }
        list.add(highsale)
        list.add(newCstm)
        list.add(secondCstm)
        if (taskList.isEmpty()) {
            for (i in 1..7) {
                list.add(0.0)
            }
        } else {
            val tempTaskArr = taskList[0] as Array<Any>
            tempTaskArr.map { s ->
                if (s is Double) list.add(s)
                else if (s is Long) list.add(s.toDouble())
                else {
                }
            }
        }
    }

    //获取当前季度 1,2
    fun currentQuarterInt(currentMonth: Double): List<Int> {
        val list = ArrayList<Int>()
        val quarterNum = Math.ceil(currentMonth / 3).toInt()
        list.add(quarterNum * 3 - 2)
        list.add(quarterNum * 3 - 1)
        list.add(quarterNum * 3)
        return list
    }

    //机构或部门数据
    fun handleCount(includeList: List<String>, startDate: Timestamp, endDate: Timestamp, dateType: Int, type: Int, orgName: String?, dptName: String?): List<Double> {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthIntList = currentQuarterInt(month.toDouble())
        val list = ArrayList<Double>()
        var goodsList = ArrayList<Double>()
        var highsale: Double = 0.0
        var newCstm: Double = 0.0
        var secondCstm: Double = 0.0
        if (includeList.isNotEmpty()) {
            goodsList = saleGoodsRepo.goodsCount(includeList, startDate, endDate) as ArrayList<Double>
            highsale = highSaleRepo.highSellCount(includeList, startDate, endDate)
            newCstm = customerRepo.newCstmCount(includeList, startDate, endDate)
            secondCstm = cstmRedPepo.secondCstmCount(includeList, startDate, endDate)
        }
        val taskList = if (dateType == 0) taskRepo.taskCount(year, month.toString(), type, orgName, dptName, null) else if (dateType == 1) taskRepo.taskCount(year, monthIntList, type, orgName, dptName, null) else taskRepo.taskCount(year, null, type, orgName, dptName, null)
        handleTaskList(list, goodsList, highsale, newCstm, secondCstm, taskList)
        return list
    }

    fun handleType(type: Int, obj: Account, firstDate: Timestamp, secondDate: Timestamp, thirdDate: Timestamp, fourthDate: Timestamp): List<Double> {
        val list = ArrayList<Double>()
        if (type == 0 || type == 3) {//0 公司 3 个人
            val employeeCode = if (type == 0) null else if (obj.platformCode == null) " " else obj.platformCode
            val acctId = if (type == 0) null else obj.id.toString()
            handleSaleCount(list, acctId, employeeCode, firstDate, secondDate)
            handleSaleCount(list, acctId, employeeCode, thirdDate, fourthDate)
        } else {//1 机构 2 部门
            val empCodeList = if (type == 1) acctRepo.findAcctByOrg(obj.fkDpt.fkOrg.id!!) else acctRepo.findAcctByDpt(obj.fkDpt.id!!)
            var dptId: String? = null
            var orgId: String? = null
            if (type == 1) orgId = obj.fkDpt.fkOrg.id!!.toString() else dptId = obj.fkDpt.id!!.toString()
            handleSaleCount(list, dptId, orgId, empCodeList, firstDate, secondDate)
            handleSaleCount(list, dptId, orgId, empCodeList, thirdDate, fourthDate)
        }
        return list
    }

    fun handleSaleCount(list: ArrayList<Double>, dptId: String?, orgId: String?, empCodeList: List<String>, startDate: Timestamp, endDate: Timestamp) {
        var goodsList = ArrayList<Double>()
        var cstmList = ArrayList<Double>()
        var scdCstm: Double = 0.0
        val orderList = cstOrderRepo.orderCounting(dptId, orgId, null, startDate, endDate) as ArrayList<Double>
        if (empCodeList.isNotEmpty()) {
            goodsList = saleGoodsRepo.goodsCounting(empCodeList, startDate, endDate) as ArrayList<Double>
            cstmList = customerRepo.cstmCounting(empCodeList, startDate, endDate) as ArrayList<Double>
            scdCstm = cstmRedPepo.secondCstmCounting(empCodeList, startDate, endDate)
        }
        handleSaleList(list, goodsList, orderList, cstmList, scdCstm)
    }

    fun handleSaleCount(list: ArrayList<Double>, acctId: String?, employeeCode: String?, startDate: Timestamp, endDate: Timestamp) {
        val orderList = cstOrderRepo.orderCounting(null, null, acctId, startDate, endDate)
        val goodsList = saleGoodsRepo.goodsCounting(employeeCode, startDate, endDate)
        val cstmList = customerRepo.cstmCounting(employeeCode, startDate, endDate)
        val scdCstm = cstmRedPepo.secondCstmCounting(employeeCode, startDate, endDate)
        handleSaleList(list, goodsList, orderList, cstmList, scdCstm)
    }

    fun handleSaleList(list: ArrayList<Double>, goodsList: List<Double>, orderList: List<Double>, cstmList: List<Double>, scdCstm: Double) {
        if (goodsList.isEmpty()) {
            for (i in 1..6) {
                list.add(0.0)
            }
        } else {
            val tempGoodsArr = goodsList[0] as Array<Any>
            tempGoodsArr.map { s ->
                val count = s as Double
                list.add(count)
            }
        }
        if (orderList.isEmpty()) {
            for (i in 1..5) {
                list.add(0.0)
            }
        } else {
            val tempOrderArr = orderList[0] as Array<Any>
            tempOrderArr.map { s ->
                if (s is Double) list.add(s)
                else if (s is Long) list.add(s.toDouble())
                else {
                }
            }
        }
        if (cstmList.isEmpty()) {
            for (i in 1..3) {
                list.add(0.0)
            }
        } else {
            val tempCstmArr = cstmList[0] as Array<Any>
            tempCstmArr.map { s ->
                if (s is Double) list.add(s)
                else if (s is Long) list.add(s.toDouble())
                else {
                }
            }
        }
        list.add(scdCstm)
    }

    //手机端分页查询所有的客户
    fun findCstmForMobile(uid: String, billType: String?, initial: String?, compName: String?, dataType: String?, mark: String?, pg: Pageable): Page<Any> {
        val uAcct = acctRepo.findOne(uid.toLong())
        val firstTime = shortSdf.format(Date())//当天
        val secondTime = shortSdf.format(commUtil.getDay(-30))//30天前
        val thirdTime = shortSdf.format(commUtil.getDay(-60))//60天前
        val fourthTime = shortSdf.format(commUtil.getDay(-90))//90天前
        val startTime = if (billType == "0") secondTime else if (billType == "1") thirdTime else if (billType == "2") fourthTime else null
        val endTime = if (billType == "0") firstTime else if (billType == "1") secondTime else if (billType == "2") thirdTime else null
        var dataLevel = uAcct.dataLevel
        if (mark == null) {//搜索功能 所有公共客户与权限筛选过的潜在正式客户
            val acctId = if (dataLevel == "业务员") uAcct.id.toString() else null
            val dptId = if (dataLevel == "部门") uAcct.fkDpt.id.toString() else null
            val orgId = if (dataLevel == "机构") uAcct.fkDpt.fkOrg.id.toString() else null
            return customerRepo.findCstmListAll(compName, acctId, dptId, orgId, pg)
        } else if (mark != "3") {
            if (dataType != null) dataLevel = if (dataType == "0") "业务员" else uAcct.dataLevel
            if (dataLevel == "业务员") return customerRepo.findCstmByAcctId(compName, startTime, endTime, initial, mark, uAcct.id, pg)
            if (dataLevel == "部门") return customerRepo.findCstmByDptId(compName, startTime, endTime, initial, mark, uAcct.fkDpt.id, pg)
            if (dataLevel == "机构") return customerRepo.findCstmByOrgId(compName, startTime, endTime, initial, mark, uAcct.fkDpt.fkOrg.id, pg)
        }
        return customerRepo.findCstmList(compName, startTime, endTime, initial, mark, pg)
    }

    //手机端首页数据查询
    fun taskCompletionForMobile(uid: Long, type: Int): List<Double> {
        val list = ArrayList<Double>()
        val uAcct = acctRepo.findOne(uid)
        log.info(">>>业务员权限为：${uAcct.dataLevel}")
        var employeeCode: String? = null
        var includeList: List<String>? = null
        var mark: Int = 0
        var taskType: Int = 0 //0公司 1机构 2部门 3个人
        var orgName: String? = null
        var dptName: String? = null
        if (uAcct.dataLevel == "业务员") {
            employeeCode = if (uAcct.platformCode == null) " " else uAcct.platformCode
            taskType = 3
        } else if (uAcct.dataLevel == "部门") {
            includeList = acctRepo.findAcctByDpt(uAcct.fkDpt.id!!)
            mark = 1
            taskType = 2
            dptName = uAcct.fkDpt.name
        } else if (uAcct.dataLevel == "机构") {
            includeList = acctRepo.findAcctByOrg(uAcct.fkDpt.fkOrg.id!!)
            mark = 1
            taskType = 1
            orgName = uAcct.fkDpt.fkOrg.name
        } else {
        }
        handleHomeData(list, employeeCode, includeList, type, mark, taskType, orgName, dptName)
        if (list.isEmpty()) {
            if (type == 0) for (i in 1..36) list.add(0.0)
            else for (i in 1..27) list.add(0.0)
        }
        return list
    }

    fun handleHomeData(list: ArrayList<Double>, employeeCode: String?, includeList: List<String>?, type: Int, mark: Int, taskType: Int, orgName: String?, dptName: String?) {
        val dayStr1 = shortSdf.format(commUtil.getDay(-1))
        val dayStr2 = shortSdf.format(commUtil.getDay(-2))
        val dayStr3 = shortSdf.format(commUtil.getDay(-3))
        val dayStr4 = shortSdf.format(commUtil.getDay(-4))
        val dayStr5 = shortSdf.format(commUtil.getDay(-5))
        val dayStr6 = shortSdf.format(commUtil.getDay(-6))
        val dayStr7 = shortSdf.format(commUtil.getDay(-7))
        val startDayStr = shortSdf.format(commUtil.getDay(commUtil.getMonthEndTime(-1), -7))
        val monthStartStr = shortSdf.format(commUtil.getMonthStartTime(0))
        val monthEndStr = shortSdf.format(commUtil.getMonthEndTime(0))
        val dayArr = arrayOf(dayStr1, dayStr2, dayStr3, dayStr4, dayStr5, dayStr6, dayStr7)

        var goodsList = ArrayList<SalesmanGoods>()
        var highsale = ArrayList<SalesmanHighSell>()
        var newCstm = ArrayList<Double>()
        var secondCstm = ArrayList<Double>()
        var taskList = ArrayList<Double>()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        //昨天，前天，前三，前四，前五，前六，前七，月累计
        if (mark == 0) {//业务员和公司
            taskList = taskRepo.taskCount(year, month, taskType, null, null, employeeCode) as ArrayList<Double>
            if (type == 0) {//全部，线上，线下，板材
                goodsList = saleGoodsRepo.goodsList(employeeCode, startDayStr, monthEndStr)
                handleGoodsList(dayArr, list, goodsList, taskList, monthStartStr, monthEndStr)
            } else {//高卖，新客户，二次开发
                val tempTaskArr = taskList[0] as Array<Any>
                highsale = highSaleRepo.highSellList(employeeCode, startDayStr, monthEndStr)
                handleHighSellList(dayArr, list, highsale, tempTaskArr, monthStartStr, monthEndStr)

                newCstm = customerRepo.newCstmCountList(employeeCode, dayStr1, dayStr2, dayStr3, dayStr4, dayStr5, dayStr6, dayStr7, monthStartStr, monthEndStr) as ArrayList<Double>
                val tempNewArr = newCstm[0] as Array<Any>
                for (i in 0..7) {
                    list.add(tempNewArr[i].toString().toDouble())
                }
                list.add(tempTaskArr[5].toString().toDouble())

                secondCstm = cstmRedPepo.secondCstmCountList(employeeCode, dayStr1, dayStr2, dayStr3, dayStr4, dayStr5, dayStr6, dayStr7, monthStartStr, monthEndStr) as ArrayList<Double>
                val tempSecondArr = secondCstm[0] as Array<Any>
                for (i in 0..7) {
                    list.add(tempSecondArr[i].toString().toDouble())
                }
                list.add(tempTaskArr[6].toString().toDouble())
            }
        } else if (mark == 1 && includeList!!.isNotEmpty()) {//机构和部门
            taskList = taskRepo.taskCount(year, month, taskType, orgName, dptName, null) as ArrayList<Double>
            if (type == 0) {//全部，线上，线下，板材
                goodsList = saleGoodsRepo.goodsList(includeList, startDayStr, monthEndStr)
                handleGoodsList(dayArr, list, goodsList, taskList, monthStartStr, monthEndStr)
            } else {//高卖，新客户，二次开发
                val tempTaskArr = taskList[0] as Array<Any>
                highsale = highSaleRepo.highSellList(includeList, startDayStr, monthEndStr)
                handleHighSellList(dayArr, list, highsale, tempTaskArr, monthStartStr, monthEndStr)

                newCstm = customerRepo.newCstmCountList(includeList, dayStr1, dayStr2, dayStr3, dayStr4, dayStr5, dayStr6, dayStr7, monthStartStr, monthEndStr) as ArrayList<Double>
                val tempNewArr = newCstm[0] as Array<Any>
                for (i in 0..7) {
                    list.add(tempNewArr[i].toString().toDouble())
                }
                list.add(tempTaskArr[5].toString().toDouble())

                secondCstm = cstmRedPepo.secondCstmCountList(includeList, dayStr1, dayStr2, dayStr3, dayStr4, dayStr5, dayStr6, dayStr7, monthStartStr, monthEndStr) as ArrayList<Double>
                val tempSecondArr = secondCstm[0] as Array<Any>
                for (i in 0..7) {
                    list.add(tempSecondArr[i].toString().toDouble())
                }
                list.add(tempTaskArr[6].toString().toDouble())
            }
        } else {
        }
    }

    fun handleGoodsList(dayArr: Array<String>, list: ArrayList<Double>, goodsList: ArrayList<SalesmanGoods>, taskList: ArrayList<Double>, startStr: String, endStr: String) {
        dayArr.indices.map { i ->
            goodsForDay(list, goodsList, dayArr[i])
        }
        val tempList = goodsList.filter { goods -> shortSdf.format(goods.dealDate) >= startStr && shortSdf.format(goods.dealDate) <= endStr }
        val all = tempList.map { s -> s.weight!! }.sum()
        val online = tempList.filter { goods -> goods.type == 1 }.map { s -> s.weight!! }.sum()
        val offline = tempList.filter { goods -> goods.type == 0 }.map { s -> s.weight!! }.sum()
        val board = tempList.filter { goods -> goods.category == "板材" }.map { s -> s.weight!! }.sum()
        list.add(all)
        list.add(online)
        list.add(offline)
        list.add(board)
        val tempTaskArr = taskList[0] as Array<Any>
        list.add(tempTaskArr[0].toString().toDouble())
        list.add(tempTaskArr[1].toString().toDouble())
        list.add(tempTaskArr[2].toString().toDouble())
        list.add(tempTaskArr[3].toString().toDouble())
    }

    fun handleHighSellList(dayArr: Array<String>, list: ArrayList<Double>, highsale: ArrayList<SalesmanHighSell>, tempTaskArr: Array<Any>, startStr: String, endStr: String) {
        dayArr.indices.map { i ->
            highSellForDay(list, highsale, dayArr[i])
        }
        val tempList = highsale.filter { high -> shortSdf.format(high.dealDate) >= startStr && shortSdf.format(high.dealDate) <= endStr }
        val high = tempList.map { s -> s.highAmount!! }.sum()
        list.add(high)
        list.add(tempTaskArr[4].toString().toDouble())
    }

    fun goodsForDay(list: ArrayList<Double>, goodsList: ArrayList<SalesmanGoods>, dayStr: String) {
        val tempList = goodsList.filter { goods -> shortSdf.format(goods.dealDate) == dayStr }
        val all = tempList.map { s -> s.weight!! }.sum()
        val online = tempList.filter { goods -> goods.type == 1 }.map { s -> s.weight!! }.sum()
        val offline = tempList.filter { goods -> goods.type == 0 }.map { s -> s.weight!! }.sum()
        val board = tempList.filter { goods -> goods.category == "板材" }.map { s -> s.weight!! }.sum()
        list.add(all)
        list.add(online)
        list.add(offline)
        list.add(board)
    }

    fun highSellForDay(list: ArrayList<Double>, highsale: ArrayList<SalesmanHighSell>, dayStr: String) {
        val tempList = highsale.filter { high -> shortSdf.format(high.dealDate) == dayStr }
        val high = tempList.map { s -> s.highAmount!! }.sum()
        list.add(high)
    }

    // 新增商机
    fun opptyCreate(busiOppty: BusinessOpportunity) = busiOpptyRepo.save(busiOppty)

    // 商机查询
    fun opptyAll(opptyName: String?, dataLevel: String, uid: String, pg: Pageable): Page<BusinessOpportunity> {
        //0只看自己 1部门级 2机构级 3公司级（所有）
        var orgId: String? = null
        var dptId: String? = null
        var acctId: String? = null
        val acct = acctRepo.findOne(uid.toLong())
        if (dataLevel == "0") acctId = uid else if (dataLevel == "1") dptId = acct.fkDpt.id.toString() else if (dataLevel == "2") orgId = acct.fkDpt.fkOrg.id.toString() else {
        }
        return busiOpptyRepo.findBusiOpptyAll(opptyName, orgId, dptId, acctId, pg)
    }

    // 商机查询
    fun opptyAll(opptyName: String?, linkerName: String?, linkerPhone: String?, cstmName: String?, dptName: String?, acctName: String?, createAt: String?, opptyAddr: String?, pg: Pageable) = busiOpptyRepo.findBusiOpptyAll(opptyName, linkerName, linkerPhone, cstmName, dptName, acctName, createAt, opptyAddr, pg)

    // 手机端消息列表
    fun acctMessage(uid: Long, type: Int) = msgRepo.findMsgByAcctId(uid.toString(), type)

    // 更新消息为已读
    fun msgUpdate(id: Long) {
        val obj = msgRepo.findOne(id)
        obj.status = 1
        msgRepo.save(obj)
    }

    //新增客户拜访 如果计划拜访时间就是当天则需要发送通知
    fun cstmCallForMobile(cstmId: Long, uid: Long, planDate: Timestamp, planVisitTime: Timestamp, callType: String?) {
        val obj = cstmCallSave(cstmId, uid, planDate, planVisitTime, callType)
        val diff = commUtil.getMinute(planVisitTime, -30).time - Date().time
        val visitTime = SimpleDateFormat("yyyy-MM-dd").format(planVisitTime)
        val currentTime = SimpleDateFormat("yyyy-MM-dd").format(Date())
        log.info(">>>>visitTime is:$visitTime,currentTime is:$currentTime,diff is:${diff / (1000 * 60)}分钟")
        if (visitTime == currentTime && diff > 0) {
            // 单条拜访延时发送
            val sendMap = java.util.HashMap<String, String>()
            sendMap["mq_type"] = "0"
            sendMap["call_id"] = obj.id.toString()
            mqSenderService.sendDelayMsg(sendMap, diff)
        }
    }

    fun mobInfoCreate(acctId: Long, deviceNum: String, type: Int): MobileInfo {
        //查询是否已存在
        var obj = mobInfoRepo.findOne(deviceNum, type)
        if (obj != null) {
            obj.acctId = acctId
            obj.updateAt = Timestamp(Date().time)
            mobInfoRepo.save(obj)
        } else {
            obj = MobileInfo()
            obj.acctId = acctId
            obj.deviceNum = deviceNum
            obj.type = type
            mobInfoRepo.save(obj)
        }
        return obj
    }

    fun mobLogOut(deviceNum: String, type: Int): String {
        var msg = ""
        val obj = mobInfoRepo.findOne(deviceNum, type)
        if (obj != null) {
            obj.acctId = null
            mobInfoRepo.save(obj)
        } else msg = "设备信息不存在"
        return msg
    }

    fun compNameCount(compName: String) = customerRepo.compNameCount(compName)

    fun busiLicenseCodeCount(busiLicenseCode: String) = customerRepo.busiLicenseCodeCount(busiLicenseCode)

    fun tfnCount(tfn: String) = customerRepo.tfnCount(tfn)

    fun openAcctNameCount(openAcctName: String) = customerRepo.openAcctNameCount(openAcctName)

    //2019-01-18 统一mq同步入口位置
    // 行为数据保存
    fun behaviorRecordSave(record: Map<String, String>) {
        val cust = customerRepo.findByEbusiAdminAcctNo(record.get("user")!!)
        if (cust != null) {
            var behaviorRecord = behaviorRecordRepo.findByRowkey(record.get("rowkey")!!)
            if (behaviorRecord == null) behaviorRecord = BehaviorRecord()
            record.keys.map { key ->
                if ("is_special_offer".equals(key)) {
                    behaviorRecord.isSpecialOffer = record.get(key)
                } else if ("mq_type".equals(key)) {
                } else if ("is_new_product".equals(key)) {
                    behaviorRecord.isNewProduct = record.get(key)
                } else if ("user".equals(key)) {
                    behaviorRecord.userId = record.get(key)
                } else if ("time".equals(key)) {
                    behaviorRecord.time = Timestamp(record.get(key)!!.toLong())
                } else {
                    val doubleArr = arrayOf("diff", "money", "num", "lj_price", "bj_price", "min_price", "min_weight")
                    var m: Method? = null
                    val colName = getBehaviorColumnName(key)
                    log.info("colName:>>$colName")
                    log.info("map val:>>${record.get(key)}")
                    if (doubleArr.contains(key)) {
                        m = behaviorRecord.javaClass.getMethod(colName, Double::class.java)
                        m.invoke(behaviorRecord, if (record.get(key) == null) 0.0 else record.get(key)!!.toDouble())
                    } else {
                        m = behaviorRecord.javaClass.getMethod(colName, String::class.java)
                        m.invoke(behaviorRecord, if (record.get(key) == null) null else record.get(key)!!)
                    }
                }
            }
            behaviorRecordRepo.save(behaviorRecord)
        } else {
            log.info("当前crm没有该用户:>>>${record.get("user")}")
        }
    }

    //客户合并，并且同步erp
    fun cstmCombine(originObj: Customer, newObj: Customer, uid: Long) {
        log.info(">>>开始合并,将{${originObj.compName}[${originObj.erpCode},${originObj.ebusiAdminAcctNo}]}合并到{${newObj.compName}[${newObj.erpCode},${newObj.ebusiAdminAcctNo}]}")
        if (StringUtils.isEmpty(originObj.erpCode)){
            log.error(">>>${originObj.compName}erpCode不存在,不能进行合并")
        }else if (StringUtils.isEmpty(newObj.erpCode)){
            log.error(">>>${newObj.compName}erpCode不存在,不能进行合并")
        }else{
            //记录合并信息
            combineRecordSave(originObj, newObj, uid)

            //合并规则
            combineCstRules(originObj,newObj)

            customerRepo.save(originObj)
            crmSync2XyOrErp(originObj)
            sleep(1000);

            customerRepo.save(newObj)
            crmSync2XyOrErp(newObj)
            sleep(1000);

            //同步erp
            val erpMap = HashMap<String, String>()
            erpMap["mq_type"] = "8"
            erpMap["erp_code_old"] = originObj.erpCode!!
            erpMap["erp_code_new"] = newObj.erpCode!!
            mqSenderService.sendMsg(erpMap, "1")
        }
    }

    fun combineCstRules(originObj: Customer, newObj: Customer){
        //原客户停用,有电商编号要合并到新客户,其他不变
        originObj.status = 0
        //客户性质存在老客户则取老客户
        if (originObj.cstmType==1||newObj.cstmType==0){
            newObj.cstmType=1
        }
        //起始时间取早的
        if (null==originObj.startTime||
                (null!=newObj.startTime&&originObj.startTime!!.time<newObj.startTime!!.time)){
            newObj.startTime = originObj.startTime
        }
        //编辑时间取晚的
        if (originObj.updateAt>newObj.updateAt){
            newObj.updateAt = originObj.updateAt
        }
        //创建时间取早的
        if ((null!=originObj.createAt&&null==newObj.createAt)||
                (null!=newObj.createAt&&null!=originObj.createAt&& (originObj.createAt!! <newObj.createAt!!))){
            newObj.createAt = originObj.createAt
        }
        //首张有效提单时间取早的
        if ((null!=newObj.firstBillDate&&null!=originObj.firstBillDate&& originObj.firstBillDate!!.time< newObj.firstBillDate!!.time)
                ||(null==newObj.firstBillDate&&null!=originObj.firstBillDate)){
            newObj.firstBillDate=originObj.firstBillDate
        }
        if ((null!=newObj.firstDeliveryDate&&null!=originObj.firstDeliveryDate&& originObj.firstDeliveryDate!!.time< newObj.firstDeliveryDate!!.time)
                ||(null==newObj.firstDeliveryDate&&null!=originObj.firstDeliveryDate)){
            newObj.firstDeliveryDate=originObj.firstDeliveryDate
        }
        //取正式客户
        if (2!= newObj.mark&&2==originObj.mark){
            newObj.mark = 2
        }
        //取最近的开单时间
        if ((null!=originObj.billDate&&null==newObj.billDate)||
                (null!=newObj.billDate&&null!=originObj.billDate&&originObj.billDate!! >newObj.billDate)){
            newObj.billDate = originObj.billDate
        }
        //联系人合并
        if (null!=originObj.linkers&&null!=newObj.linkers){
            val newLinkerSet = newObj.linkers
            val oriLinkerSet = originObj.linkers
            for (oriLinker in oriLinkerSet){
                if (0==oriLinker.mainStatus){
                    (newLinkerSet as TreeSet).add(oriLinker)
                }
            }
            newObj.linkers = newLinkerSet
        }
        if (originObj.ebusiAdminAcctNo != null) {
            newObj.ebusiAdminAcctNo = originObj.ebusiAdminAcctNo
            newObj.ebusiMemberCode = originObj.ebusiMemberCode
            originObj.ebusiAdminAcctNo = null
            originObj.ebusiMemberCode = null
        }
        else if(null!=originObj.busiRelation){
            //业务关系取没有电商编号的
            val newRelationSet = HashSet<BusiRelation>()
            val oriRelationSet = originObj.busiRelation
            for (oriRelation in oriRelationSet){
                newRelationSet.add(oriRelation)
            }
            newObj.busiRelation = newRelationSet
        }
    }


    //分页查询合并记录
    fun combineRecordList(oldCustName: String?, newCustName: String?, pageable: Pageable) = combRcdRepo.combineRecordList(oldCustName, newCustName, pageable)

    // 型云用户订单情况，保存或更新统计库订单情况
    fun statOrderSave(record: Map<String, String>) {
        val cstm = customerRepo.findByEbusiAdminAcctNo(record["acct_no"]!!)
        if (cstm != null) {
            var cstmOrder = cstOrderRepo.findByOrderNo(record["order_no"]!!)
            if (cstmOrder == null) {
                cstmOrder = CustomerOrder()
                cstmOrder.acctNo = record["acct_no"]!!
                cstmOrder.orderNo = record["order_no"]!!
                cstmOrder.orderTime = Timestamp(record["order_time"]!!.toLong())
            }
            cstmOrder.status = record["status"]!!
            cstmOrder.type = record["type"]!!
            cstmOrder.dptId = cstm.fkDpt.id
            cstmOrder.dptName = cstm.fkDpt.name
            cstmOrder.orgId = cstm.fkDpt.fkOrg.id
            cstmOrder.orgName = cstm.fkDpt.fkOrg.name
            cstmOrder.employeeName = cstm.fkAcct.name
            cstmOrder.crmAcctId = cstm.fkAcct.id!!
            cstOrderRepo.save(cstmOrder)
        } else {
            log.error("当前crm没有该电商账号:>>>${record["acct_no"]}")
        }
    }

    // 型云新增或修改客户并审核通过后同步到crm,然后crm同步到erp
    fun xyCstmUpdate(record: Map<String, String>) {
        val cstm = customerRepo.findByEbusiAdminAcctNo(record["ebusi_admin_acct_no"]!!)
        if (cstm != null) {
            //存在则更新
            log.info("存在该电商账号:>>>${record["ebusi_admin_acct_no"]}>>>开始更新")
            handleCstmUpdate(record, cstm, "型云同步更新")
        } else {
            //不存在则先查客户名称
            val tempCstm = customerRepo.findByCompName(record["comp_name"]!!.trim())
            if (tempCstm != null) {
                log.info("存在线下客户:>>>${record["comp_name"]}>>>开始更新")
                handleCstmUpdate(record, tempCstm, "型云同步更新")//合并
            } else {
                log.info("当前crm没有该电商账号:>>>${record["ebusi_admin_acct_no"]}>>>开始新增,公司名称${record["comp_name"]}")
                val newCstm = Customer()
                handleCstmCreate(record, newCstm, "型云同步新增")
            }
        }
    }

    // erp物资品类销量同步
    fun erpGoodSaleSave(record: Map<String, Any>) {
        val list = record.get("list") as List<Map<String, String>>
        recycleGoodSave(0, list)
    }

    //crm通知erp新增客户，erp成功后返回erpCode给crm
    fun erpCodeSave(record: Map<String, String>) {
        val id = record["id"]!!.toLong()
        var cstm = customerRepo.findOne(id)
        if (cstm == null) {
            cstm = customerRepo.findByCompName(record["comp_name"]!!)
            if (cstm == null) {
                log.info("crm by name still null:>>${record["comp_name"]}; id:>>${record["id"]}")
            }
        }
        log.info("id is:${id}")
        if (record["errCount"] != null && record["errCount"]!!.toInt() == 5) {
            log.info("[erp同步]5次没有找到crm客户;${record["comp_name"]}, id:>>${record["id"]}")
            commUtil.errLogSave("[erp同步]当前crm没有该客户:>>>${record["comp_name"]},无法同步erpCode")
        } else {
            if (cstm != null) {
                cstm.erpCode = record["erp_code"]
                customerRepo.save(cstm)
            } else {
                log.info("cstm null:>>>; id:>>$id")
                val r = record as HashMap<String, String>
                if (r["errCount"] == null) {
                    r["errCount"] = "1"
                } else {
                    r["errCount"] = (r["errCount"]!!.toInt() + 1).toString()
                }
                commUtil.errLogSave("[erp同步]当前crm没有该客户:>>>${record["comp_name"]},无法同步erpCode")
                log.info("再次调用一次")
                erpCodeSave(r)
            }
        }
    }

    // 型云下单成功就更新客户billDate
    fun xyCstmOrderSave(record: Map<String, String>) {
        val cstm = customerRepo.findByEbusiAdminAcctNo(record["acct_no"]!!)
        if (cstm != null) {
            handleXyCstmOrder(cstm, record)
        } else {
            commUtil.errLogSave("[xy同步]当前crm没有该电商账号:>>>${record["acct_no"]},无法更新开单时间")
        }
    }

    // erp线下下单成功就更新客户billDate
    fun erpCstmOrderSave(record: Map<String, String>) {
        when (record["mark"]!!.toInt()) {
            0 -> handleErpNewCustomerOrder(record)
            1 -> {
                handleErpNewCustomerOrder(record)
                handleErpOldCustomerOrder(record)
            }
            else -> handleErpOldCustomerOrder(record)
        }
    }

    //erp线下第一次出库
    fun erpCstFirstOrder(record: Map<String, String>) {
        val newErpCode = record["erp_code"] ?: error("erp_code不存在")
        val cst = customerRepo.findByErpCode(newErpCode)
        log.info("erp_code is:${newErpCode}")
        var crmChange = false
        if (cst != null) {
            //先与主单位保持一致
            var hasMain = false
            if (null != cst.mainCstm && cst.mainCstm != cst) {
                cst.startTime = cst.mainCstm!!.startTime
                cst.cstmType = cst.mainCstm!!.cstmType
                hasMain = true
            }
            if (cst.cstmType == 0 && cst.startTime != null) {
                val mark = record["mark"] ?: error("出库标记为空")
                if (mark == "0") {
                    //新客户第一次开单时，更新起始时间
                    val billDate = Timestamp((record["bill_date"] ?: error("开单时间为空")).toLong())
                    log.info(">>>>>startTime:${cst.startTime},billDate:$billDate")
                    cst.startTime = billDate
                    crmChange = true
                } else {
                    //新客户首张单据出库弃审时，还原起始时间
                    val firstDate = record["bill_date"]
                    if (StringUtils.isEmpty(firstDate)) {
                        cst.startTime = cst.createAt
                        crmChange = true
                    } else if (firstDate!!.toLong() > cst.startTime!!.time) {
                        cst.startTime = Timestamp(firstDate!!.toLong())
                        crmChange = true
                    }
                }
            }
            if (null == cst.startTime) {//更新过pro_insert_customer_first_date后，从未开过单
                val mark = record["mark"] ?: error("出库标记为空")
                if (mark == "0") {
                    val billDate = Timestamp((record["bill_date"] ?: error("开单时间为空")).toLong())
                    log.info(">>>>>第一次出库billDate:$billDate")
                    if (cst.cstmType == 0) {
                        cst.startTime = billDate
                    } else {
                        cst.startTime = Timestamp(billDate.time - (365 + 2) * 24 * 3600 * 1000L)//+2防止闰年
                    }
                    crmChange = true
                }
            }

            //首次出库时间
            val mark = record["mark"]
            if (null != mark) {
                if ("0" == mark) {
                    if (null == cst.firstDeliveryDate) {
                        cst.firstDeliveryDate = Timestamp(Date().time)
                    }
                } else if ("1" == mark) {
                    val firstDate = record["bill_date"]
                    if (null != cst.firstBillDate && null == firstDate &&
                            //目前只能提供2020年的开单数据
                            cst.firstBillDate!! > Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01").time)) {
                        cst.firstDeliveryDate = null
                    }
                }
            }

            //同步erp或xy
            if (crmChange) {
                crmSync2XyOrErp(cst)
            }
            //更新相关单位
            updateMainAndFollow(cst, crmChange, hasMain)
            customerRepo.save(cst)
        } else {
            commUtil.errLogSave("[erp同步]当前crm没有该erp编号:>>>${newErpCode},无法更新起始时间")
        }
    }

    //更新主体和下属相关单位
    fun updateMainAndFollow(cst:Customer,crmChange:Boolean,hasMain:Boolean){
        var mainCstId = cst.id
        if (hasMain){
            mainCstId = cst.mainCstm!!.id
        }
        customerRepo.updateFollowCst(mainCstId!!, cst.cstmType, cst.startTime!!, cst.billDate!!,cst.mark)
        val follower = customerRepo.findFollowCst(mainCstId, cst.id)
        if (follower.isNotEmpty()) {
            for (item in follower){
                item.startTime = cst.startTime
                item.cstmType = cst.cstmType
                item.billDate = cst.billDate
                item.mark = cst.mark
                if (crmChange){
                    crmSync2XyOrErp(item)
                }
            }
        }
    }


    //单个拜访延时发送
    fun msgSaveAndSend(callId: Long) {
        log.info(">>>单个拜访延时发送开始,callId:$callId")
        val obj = cstCallRepo.findOne(callId)
        if (obj.status == 0) {
            val str = callId.toString() + "|" + obj.customer.compName
            val msgInfo = "您有一条拜访即将超时，点击查看拜访计划"
            log.info(">>>用户${obj.creator.id}：$msgInfo")
            val msg = Message()
            //type = 0 status = 0
            msg.contentType = 1
            msg.content = str
            msg.acctCode = obj.creator.id.toString()
            msg.msgInfo = msgInfo
            msgRepo.save(msg)

            //提醒用户
            val mobileList = mobInfoRepo.findByAcctId(obj.creator.id!!)
            if (mobileList != null && mobileList.size > 0) {
                mobileList.map { mi ->
                    avPushService.send2Persion(msg.msgInfo, if (mi.type == 0) "ios" else "android", mi.deviceNum!!)
                }
            }
        }
    }

    //所有拜访延时发送
    fun msgSend2Person(msgId: Long) {
        log.info(">>>所有拜访延时发送开始,msgId:$msgId")
        val obj = msgRepo.findOne(msgId)
        if (obj != null) {
            //提醒用户
            val mobileList = mobInfoRepo.findByAcctId(obj.acctCode.toLong())
            if (mobileList != null && mobileList.size > 0) {
                mobileList.map { mi ->
                    avPushService.send2Persion(obj.msgInfo, if (mi.type == 0) "ios" else "android", mi.deviceNum!!)
                }
            }
        }
    }

    //2019-01-18 统一私有方法位置
    private fun combineRecordSave(oldCust: Customer, newCust: Customer, uid: Long) {
        val combine = CombineRecord()
        combine.oldCustId = oldCust.id
        combine.oldCustName = oldCust.compName
        combine.oldCustErpCode = oldCust.erpCode
        combine.oldCustEbusiCode = oldCust.ebusiAdminAcctNo
        combine.newCustId = newCust.id
        combine.newCustName = newCust.compName
        combine.newCustErpCode = newCust.erpCode
        combine.newCustEbusiCode = newCust.ebusiAdminAcctNo
        combine.operator = acctRepo.findOne(uid)
        combRcdRepo.save(combine)
    }

    private fun getBehaviorColumnName(key: String): String {
        val keyArr = key.split("_")
        var modifyArr = arrayOf("set")
        keyArr.indices.map { i ->
            val modKey = keyArr[i].substring(0, 1).toUpperCase() + keyArr[i].substring(1)
            modifyArr = modifyArr.plus(modKey)
        }
        return modifyArr.joinToString("")
    }

//    private fun handleErpCstmOrder(cstm: Customer, record: Map<String, String>) {
//        val mqMark = record["mark"]!!.toInt()
//        if (mqMark == 1) {//改单
//            val billDate = Timestamp(record["bill_date"]!!.toLong())
//            log.info(">>>>>old billDate:${cstm.billDate},new billDate:$billDate")
//            if (billDate > cstm.billDate) {//传过来的时间最新就替换
//                cstm.billDate = billDate
//            }
//        } else {
//            cstm.billDate = Timestamp(record["bill_date"]!!.toLong())
//        }
//        log.info(">>>>>ErpCstmOrder billDate:${cstm.billDate}")
//        if (cstm.mark == 3) {
//            //如果是公共客户,需要转为正式客户,此时不需要完善信息校验
//            log.info(">>>>>comCstm:${cstm.compName} transforms into regCstm")
//            cstm.mark = 2
//            cstm.convertDate = Timestamp(Date().time)//转化时间
//            //保存到记录表,转化人默认为超级管理员
//            val cstmRecord = CustomerRecord()
//            cstmRecord.fkCustom = cstm
//            cstmRecord.type = 4
//            cstmRecord.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
//            cstmRedPepo.save(cstmRecord)
//        }
//        customerRepo.save(cstm)
//    }

    private fun handleErpNewCustomerOrder(record: Map<String, String>) {
        val newErpCode = record["new_erp_code"] ?: error("new_erp_code不存在")
        val cst = customerRepo.findByErpCode(newErpCode)
        log.info("erp_code is:${newErpCode}")
        var crmChange = false
        if (cst != null) {
            //先与主单位保持一致
            var hasMain = false
            if (null != cst.mainCstm && cst.mainCstm != cst) {
                cst.startTime = cst.mainCstm!!.startTime
                cst.cstmType = cst.mainCstm!!.cstmType
                hasMain = true
            }
            val billDate = Timestamp((record["new_bill_date"] ?: error("开单时间为空")).toLong())
            log.info(">>>>>old billDate:${cst.billDate},new billDate:$billDate")
            //新创建的老客户当做新抬头时，要注意修改起始时间，防止变为新客户
            //取这个客户最早开单时间
            val firstBillDate = record["new_first_bill_date"]
            if (cst.cstmType == 1 && cst.startTime != null) {
                //新提单是较早的提单，且会把这个客户计算成新客户
                if ((null == cst.billDate || billDate < cst.billDate) && (billDate!!.time - cst.startTime!!.time) <= (365 * 24 * 3600 * 1000L)
                        //一年之内没开过单，说明是刚创建就是老客户,属于老客户换抬头开票的情况
                        && (StringUtils.isEmpty(firstBillDate) || (firstBillDate!!.toLong() - cst.startTime!!.time) > (365 * 24 * 3600 * 1000L))) {
                    cst.startTime = Timestamp(billDate.time - (365 + 2) * 24 * 3600 * 1000L)//+2防止闰年
                    crmChange = true
                }
            } else if (cst.cstmType == 0) {
                var deliveryState = record["deliveryState"]
                if (StringUtils.isEmpty(deliveryState)) {
                    deliveryState = "0"
                }
                if ("1" == deliveryState && (StringUtils.isEmpty(firstBillDate) || firstBillDate!!.toLong() >= billDate.time)) {
                    cst.startTime = billDate
                    crmChange = true
                } else if (StringUtils.isNotEmpty(firstBillDate)) {
                    cst.startTime = Timestamp(firstBillDate!!.toLong())
                    crmChange = true
                }
            }
            //传过来的时间最新就替换
            if (null == cst.billDate || billDate > cst.billDate) {
                cst.billDate = billDate
            }
            log.info(">>>>>ErpCstmOrder billDate:${cst.billDate}")
            //记录第一次开单时间
            if (null == cst.firstBillDate || billDate < cst.firstBillDate) {
                cst.firstBillDate = billDate
                log.info(">>记录客户${cst.erpCode}第一次开单时间${cst.firstBillDate}")
            }
            //新增的开单时间在90天内才改为正式客户
            if (cst.mark == 3 && (Date().time-cst.billDate!!.time) < (90 * 24 * 3600 * 1000L)) {
                //如果是公共客户,需要转为正式客户,此时不需要完善信息校验
                log.info(">>>>>comCstm:${cst.compName} transforms into regCstm")
                if (cst.transType == 0) {//标记为自动转化
                    cst.transType = 1
                }
                cst.mark = 2
                cst.convertDate = Timestamp(Date().time)//转化时间
                //保存到记录表,转化人默认为超级管理员
                val cstRecord = CustomerRecord()
                cstRecord.fkCustom = cst
                cstRecord.type = 4
                cstRecord.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
                cstRecord.reason = "Erp开单公共转正式，开单日期${shortSdf.format(cst!!.billDate)}"
                cstmRedPepo.save(cstRecord)
            }
            //同步erp或xy
            if (crmChange) {
                crmSync2XyOrErp(cst)
            }
            //更新相关单位
            updateMainAndFollow(cst, crmChange , hasMain)
            customerRepo.save(cst)
        } else {
            commUtil.errLogSave("[erp同步]当前crm没有该erp编号:>>>${newErpCode},无法更新开单时间")
        }

    }

    private fun handleErpOldCustomerOrder(record: Map<String, String>) {
        val oldErpCode = record["old_erp_code"] ?: error("old_erp_code不存在")
        val cst = customerRepo.findByErpCode(oldErpCode)
        log.info("erp_code is:${oldErpCode}")
        var crmChange = false
        if (cst != null) {
            //先与主单位保持一致
            var hasMain = false
            if (null != cst.mainCstm && cst.mainCstm != cst) {
                cst.startTime = cst.mainCstm!!.startTime
                cst.cstmType = cst.mainCstm!!.cstmType
                hasMain = true
            }
            //清空首次开单时间
            if (null != cst.firstBillDate && cst.firstBillDate == cst.billDate) {
                cst.firstBillDate = null
            }
            if (StringUtils.isNotEmpty(record["old_bill_date"])) {
                val billDate = Timestamp((record["old_bill_date"] ?: error("")).toLong())
                log.info(">>>>>last billDate:${cst.billDate},now billDate:$billDate")
                cst.billDate = billDate
            } else {
                cst.billDate = cst.createAt
            }
            //新客户起始时间回退,取最早时间
            if (cst.cstmType == 0) {
                if (StringUtils.isNotEmpty(record["old_first_bill_date"])) {
                    val firstBillDate = Timestamp((record["old_first_bill_date"] ?: error("")).toLong())
                    if (firstBillDate > cst.startTime) {
                        cst.startTime = firstBillDate
                        crmChange = true
                    }
                } else {
                    cst.startTime = cst.createAt
                    crmChange = true
                }
            }
            log.info(">>>>>ErpCstmOrder billDate:${cst.billDate}")
            if (cst.mark == 2 && cst.transType == 1) {
                //未开单或者上次开单时间再90天之前,如果是正式客户,需要转为公共客户
                if (StringUtils.isEmpty(record["old_bill_date"]) || (Date().time - cst.billDate!!.time) >= (90 * 24 * 3600 * 1000L)) {
                    log.info(">>>>>comCstm:${cst.compName} transforms into regCstm")
                    cst.mark = 3
                    cst.convertDate = Timestamp(Date().time)//流失时间
                    cst.transType = 0
                    //保存到记录表,转化人默认为超级管理员
                    val cstRecord = CustomerRecord()
                    cstRecord.fkCustom = cst
                    cstRecord.type = 5
                    cstRecord.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
                    cstRecord.reason = "提单修改流失"
                    cstmRedPepo.save(cstRecord)
                }
            }
            //同步erp或xy
            if (crmChange) {
                crmSync2XyOrErp(cst)
            }
            //更新相关单位
            updateMainAndFollow(cst, crmChange, hasMain)
            customerRepo.save(cst)
        } else {
            commUtil.errLogSave("[erp同步]当前crm没有该erp编号:>>>${oldErpCode},无法更新开单时间")
        }

    }

    private fun handleXyCstmOrder(cstm: Customer, record: Map<String, String>) {
        cstm.billDate = Timestamp(record["bill_date"]!!.toLong())
        log.info(">>>>>XyCstmOrder billDate:${cstm.billDate}")
        if (cstm.mark == 3) {
            //如果是公共客户,需要转为正式客户,此时不需要完善信息校验
            log.info(">>>>>comCstm:${cstm.compName} transforms into regCstm")
            cstm.mark = 2
            cstm.convertDate = Timestamp(Date().time)//转化时间
            //保存到记录表,转化人默认为超级管理员
            val cstmRecord = CustomerRecord()
            cstmRecord.fkCustom = cstm
            cstmRecord.type = 4
            cstmRecord.reason = "型云开单公共转正式,开单时间>>${shortSdf.format(cstm.billDate)}"
            cstmRecord.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
            cstmRedPepo.save(cstmRecord)
        }
        customerRepo.save(cstm)
    }

    //型云修改同步
    private fun handleCstmUpdate(record: Map<String, String>, oldCstm: Customer, remark: String) {
        val link = Linker()
        //不处理的字段
        val excludeArr = arrayOf("mq_type", "dpt_name", "acct_name", "acct_erp_code", "linker_name", "linker_phone", "set_up_date", "register_capital", "comp_prov", "comp_city", "region", "comp_name")
        //修改记录字段
        val cstmMap: Map<String, String> = mapOf("comp_name" to "公司名称", "busi_license_code" to "工商证照编码", "comp_addr" to "公司地址-详情", "comp_prov" to "公司地址-省", "comp_city" to "公司地址-市", "comp_area" to "公司地址-区", "tfn" to "税号", "open_bank" to "开户银行", "open_acct_name" to "开户名称", "open_acct" to "开户账号", "bill_addr" to "开票地址")
        //循环时每单独处理一个字段，都需要在excludeArr中加上
        record.keys.map { key ->
            if ("linker_name" == key) {
                log.info("colName is linker_name, value is ${record[key]}")
                //联系人
                link.name = record[key]!!
            } else if ("linker_phone" == key) {
                log.info("colName is linker_phone, value is ${record[key]}")
                link.phone = record[key]!!
            } else if ("dpt_name" == key) {
                log.info("colName is dpt_name, value is ${record[key]}")
                //业务部门
                if (record[key].isNullOrBlank()) {
                    oldCstm.fkDpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                } else {
                    var dpt = dptRepo.findByName(record[key]!!)
                    if (dpt == null) dpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                    handleDptMdy(oldCstm, dpt!!.id, oldCstm, 1, remark)
                    oldCstm.fkDpt = dpt!!
                }
            } else if ("acct_erp_code" == key) {
                log.info("colName is acct, value is ${record[key]}")
                //业务员
                if (record[key] != "") {
                    val acct = acctRepo.findByPlatformCode(record[key]!!)
                    handleAcctMdy(oldCstm, acct!!.id, oldCstm, 1, remark)
                    oldCstm.fkAcct = acct
                }
            } else if ("set_up_date" == key) {
                log.info("colName is set_up_date, value is ${record[key]}")
                //成立时间
                oldCstm.setUpDate = if (record[key] == "") null else Timestamp(record[key]!!.toLong())
            } else if ("register_capital" == key) {
                log.info("colName is register_capital, value is ${record[key]}")
                //注册资本
                oldCstm.registerCapital = record[key]
            } else if ("comp_prov" == key) {
                //xy传过来没有省后缀，需处理，区不需要额外处理
                oldCstm.compProv = commUtil.handleXYCompProv(record[key]!!)
                log.info("colName is comp_prov, value is ${oldCstm.compProv}")
            } else if ("comp_city" == key) {
                //xy传过来没有市后缀，需处理,区不需要额外处理
                oldCstm.compCity = commUtil.handleXYCompCity(record[key]!!)
                log.info("colName is comp_city, value is ${oldCstm.compCity}")
            } else if ("region" == key) {
                //xy城市名没有市后缀，需处理
                oldCstm.region = if (record[key] == "") null else commUtil.handleRegion(record[key]!!)
                log.info("colName is region, value is ${oldCstm.region}")
            } else if ("comp_name" == key) {
                //统计名称如果和原名称一致，则需要同步修改
                if (oldCstm.publicCompName == oldCstm.compName) oldCstm.publicCompName = record[key]!!
                oldCstm.compName = record[key]!!
                log.info("colName is comp_name, value is ${oldCstm.compName};publicCompName is ${oldCstm.publicCompName}")
            } else if (!excludeArr.contains(key)) {
                val methodName = commUtil.getColumnMethodName(key)
                log.info("colName is $key, value is ${record[key]}")
                if (record[key] != null && record[key] != "") {
                    val m = oldCstm.javaClass.getMethod("set$methodName", String::class.java)
                    val om = oldCstm.javaClass.getMethod("get$methodName")
                    if (cstmMap.containsKey(key)) {
                        val oldValue = if (om.invoke(oldCstm) == null || om.invoke(oldCstm) == "") " " else om.invoke(oldCstm) as String
                        val newValue = record[key]!!
                        log.info(">>>>>methodName:>>$methodName>>>>>oldValue:>>$oldValue>>>>newValue:>>$newValue")
                        if (oldValue.trim() != newValue.trim()) { //kt字符串可以这样比较
                            saveCstModify(oldValue, newValue, cstmMap[key]!!, oldCstm, 1, remark)
                        }
                    }
                    m.invoke(oldCstm, record[key])
                }
            }
        }
        //联系人更新记录到表中
        handleLinkerMdy(oldCstm.id!!, oldCstm, 1, link, remark)
        //修改联系人
        crmUpdateLink(link, oldCstm)
        //保存银行信息
        val bank = bkIfRepo.findBankInfo(oldCstm.id)
        bank!!.bankAcct = record["open_acct"]!!
        bank.openBank = record["open_bank"]!!
        bank.name = record["open_acct_name"]!!
        bkIfRepo.save(bank)
        //保存客户
        oldCstm.billAddr = oldCstm.compAddr
        oldCstm.billProv = oldCstm.compProv
        oldCstm.billCity = oldCstm.compCity
        oldCstm.billArea = oldCstm.compArea
        // 合并，变正式客户
        if (oldCstm.mark != 3) oldCstm.mark = 2
        if (oldCstm.unitProperty.isNullOrBlank()) {
            oldCstm.unitProperty = "1,2"
            oldCstm.depositRate = "20"
        }

        //查看是否重名
        val cstList = customerRepo.findOtherSameName(oldCstm.compName, oldCstm.id!!)
        if (cstList.isNotEmpty()){
            var index = 2
            for (cst in cstList){
                cst.compName = oldCstm.compName+index.toString()
                index++
                log.info("型云修改客户名同步crm时客户名重复，crm进行客户合并>>>>["+cst.id+"]->["+oldCstm.id+"]")
                cstmCombine(cst,oldCstm,1)
            }
        }else{
            //同步erp
            val obj = customerRepo.save(oldCstm)//会保存联系人
            log.info("handleCstmUpdate successed>>>>")
            //修改同步erp
            val erpArr = arrayOf("CompName", "CompNameAb", "MemberCode", "EbusiMemberCode", "EbusiAdminAcctNo", "LegalRept", "FaxNum", "Region", "Tfn", "OpenBank", "OpenAcct", "CompAddr", "CompProv", "CompCity", "CompArea", "WorkgroupName")
            val erpMap = HashMap<String, String>() //型云新增需要同步给erp（只提供xy和erp的公共字段）
            erpArr.map { name ->
                val m = obj.javaClass.getMethod("get$name")
                erpMap[commUtil.underscoreName(name)] = if (m.invoke(obj) != null) m.invoke(obj).toString() else ""
            }
            erpMap["mq_type"] = "5"
            erpMap["bill_addr"] = obj.compProv + obj.compCity + obj.compArea + obj.compAddr
            // crm没有erpcode需要让erp新增
            if (oldCstm.erpCode == null) {
                log.info(">>>>型云合并客户，新增")
                erpMap["erp_code"] = ""
                erpMap["id"] = obj.id.toString()
            } else {
                erpMap["erp_code"] = obj.erpCode!!
            }
            val list = ArrayList<String>()
            obj.busiRelation.map { bs ->
                list.add(bs.name)
            }
            erpMap["busi_relation"] = list.joinToString(separator = ",")
            erpMap["cstm_property"] = obj.fkCustomProperty.name
            erpMap["dpt_name"] = obj.fkDpt.name
            erpMap["acct_name"] = obj.fkAcct.name
            erpMap["acct_erp_code"] = obj.fkAcct.platformCode!!
            erpMap["linker_phone"] = link.phone
            erpMap["linker_name"] = link.name
            erpMap["cstm_type"] = obj.cstmType.toString()
            erpMap["start_time"] = if (obj.startTime == null) "" else shortSdf.format(obj.startTime)
            erpMap["cstm_status"] = ""
            mqSenderService.sendMsg(erpMap, "1")
            log.info("sendMsg to erp successed>>>>")
        }
    }

    //型云新增同步
    private fun handleCstmCreate(record: Map<String, String>, cstm: Customer, remark: String) {
        val linker = Linker()
        //不处理的字段
        val excludeArr = arrayOf("mq_type", "dpt_name", "acct_name", "acct_erp_code", "linker_name", "linker_phone", "set_up_date", "register_capital", "comp_prov", "comp_city", "region")
        //循环时每单独处理一个字段，都需要在excludeArr中加上
        record.keys.map { key ->
            if ("linker_name" == key) {
                log.info("colName is linker_name, value is ${record[key]}")
                //联系人
                linker.name = record[key]!!
            } else if ("linker_phone" == key) {
                log.info("colName is linker_phone, value is ${record[key]}")
                linker.phone = record[key]!!
            } else if ("dpt_name" == key) {
                log.info("colName is dpt_name, value is ${record[key]}")
                //业务部门
                if (record[key].isNullOrBlank()) {
                    cstm.fkDpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                } else {
                    var dpt = dptRepo.findByName(record[key]!!)
                    if (dpt == null) dpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                    cstm.fkDpt = dpt!!
                }
            } else if ("acct_erp_code" == key) {
                log.info("colName is acct, value is ${record[key]}")
                //业务员
                if (record[key].isNullOrBlank()) {
                    cstm.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
                } else {
                    val acct = acctRepo.findByPlatformCode(record[key]!!)
                    cstm.fkAcct = acct!!
                }
            } else if ("set_up_date" == key) {
                log.info("colName is set_up_date, value is ${record[key]}")
                //成立时间
                cstm.setUpDate = if (record[key] == "") null else Timestamp(record[key]!!.toLong())
            } else if ("register_capital" == key) {
                log.info("colName is register_capital, value is ${record[key]}")
                //注册资本
                cstm.registerCapital = record[key]
            } else if ("comp_prov" == key) {
                //xy传过来没有省后缀，需处理
                cstm.compProv = commUtil.handleXYCompProv(record[key]!!)
                log.info("colName is comp_prov, value is ${cstm.compProv}")
            } else if ("comp_city" == key) {
                //xy传过来没有市后缀，需处理
                cstm.compCity = commUtil.handleXYCompCity(record[key]!!)
                log.info("colName is comp_city, value is ${cstm.compCity}")
            } else if ("region" == key) {
                //xy城市名没有市后缀，需处理
                cstm.region = if (record[key] == "") null else commUtil.handleRegion(record[key]!!)
                log.info("colName is region, value is ${cstm.region}")
            } else if (!excludeArr.contains(key)) {
                val methodName = commUtil.getColumnMethodName(key)
                val m = cstm.javaClass.getMethod("set$methodName", String::class.java)
                m.invoke(cstm, if (record[key].isNullOrBlank()) "" else record[key])
                log.info("colName is $key, value is ${record[key]}")
            }
        }
        //新增联系人
        handleLinkCreate(cstm, linker, CrmConstants.DEFAULT_ACCT_ID)
        //保存
        val busiRelation = HashSet<BusiRelation>()
        busiRelation.add(busiRelationRepo.findOne(CrmConstants.DEFAULT_BUSI_RELATION_ID))//默认客户
        cstm.busiRelation = busiRelation
        cstm.fkCustomProperty = customPropertyRepo.findByName("其他")
        cstm.createAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
        cstm.procurementGoods = HashSet<SupplyCatalog>()
        cstm.procurementPurpose = HashSet<Purpose>()
        cstm.hopeAddGoods = HashSet<SupplyCatalog>()
        cstm.dealGoods = HashSet<SupplyCatalog>()
        cstm.dealPurpose = HashSet<Purpose>()
        cstm.processRequirement = HashSet<ProcessRequirement>()
        cstm.industry = HashSet<Industry>()
        cstm.billAddr = cstm.compAddr
        cstm.billProv = cstm.compProv
        cstm.billCity = cstm.compCity
        cstm.billArea = cstm.compArea
        cstm.customerSource = "型云"
        cstm.startTime = Timestamp(Date().time)//型云新建取创建日期
        //保存银行信息
        val bankInfo = handleBankInfo(cstm)
        bankInfo.fkCstm = cstm
        bkIfRepo.save(bankInfo)
        //xy过来的就是正式客户
        cstm.mark = 2
        cstm.publicCompName = cstm.compName //默认和客户名称一致
        cstm.convertDate = Timestamp(Date().time) //转化时间
        cstm.billDate = Timestamp(Date().time) //开单日期
        // 新增客户单位性质和定金默认比例
        cstm.unitProperty = "1,2"
        cstm.depositRate = "20"
        var obj = customerRepo.save(cstm)
        obj.mainCstm = obj
        obj = customerRepo.save(obj)
        log.info(">>>>handleCstmCreate successed>>>>")
        //新增同步erp,新增需要传cstmId
        val erpArr = arrayOf("CompName", "CompNameAb", "MemberCode", "EbusiMemberCode", "EbusiAdminAcctNo", "LegalRept", "FaxNum", "Region", "Tfn", "OpenBank", "OpenAcct", "CompAddr", "CompProv", "CompCity", "CompArea", "WorkgroupName")
        val erpMap = HashMap<String, String>() //型云新增需要同步给erp（只提供xy和erp的公共字段）
        erpArr.map { name ->
            val m = obj.javaClass.getMethod("get$name")
            erpMap[commUtil.underscoreName(name)] = if (m.invoke(obj) != null) m.invoke(obj).toString() else ""
        }
        erpMap["mq_type"] = "5"
        erpMap["erp_code"] = ""
        erpMap["bill_addr"] = obj.compProv + obj.compCity + obj.compArea + obj.compAddr
        erpMap["id"] = obj.id.toString()
        erpMap["busi_relation"] = CrmConstants.DEFAULT_BUSI_RELATION
        erpMap["cstm_property"] = CrmConstants.DEFAULT_CUSTOM_PROPERTY
        erpMap["dpt_name"] = obj.fkDpt.name
        erpMap["acct_name"] = obj.fkAcct.name
        erpMap["acct_erp_code"] = obj.fkAcct.platformCode!!
        erpMap["linker_phone"] = linker.phone
        erpMap["linker_name"] = linker.name
        erpMap["cstm_type"] = obj.cstmType.toString()
        erpMap["start_time"] = if (obj.startTime == null) "" else shortSdf.format(obj.startTime)
        erpMap["cstm_status"] = ""
        mqSenderService.sendMsg(erpMap, "1")
        log.info(">>>>sendMsg to erp successed>>>>")
        //型云新增不存在重名需要合并
    }

    //crm新增或修改客户同步
    private fun handelCstmSyncMap(obj: Customer, array: Array<String>, type: Int): HashMap<String, String> {
        val syncMap = HashMap<String, String>()
        val link = linkerRepo.findMainLink(obj.id)
        if (type == 1) { //erp单独需要的字段
            val list = ArrayList<String>()
            obj.busiRelation.map { s ->
                list.add(s.name)
            }
            val str = Joiner.on(",").join(list)
            syncMap["busi_relation"] = str
            syncMap["cstm_property"] = obj.fkCustomProperty.name
            //linker_name,linker_phone
            syncMap["linker_name"] = link.name
            syncMap["linker_phone"] = link.phone
            //2019-01-17 客户类型,起始日期需要同步给erp
            syncMap["cstm_type"] = obj.cstmType.toString()
            syncMap["start_time"] = if (obj.startTime == null) "" else shortSdf.format(obj.startTime)
            /**
             * 关联主体名字
             * @author samy
             * @date 2020/01/08
             */
            syncMap["linkCompName"] = obj.mainCstm!!.compName!!
            syncMap["cstm_status"] = if (obj.ebusiAdminAcctNo.isNullOrBlank()) obj.status.toString() else ""
        } else { //xy单独需要的字段
            if (obj.setUpDate == null) syncMap["set_up_date"] = ""
            else {
                val date = obj.setUpDate as Timestamp
                syncMap["set_up_date"] = date.time.toString()
            }
            syncMap["register_capital"] = if (obj.registerCapital == null) "" else obj.registerCapital!!
            log.info(">>>>register_capital:${obj.registerCapital}")
            //linker_name
            syncMap["linker_name"] = link.name
        }
        //erp和xy都需要的字段
        syncMap["dpt_name"] = obj.fkDpt.name
        //acct_name
        syncMap["acct_name"] = obj.fkAcct.name
        syncMap["acct_erp_code"] = if (obj.fkAcct.platformCode == null) "" else obj.fkAcct.platformCode!!
        //处理其他字段
        array.map { name ->
            val m = obj.javaClass.getMethod("get$name")
            syncMap[commUtil.underscoreName(name)] = if (m.invoke(obj) != null) m.invoke(obj).toString().trim() else ""
        }
        return syncMap
    }

    //正式或公共客户修改需要同步xy或erp
    fun crmSync2XyOrErp(obj: Customer, noteErp: Boolean = true) {
        if (obj.mark != 1) {
            if (!obj.ebusiAdminAcctNo.isNullOrBlank()) { //电商编号不为空,空字符串,空格字符串则同步型云
                //xy同步字段
                val xyArr = arrayOf("CompName", "CustomerChannel", "EbusiMemberCode", "EbusiAdminAcctNo", "BusiLicenseCode", "LegalRept", "CompAddr", "CompProv", "CompCity", "CompArea", "FaxNum", "CompSize", "CompType", "Region", "FactController", "FactControllerIdno", "Tfn", "OpenAcctName", "OpenBank", "OpenAcct")
                val map = handelCstmSyncMap(obj, xyArr, 2)
                map["mq_type"] = "4"
                mqSenderService.sendMsg(map, "2")
            }
            if (!obj.erpCode.isNullOrBlank() && noteErp) { //erp编号不为空,空字符串,空格字符串则同步型云
                //erp同步字段
                val erpArr = arrayOf("CompName", "CompNameAb", "MemberCode", "EbusiMemberCode", "EbusiAdminAcctNo", "LegalRept", "FaxNum", "Region", "Tfn", "OpenBank", "OpenAcct", "CompAddr", "CompProv", "CompCity", "CompArea", "WorkgroupName")
                val map = handelCstmSyncMap(obj, erpArr, 1)
                map["mq_type"] = "5"
                /**
                 * 主体客户
                 * @date 2020/01/08
                 * @author samy
                 */
                map["linkCompName"] = obj.mainCstm!!.compName!!
                map["linkCompCode"] = obj.mainCstm!!.erpCode!!
                map["bill_addr"] = if (obj.billAddr == null) "" else obj.billAddr!!
                map["erp_code"] = obj.erpCode!!
                mqSenderService.sendMsg(map, "1")
            }
        }
    }

    //专门用于客户更新，在更新时将部分字段保存到客户修改表
    private fun autoSetClass(newMod: Customer, oldMod: Customer, excludeFieldNames: Array<String> = arrayOf("id", "updateAt", "createAt"), cstmMap: Map<String, String>, uid: Long, remark: String): Any {
        newMod.javaClass.declaredFields.map { f ->
            val methodName = f.name.substring(0, 1).toUpperCase() + f.name.substring(1)
            if (!excludeFieldNames.contains(f.name)) {
                val om = newMod.javaClass.getMethod("get$methodName")
                val tm = oldMod.javaClass.getMethod("get$methodName")
                val m = oldMod.javaClass.getMethod("set$methodName", f.type)
                if (cstmMap.containsKey(methodName)) {//针对String类型
                    val oldValue = if (tm.invoke(oldMod) != null) tm.invoke(oldMod) as String else " "
                    val newValue = if (om.invoke(newMod) != null) om.invoke(newMod) as String else " "
                    log.info(">>>>>methodName:>>$methodName>>>>>oldValue:>>$oldValue>>>>newValue:>>$newValue")
                    if (oldValue.trim() != newValue.trim()) { //kt字符串可以这样比较
                        saveCstModify(oldValue, newValue, cstmMap[methodName]!!, newMod, uid, remark)
                    }
                }
                m.invoke(oldMod, om.invoke(newMod))
            }
        }
        return oldMod
    }

    //修改联系人
    fun crmUpdateLink(newLink: Linker, oldCstm: Customer) {
        val newName = newLink.name
        val newPhone = newLink.phone
        val oldLink = linkerRepo.findMainLink(oldCstm.id)
        if (oldLink.phone != newLink.phone) {//主联系人电话不一致，以xy的为准，crm原主联系人变为副联系人
            //处理新联系人
            val newObj = commUtil.autoSetClass(oldLink, newLink, arrayOf("id", "updateAt", "createAt", "customers")) as Linker
            newObj.name = newName
            newObj.phone = newPhone
            //修改旧联系人主副关系
            oldLink.mainStatus = 0
            //修改关联关系
            val linkerSet = oldCstm.linkers as MutableSet<Linker>
            linkerSet.add(newObj)
            oldCstm.linkers = linkerSet
        } else {//一致则修改名称
            oldCstm.linkers.map { s ->
                if (s.mainStatus == 1) {
                    s.name = newName
                }
            }
        }
    }

    //联系人修改记录到linkerModify和cstmModify
    private fun handleLinkModify(oldCstmId: String?, newCstmId: Long, oldLink: Linker, newLink: Linker, uid: Long, source: String? = null) {
        val cstm = customerRepo.findOne(newCstmId)
        var sourceDes = ""
        when (source) {
            "xy" -> sourceDes = ",来源：型云发起"
            else -> ""
        }
        //客户名称
        if (oldCstmId != null) saveLinkModify(newLink, customerRepo.findOne(oldCstmId.toLong()).compName, customerRepo.findOne(newCstmId).compName, "客户名称", uid, "联系人更新")
        //联系人姓名
        if (oldLink.name != newLink.name) {
            var title = "联系人姓名"
            if (oldLink.mainStatus == 1) title = "主联系人姓名"
            saveLinkModify(newLink, oldLink.name, newLink.name, title, uid, "联系人更新$sourceDes")
            saveCstModify(oldLink.name, newLink.name, title, cstm, uid, "联系人更新$sourceDes")
        }
        //联系方式
        if (oldLink.phone != newLink.phone) {
            saveLinkModify(newLink, oldLink.phone, newLink.phone, "联系方式", uid, "联系人更新$sourceDes")
            saveCstModify(oldLink.phone, newLink.phone, "联系电话", cstm, uid, "联系人更新$sourceDes")
        }
    }

    //保存客户修改信息到记录表
    private fun saveCstModify(originVal: String, modifyVal: String, columnName: String, newCstm: Customer, uid: Long, remark: String) {
        val cstMd = CustomerModify()
        cstMd.originVal = if (originVal.isBlank()) " " else originVal
        cstMd.modifyVal = if (modifyVal.isBlank()) " " else modifyVal
        cstMd.columnName = columnName
        cstMd.customer = newCstm
        cstMd.creator = acctRepo.findOne(uid)
        cstMd.remark = remark
        cstMdRepo.save(cstMd)
        log.info(">>>>>>>>cstModifySave successed>>>${cstMd.columnName}")
    }

    private fun saveLinkModify(linker: Linker, originVal: String, modifyVal: String, columnName: String, uid: Long, remark: String) {
        val linkMdy = LinkerModify()
        linkMdy.creator = acctRepo.findOne(uid)
        linkMdy.linker = linker
        linkMdy.originVal = originVal
        linkMdy.modifyVal = modifyVal
        linkMdy.columnName = columnName
        linkMdy.remark = remark
        linkMdyRepo.save(linkMdy)
    }

    //联系人保存
    private fun saveLinker(linker: Linker, cstmId: Long): Linker {
        //新建
        val obj = linkerRepo.save(linker)
        //保存关系
        val cstmObj = customerRepo.findOne(cstmId)
        val linkerSet = cstmObj.linkers as MutableSet<Linker>
        linkerSet.add(obj)
        cstmObj.linkers = linkerSet
        customerRepo.save(cstmObj)
        return obj
    }

    private fun handleBankMdy(newBank: BankInfo, oldBank: BankInfo, cstm: Customer, uid: Long) {
        //银行信息可能为null或者""
        val oldBankName = tsfmBank(oldBank.name)
        val newBankName = tsfmBank(newBank.name)
        val oldOpenBank = tsfmBank(oldBank.openBank)
        val newOpenBank = tsfmBank(newBank.openBank)
        val oldBankAcct = tsfmBank(oldBank.bankAcct)
        val newBankAcct = tsfmBank(newBank.bankAcct)
        if (oldBankName != newBankName) saveCstModify(oldBankName, newBankName, "开户名称", cstm, uid, "银行信息更新")
        if (oldOpenBank != newOpenBank) saveCstModify(oldOpenBank, newOpenBank, "开户银行", cstm, uid, "银行信息更新")
        if (oldBankAcct != newBankAcct) saveCstModify(oldBankAcct, newBankAcct, "开户账号", cstm, uid, "银行信息更新")
    }

    private fun recycleGoodSave(idx: Int, list: List<Map<String, String>>) {
        (idx until list.size).map { i ->
            val tempMap = list.get(i)
            try {
                simpleGoodSave(tempMap)
            } catch (e: Exception) {
                log.error("idx:>>$idx")
                recycleGoodSave(idx + 1, list)
            }
        }
    }

    private fun simpleGoodSave(record: Map<String, String>) {
        val memberCode = record.get("member_code")!!
        log.info("memberCode:>>>$memberCode")
        val amount = record.get("amount")!!.toDouble()
        val weight = record.get("weight")!!.toDouble()
        val type = record.get("type")!!.toInt()
        val dealDate = record.get("dealDate")!!
        val category = record.get("category")!!
        var temp = goodsSaleRepo.findGoodSale(memberCode, dealDate, type, category)
        if (temp == null) {
            temp = GoodsSales()
            temp.amount = amount
            temp.weight = weight
            temp.memberCode = memberCode
            temp.type = type
            temp.category = category
            temp.dealDate = Timestamp(shortSdf.parse(dealDate).time)
        } else {
            temp.amount = amount
            temp.weight = weight
        }
        goodsSaleRepo.save(temp)
    }

    //entityManager的原生查询统一写在最后
    //分页查询所有的客户
    fun findCustomerList(result: HashMap<String, Any>, req: HttpServletRequest): HashMap<String, Any> {
        val mark = req.getParameter("mark").toInt()
        val orderType = req.getParameter("orderType").toInt()
        val specialMark = req.getParameter("specialMark")
        val uid = req.getParameter("uid").toLong()
        val xyMark = req.getParameter("xyMark")
        val queryMap = HashMap<String, String>()
        //specialMark表示走供应商逻辑
        if (specialMark != null) {
            queryMap["mainStr"] = "select a.* from v_cstm_list a left join ref_cstm_busi_relation b on a.id = b.customer_id"
            queryMap["append1"] = "and b.busi_relation_id = 2"
        } else queryMap["mainStr"] = "select a.* from v_cstm_list a"
        //mark=4查询所有公共正式客户
        if (mark == 4) queryMap["append2"] = "and a.mark in('2','3') and a.erp_code is not null"
        else queryMap["mark"] = "and a.mark = '#'"
        //组装条件
        val dateStr = if (mark == 1) "create_at" else "convert_date"
        queryMap["startTime"] = "and to_char(a.$dateStr,'yyyy-MM-dd') >= '#'"
        queryMap["endTime"] = "and to_char(a.$dateStr,'yyyy-MM-dd') <= '#'"
        queryMap["compName"] = "and a.comp_name like '%#%'"
        queryMap["name"] = "and a.linkName like '%#%'"
        queryMap["phone"] = "and a.linkPhone like '%#%'"
        queryMap["dptName"] = "and a.dptName like '%#%'"
        queryMap["acctName"] = "and a.acctName like '%#%'"
        if (mark in arrayOf(1, 2)) queryMap["append3"] = entityManage.dataLevelStr(uid, "a.acctId", "a.dptId", "a.orgId")
        queryMap["status"] = "and a.status = '#'"
        //上线情况
        queryMap["append4"] = if (xyMark == null) "" else if (xyMark == "1") "and a.erp_code is not null and a.xy_code is not null" else "and a.erp_code is not null and a.xy_code is null"
        //排序
        val arr = arrayOf(0, 2, 3)
        val sort = if (orderType in arr) "desc" else "asc"
        val prop = if (orderType in 1..2) "convert_date" else if (orderType in 3..4) "bill_date" else "update_at"
        queryMap["orderBy"] = "order by $prop $sort"
        entityManage.handleNativeQuery(result, req, queryMap)
        return result
    }

    /**
     * 微信小程序接口
     * @date 2020/03/14
     * @author samy
     */
    fun findCstmListInWxMini(name: String?, pageable: Pageable, type: Int = 0): Page<WxMiniCustomer> {
        return if (type == 1) {
            customerRepo.findXyCstmListInWxMini(name, pageable)
        } else {

            customerRepo.findCstmListInWxMini(name, pageable)
        }
    }

    /**
     * 2012-现在 客户首次开单首次提货情况
     * 弃用 历史库关闭
     */
//    fun findCstFirstBillDate(erpCode: String) = customerRepo.findCstFirstBillDate(erpCode)

    /**
     * 地图迭代
     */
    data class DataMap(var areaName: String, var count: Int,var weight: Double,var percent:String,var children:List<Any>)

    fun findMapModel(): ArrayList<DataMap> {
        val addressList = addressRepository.findAll()
        return findMapModelChildren("null", addressList as List<Address>)
    }

    fun findMapModelChildren(parentCode:String?,addressList:List<Address>): ArrayList<DataMap> {
        var tempAddressList = ArrayList<Address>()
        for (thisAddress in addressList){
            if (parentCode == thisAddress.parentCode&&"市辖区"!=thisAddress.name){
                tempAddressList.add(thisAddress)
            }
        }

        val mapList = ArrayList<DataMap>()
        if (tempAddressList.isNotEmpty()){
            for (thisAddress in tempAddressList){
                val dataMap = DataMap(thisAddress.name,0,0.00,"0",findMapModelChildren(thisAddress.code,addressList))
                mapList.add(dataMap)
            }
        }
        return mapList
    }

    /**
     * 数量填充
     * dataMap :省市区数据  childrenList:mapModel的下级
     */
    fun dealChildrenMapCount(dataMap:HashMap<String,Int>,childrenList:ArrayList<DataMap>){
        if (childrenList.isNotEmpty()){
            for (childrenData in childrenList){
                for ((key,value) in dataMap){
                    if (key==childrenData.areaName){
                        childrenData.count = value
                        break
                    }
                }
                dealChildrenMapCount(dataMap, childrenData.children as ArrayList<DataMap>)
            }
        }
    }

    /**
     * 重量填充
     * dataMap :省市区数据  childrenList:mapModel的下级
     */
    fun dealChildrenMapWeight(dataMap:HashMap<String,Double>,childrenList:ArrayList<DataMap>){
        if (childrenList.isNotEmpty()){
            for (childrenData in childrenList){
                for ((key,value) in dataMap){
                    if (key==childrenData.areaName){
                        childrenData.weight = value
                    }
                }
                dealChildrenMapWeight(dataMap, childrenData.children as ArrayList<DataMap>)
            }
        }
    }

    /**
     * 百分比填充
     * dataMap :省市区数据  childrenList:mapModel的下级
     */
    fun dealChildrenMapPercent(dataMap:HashMap<String,String>, childrenList:ArrayList<DataMap>){
        if (childrenList.isNotEmpty()){
            for (childrenData in childrenList){
                for ((key,value) in dataMap){
                    if (key==childrenData.areaName){
                        childrenData.percent = value
                    }
                }
                dealChildrenMapPercent(dataMap, childrenData.children as ArrayList<DataMap>)
            }
        }
    }


    /**
     * 根据客户性质找评估过的客户
     */
    fun findEvaluatedCst(compName: String?) :HashMap<String,Any>{
        val totalList = customerEvaluationRepo.findByCstProperty(null,compName)
        val myList = customerEvaluationRepo.findByCstProperty("贸易商",compName)
        val zdList = customerEvaluationRepo.findByCstProperty("终端客户",compName)
        val jgList = customerEvaluationRepo.findByCstProperty("加工单位",compName)
        val wlList = customerEvaluationRepo.findByCstProperty("物流单位",compName)
        val qtList = customerEvaluationRepo.findByCstProperty("其他",compName)

        //省市对应关系
        val cityProvList = addressRepository.findCityProv()

        val result = HashMap<String,Any>()
        val addressList = addressRepository.findAll() as List<Address>
        result["total"] = convertScopeToAreaList(totalList,cityProvList,addressList)
        result["my"] = convertScopeToAreaList(myList,cityProvList,addressList)
        result["zd"] = convertScopeToAreaList(zdList,cityProvList,addressList)
        result["jg"] = convertScopeToAreaList(jgList,cityProvList,addressList)
        result["wl"] = convertScopeToAreaList(wlList,cityProvList,addressList)
        result["qt"] = convertScopeToAreaList(qtList,cityProvList,addressList)
        return result
    }

    /**
     * 客户区域数据格式转化
     */
    fun convertScopeToAreaList(scopeList:List<CustomerEvaluationProjection>,cityProvList:List<CityProvProjection>,addressList: List<Address>) : List<DataMap>{
        val scopeMap = HashMap<String,Int>()
        for (scope in scopeList){
            //["aaa","b"]这种格式,得到<地址,数量>
            if (StringUtils.isNotEmpty(scope.getBusiScope()?.trim())){
                val areaArray = JSONArray.parseArray(scope.getBusiScope())
                for (thisArea in areaArray){
                    var areaStr = thisArea.toString()
                    if (scopeMap.containsKey(areaStr)) {
                        scopeMap[areaStr] = scopeMap[areaStr]!!+1
                    } else {
                        scopeMap[areaStr] = 1
                    }
                }
            }
        }
        val provMap = HashMap<String,Int>()
        scopeMap.forEach{
            //封装各个城市
            val cityName = it.key
            var provName = ""
            val tempList = cityProvList.filter { item-> item.getCity() == cityName}
            if (tempList.isNotEmpty()) provName = tempList[0].getProv()
            //各个省数量求和
            if (provMap.containsKey(provName)) {
                provMap[provName] = provMap[provName]!!+it.value
            } else {
                provMap[provName] = it.value
            }
        }

        //填数据
        //地图模型空数据
        val mapList = findMapModelChildren("null", addressList as List<Address>)
        val dataMap = HashMap<String,Int>()
        dataMap.putAll(provMap)
        dataMap.putAll(scopeMap)
        dealChildrenMapCount(dataMap,mapList)
        return mapList.sortedByDescending { it.count}
    }

    /**
     * 地区销量
     */
    fun findAreaSale(startNy: String?,endNy: String?,wsFlag:String?) : List<DataMap>{
        val citySaleList = customerEvaluationRepo.findAreaSale(startNy,endNy,wsFlag)
        val provMap = HashMap<String,Double>()
        val scopeMap = HashMap<String,Double>()
        for (citySale in citySaleList){
            scopeMap[citySale.getAreaName()] = citySale.getWeight().toDouble()
            if (provMap.containsKey(citySale.getProvinceName())) {
                provMap[citySale.getProvinceName()] = provMap[citySale.getProvinceName()]!!+citySale.getWeight().toDouble()
            } else {
                provMap[citySale.getProvinceName()] = citySale.getWeight().toDouble()
            }
        }
        //填数据
        var mapList =findMapModel()
        val dataMap = HashMap<String,Double>()
        dataMap.putAll(provMap)
        dataMap.putAll(scopeMap)
        dealChildrenMapWeight(dataMap,mapList)
        return mapList.sortedByDescending { it.weight}
    }

    /**
     * 各地区客户数
     */
    fun findCstAreaCount():HashMap<String,Any>{
        val areaSaleList = customerEvaluationRepo.findCstAreaCount()
        //总数
        val totalCityMap = HashMap<String,Int>()
        val totalProvMap = HashMap<String,Int>()
        //贸易
        val myCityMap = HashMap<String,Int>()
        val myProvMap = HashMap<String,Int>()
        //终端
        val zdCityMap = HashMap<String,Int>()
        val zdProvMap = HashMap<String,Int>()
        //加工
        val jgCityMap = HashMap<String,Int>()
        val jgProvMap = HashMap<String,Int>()
        //物流
        val wlCityMap = HashMap<String,Int>()
        val wlProvMap = HashMap<String,Int>()
        //其他
        val qtCityMap = HashMap<String,Int>()
        val qtProvMap = HashMap<String,Int>()
        for (areaSale in areaSaleList){
            //省
            var provName = areaSale.getProvinceName()
            //["aaa","b"]这种格式,得到<地址,数量>
            totalCityMap[areaSale.getAreaName()]=areaSale.getSumCount().toInt()
            myCityMap[areaSale.getAreaName()]=areaSale.getMyCount().toInt()
            zdCityMap[areaSale.getAreaName()]=areaSale.getZdCount().toInt()
            jgCityMap[areaSale.getAreaName()]=areaSale.getJgCount().toInt()
            wlCityMap[areaSale.getAreaName()]=areaSale.getWlCount().toInt()
            qtCityMap[areaSale.getAreaName()]=areaSale.getQtCount().toInt()
            //各个省数量求和
            if (totalProvMap.containsKey(provName)) {
                totalProvMap[provName] = totalProvMap[provName]!!+areaSale.getSumCount().toInt()
            } else {
                totalProvMap[provName] = areaSale.getSumCount().toInt()
            }
            if (myProvMap.containsKey(provName)) {
                myProvMap[provName] = myProvMap[provName]!!+areaSale.getMyCount().toInt()
            } else {
                myProvMap[provName] = areaSale.getMyCount().toInt()
            }
            if (zdProvMap.containsKey(provName)) {
                zdProvMap[provName] = zdProvMap[provName]!!+areaSale.getZdCount().toInt()
            } else {
                zdProvMap[provName] = areaSale.getZdCount().toInt()
            }
            if (jgProvMap.containsKey(provName)) {
                jgProvMap[provName] = jgProvMap[provName]!!+areaSale.getJgCount().toInt()
            } else {
                jgProvMap[provName] = areaSale.getJgCount().toInt()
            }
            if (wlProvMap.containsKey(provName)) {
                wlProvMap[provName] = wlProvMap[provName]!!+areaSale.getWlCount().toInt()
            } else {
                wlProvMap[provName] = areaSale.getWlCount().toInt()
            }
            if (qtProvMap.containsKey(provName)) {
                qtProvMap[provName] = qtProvMap[provName]!!+areaSale.getQtCount().toInt()
            } else {
                qtProvMap[provName] = areaSale.getQtCount().toInt()
            }
        }

        val result = HashMap<String,Any>()
        //填数据
        val addressList = addressRepository.findAll()

        val totalMapList = findMapModelChildren("null", addressList as List<Address>)
        val totalMap = HashMap<String,Int>()
        totalMap.putAll(totalCityMap)
        totalMap.putAll(totalProvMap)
        dealChildrenMapCount(totalMap,totalMapList)
        result["total"] = totalMapList.sortedByDescending { it.count}

        val myMapList = findMapModelChildren("null", addressList as List<Address>)
        val myMap = HashMap<String,Int>()
        myMap.putAll(myCityMap)
        myMap.putAll(myProvMap)
        dealChildrenMapCount(myMap,myMapList)
        result["my"] = myMapList.sortedByDescending { it.count}

        val zdMapList = findMapModelChildren("null", addressList as List<Address>)
        val zdMap = HashMap<String,Int>()
        zdMap.putAll(zdCityMap)
        zdMap.putAll(zdProvMap)
        dealChildrenMapCount(zdMap,zdMapList)
        result["zd"] = zdMapList.sortedByDescending { it.count}

        val jgMapList = findMapModelChildren("null", addressList as List<Address>)
        val jgMap = HashMap<String,Int>()
        jgMap.putAll(jgCityMap)
        jgMap.putAll(jgProvMap)
        dealChildrenMapCount(jgMap,jgMapList)
        result["jg"] = jgMapList.sortedByDescending { it.count}

        val wlMapList = findMapModelChildren("null", addressList as List<Address>)
        val wlMap = HashMap<String,Int>()
        wlMap.putAll(wlCityMap)
        wlMap.putAll(wlProvMap)
        dealChildrenMapCount(wlMap,wlMapList)
        result["wl"] = wlMapList.sortedByDescending { it.count}

        val qtMapList = findMapModelChildren("null", addressList as List<Address>)
        val qtMap = HashMap<String,Int>()
        qtMap.putAll(qtCityMap)
        qtMap.putAll(qtProvMap)
        dealChildrenMapCount(qtMap,qtMapList)
        result["qt"] = qtMapList.sortedByDescending { it.count}
        return result
    }

    /**
     * 运力占比
     */
    fun findCstDeliveryPercent() :HashMap<String,Any>{
        val provCstDeliveryList = customerEvaluationRepo.findProvCstDeliveryPercent()
        val areaCstDeliveryList = customerEvaluationRepo.findAreaCstDeliveryPercent()

        val zyMap = HashMap<String,String>()
        val wsMap = HashMap<String,String>()
        val gdMap = HashMap<String,String>()
        val fgdMap = HashMap<String,String>()
        for (delivery in provCstDeliveryList){
            val prov = delivery[0] as String
            val percent = (delivery[3] as String).trim()
            when (delivery[1]){
                "自有运力"-> zyMap[prov]=percent
                "我司配送"-> wsMap[prov]=percent
                "固定三方物流"-> gdMap[prov]=percent
                "非固定三方物流"-> fgdMap[prov]=percent
            }
        }
        for (delivery in areaCstDeliveryList){
            val area = delivery[0] as String
            val percent = (delivery[3] as String).trim()
            when (delivery[1]){
                "自有运力"-> zyMap[area]=percent
                "我司配送"-> wsMap[area]=percent
                "固定三方物流"-> gdMap[area]=percent
                "非固定三方物流"-> fgdMap[area]=percent
            }
        }

        val result = HashMap<String,Any>()
        //填数据
        val addressList = addressRepository.findAll()

        val zyMapList = findMapModelChildren("null", addressList as List<Address>)
        dealChildrenMapPercent(zyMap,zyMapList)
        result["zy"] = zyMapList.sortedByDescending { it.percent}

        val wsMapList = findMapModelChildren("null", addressList as List<Address>)
        dealChildrenMapPercent(wsMap,wsMapList)
        result["ws"] = wsMapList.sortedByDescending { it.percent}

        val gdMapList = findMapModelChildren("null", addressList as List<Address>)
        dealChildrenMapPercent(gdMap,gdMapList)
        result["gd"] = gdMapList.sortedByDescending { it.percent}

        val fgdMapList = findMapModelChildren("null", addressList as List<Address>)
        dealChildrenMapPercent(fgdMap,fgdMapList)
        result["fgd"] = fgdMapList.sortedByDescending { it.percent}
        return result
    }

    fun updateCstCityToArea(pageable: Pageable):String?{
        val cstList = customerRepo.findCstCityArea(pageable)
        return if (cstList.totalElements>0){
            for (customer in cstList.content){
                val compArea = customer.compArea
                if (StringUtils.isNotEmpty(compArea)){
                    customer.region = compArea!!.substring(0,compArea.length-1)
                    customerRepo.save(customer)
                    crmSync2XyOrErp(customer)
                }
            }
            null
        }else{
            "无需要修改地区的单位"
        }
    }

}


