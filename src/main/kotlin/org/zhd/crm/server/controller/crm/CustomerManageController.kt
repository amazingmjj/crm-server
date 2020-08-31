package org.zhd.crm.server.controller.crm

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.GlobalConfig
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.model.projection.CustomerEvaluationProjection
import org.zhd.crm.server.model.projection.erp.CustomerYearSaleProjection
import org.zhd.crm.server.model.projection.statistic.CustomerAreaRecordProjection
import org.zhd.crm.server.model.statistic.CustomerAreaRecord
import org.zhd.crm.server.model.statistic.GoodsSales
import org.zhd.crm.server.service.HttpService
import org.zhd.crm.server.service.crm.CustomerService
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.collections.HashMap

@RestController
@RequestMapping("customerManage")
class CustomerManageController : CrmBaseController() {

    private val log = LoggerFactory.getLogger(CustomerManageController::class.java)
    private val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val shortSdf = SimpleDateFormat("yyyy-MM-dd")
    @Autowired
    private lateinit var httpService: HttpService

    @PostMapping("customer/create")
    fun customerCreate(customer: Customer, linker: Linker, req: HttpServletRequest): Map<String, Any> {
        //预处理
        val result = HashMap<String, Any>()
        var resp = true
        val uid = req.getParameter("uid").toLong()
        val specialMark = req.getParameter("specialMark")
        //处理字段
        customer.createAcct = settingService.acctFindById(uid)//创建人
        customer.convertDate = Timestamp(Date().time)
        customer.billDate = Timestamp(Date().time)
        // 空格处理
        customer.compName = customer.compName.trim()
        handleCustomer(customer, req) //潜在客户,客户标识为1
        if (specialMark != null && specialMark == "0") customer.lockStatus = 1//上锁后不会转为公共客户
        //处理校验
        val msg = checkCustomer(customer)
        if (msg != "") {
            resp = false
            result["errMsg"] = msg
        }
        if (resp) {
            if (customer.unitProperty != null) {
                // 修改客户性质
                var unitPropertyStr = "1"
                customer.unitProperty!!.split(",").map { s ->
                    if (s == "2") unitPropertyStr += ",2"
                    if (s == "3") unitPropertyStr += ",3"
                }
                customer.unitProperty = unitPropertyStr
            } else {
                customer.unitProperty = "1,2"
                customer.depositRate = "20"
            }
            customerService.handleLinkCreate(customer, linker, uid)//处理联系人
            var newObj = customerService.customerSave(customer)
            if (newObj.mainCstm == null) {
                newObj.mainCstm = newObj
            } else {
                newObj.cstmType = newObj.mainCstm!!.cstmType
                newObj.startTime = newObj.mainCstm!!.startTime
            }
            customerService.customerSave(newObj)

            //处理银行账户信息,新增客户不会存在账户不唯一（cstmId+bankAcct）
            val bankInfo = customerService.handleBankInfo(newObj)
            customerService.bankInfoSave(bankInfo, newObj.id!!, uid)
            //处理供应商逻辑
            if (specialMark != null && specialMark == "0") customerService.customerTransform(newObj.id!!, uid)
            if (newObj.id!! > 0) {
                result["newObj"] = newObj
            } else {
                resp = false
                result["errMsg"] = "保存失败"
            }
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("customer/update")
    fun customerUpdate(customer: Customer, linker: Linker, linkId: Long, req: HttpServletRequest): Map<String, Any> {
        //预处理
        val result = HashMap<String, Any>()
        var resp = true
        val uid = req.getParameter("uid").toLong()
        // 空格处理
        customer.compName = customer.compName.trim()
        //处理字段
        handleCustomer(customer, req)
        linker.id = linkId
        val bankInfo = customerService.handleBankInfo(customer)
        val originObj = customerService.findMainBankInfo(customer.id)//批量导入时可能不存在银行信息
        if (originObj != null) bankInfo.id = originObj.id
        //处理校验
        var msg = checkCustomer(customer)
        if (msg == "") msg = checkCstmIntegrity(customer)
        if (msg == "" && customerService.checkBankAcct(bankInfo, customer.id!!)) msg = "当前客户下银行账号不能重复"
        if (msg != "") {
            resp = false
            result["errMsg"] = msg
        }
        if (resp) {
            handleColumnModify(customer, linker, req)//修改记录保存，先处理特殊字段，其他字段在保存时处理
            customerService.linkerUpdate(linker)//更新联系人
            customerService.bankInfoSave(bankInfo, customer.id!!, uid)//更新银行账户
            customer.compNameInitial = commUtil.getFirstSpell(customer.compName)//更新首字母
            //更新地区，取公司所在市
            if (customer.region.isNullOrBlank() && !customer.compCity.isNullOrBlank()) customer.region = commUtil.handleRegion(customer.compCity!!)
            if (customer.unitProperty != null) {
                // 修改客户性质
                var unitPropertyStr = "1"
                customer.unitProperty!!.split(",").map { s ->
                    if (s == "2") unitPropertyStr += ",2"
                    if (s == "3") unitPropertyStr += ",3"
                }
                customer.unitProperty = unitPropertyStr
            } else {
                customer.unitProperty = "1,2"
                customer.depositRate = "20"
            }
            //更新客户
            val obj = customerService.customerUpdate(customer, uid)
            result["originObj"] = obj
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @GetMapping("customer/{id}")
    fun customerFindById(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val obj = customerService.findCustomerById(id)
        result["obj"] = obj
        result["mainCustomer"] = obj.mainCstm!!
        result["returnCode"] = 0
        return result
    }

    @PostMapping("customerTransform")
    fun customerChange(cstmId: Long, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val originObj = customerService.findCustomerById(cstmId)
        if (originObj.status == 0) {
            resp = false
            result["errMsg"] = "停用客户无法转化,请修改客户状态"
        }
        if (resp) {
            customerService.customerTransform(cstmId, uid)
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("customer")
    fun customerList(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.findCustomerList(result, req)
        result["returnCode"] = 0
        return result
    }

    @GetMapping("customer/{id}/wxLinkers")
    fun customerWxLinkers(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val currentPage = if (request.getParameter("currentPage") == null) 1 else request.getParameter("currentPage").toInt()
        val pageSize = if (request.getParameter("pageSize") == null) 10 else request.getParameter("pageSize").toInt()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.cstmWxLinkers(id, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("customer/{id}/wxLinker")
    fun customerWxLinkerCreate(@PathVariable("id") id: Long, content: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val wxUser = JSONObject.parseObject(content)
        customerService.cstmWxLinker(id.toString(), wxUser.getString("nickname"), wxUser.getString("openId"), wxUser.getString("appName"), wxUser.getString("appKey"), wxUser.getString("headImgUrl"), wxUser.getString("subscribe"))
        result["returnCode"] = 0
        return result
    }

    @PostMapping("customer/{id}/wxTicket")
    fun customerWxTicker(@PathVariable("id") id: Long, appKey: String, appName: String): Map<String, Any> {
        val cstm = customerService.findCustomerById(id)
        var wxTicket = customerService.cstmWxTicket(cstm, appKey)
        if (wxTicket == null) {
            val ticketStr = httpService.sendGetRequest("${GlobalConfig.SCP.PROXY_URL}api/wechat/auth/qrTicket?routeKey=${appKey}&params=${id}")
            wxTicket = CustomerWxTicket()
            wxTicket!!.appKey = appKey
            wxTicket!!.appName = appName
            wxTicket!!.ticket = ticketStr
            wxTicket!!.fkCstm = cstm
            customerService.cstmWxTicketSave(wxTicket)
        }
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["ticket"] = wxTicket!!.ticket!!
        return result
    }

    @DeleteMapping("wxLinker/{id}")
    fun wxLinkerDel(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.wxLinkerDel(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("customerRecord")
    fun customerRecordList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val recordType = req.getParameter("recordType")//1表示查询的删除记录,2表示查询的转化记录,3表示查询流失记录
        log.info(">>>>recordType: >>$recordType")
        val page = customerService.findRecordList(req.getParameter("compName"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("startTime"),
                req.getParameter("endTime"), req.getParameter("dptName"), req.getParameter("acctName"), recordType, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("customer/queryCombo")
    fun customerAll(pageSize: Int, compName: String?, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(0, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findCustomerAll(compName, uid, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        return result
    }

    @PostMapping("customer/queryAll")
    fun customerAll(pageSize: Int, compName: String?): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(0, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findCustomerAll(compName, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        return result
    }

    @PostMapping("customer/queryRegion")
    fun cstmRegionList(pageSize: Int, region: String?): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(0, pageSize)
        val page = customerService.findRegionList(region, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        return result
    }

    @GetMapping("customer/querySupplyCatalog")
    fun supplyCatalogList(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.findSupplyCatalog()
        return result
    }

    @PostMapping("customer/updateLock")
    fun updateLock(cstmId: Long, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val originObj = customerService.findCustomerById(cstmId)
        originObj.lockStatus = status
        customerService.customerSave(originObj)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("customer/combine")
    fun combine(newId: Long, oldId: Long, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val originObj = customerService.findCustomerById(oldId)
        val newObj = customerService.findCustomerById(newId)
        if (originObj.erpCode == null) {
            resp = false
            result["errMsg"] = "客户[${originObj.compName}]不是erp客户,不允许合并"
        } else if (newObj.erpCode == null) {
            resp = false
            result["errMsg"] = "客户[${newObj.compName}]不是erp客户,不允许合并"
        } else if (originObj.erpCode == newObj.erpCode) {
            resp = false
            result["errMsg"] = "相同客户不允许合并"
        } else if (originObj.ebusiAdminAcctNo != null && newObj.ebusiAdminAcctNo != null) {
            resp = false
            result["errMsg"] = "合并客户[${newObj.compName}]存在电商编号,不允许合并"
        } else {
        }
        if (resp) {
            customerService.cstmCombine(originObj, newObj, uid)
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("combineRecord")
    fun combineRecordList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.combineRecordList(req.getParameter("oldCustName"), req.getParameter("newCustName"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("linker/{id}")
    fun linkerFindById(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["obj"] = customerService.linkFindById(id)
        return result
    }

    @PostMapping("linkerDel")
    fun linkerDelete(id: Long, cstmId: Long, reason: String, uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val obj = customerService.linkFindById(id)
        if (obj.mainStatus == 0) {
            customerService.linkerDelete(obj, cstmId, reason, uid)
            result["returnCode"] = 0
        } else {
            //主联系人不能删
            result["returnCode"] = -1
            result["errMsg"] = "主联系人不能删除"
        }
        return result
    }

    @PostMapping("linker")
    fun likerList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "update_at")
        val page = customerService.findLinkerList(req.getParameter("xyMark"), req.getParameter("compName"), req.getParameter("name"), req.getParameter("phone"),
                req.getParameter("startTime"), req.getParameter("endTime"), req.getParameter("mainStatus"), req.getParameter("sex"), req.getParameter("position"),
                req.getParameter("dptName"), req.getParameter("acctName"), req.getParameter("uid").toLong(), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("linker/createOrUpdate")
    fun linkerCreate(linker: Linker, cstmId: Long, uid: String, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        // 同一个客户联系人手机号不能重复
        var cstmLinkProjectList = customerService.findCstmLinkRepeat(linker.phone, cstmId)
        if (cstmLinkProjectList.count() == 0 || cstmLinkProjectList.first().getLinkId() == linker.id) {
            var resp = true
            ///修改所属客户时,需要检查
            val type = customerService.checkCstmForLink(linker, cstmId, req.getParameter("originCstmId"))
            if (type != 0) {
                resp = false
                if (type == 1) result["errMsg"] = "主联系人不能修改所属客户" else result["errMsg"] = "该联系人已归属于修改的客户"
            } else if (customerService.checkMainLinker(linker, cstmId)) {
                resp = false
                result["errMsg"] = "主联系人个数不能超过1个"
            } else if (linker.id != null) {
                if (customerService.checkModifyPhone(linker, cstmId)) {
                    resp = false
                    result["errMsg"] = "型云客户不能修改主联系人号码"
                }
            }
            if (resp) {
                val obj = customerService.linkerSave(uid.toLong(), linker, cstmId, req.getParameter("originCstmId"))
                result["obj"] = obj
            }
            result["returnCode"] = if (resp) 0 else -1
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "同一个客户下联系人电话不能重复"
        }
        return result

    }

    /**
     * 设置常用联系人
     * @author samy
     * @date 2019/08/20
     */
    @PostMapping("linker/{id}/common")
    fun commonLinker(cstmId: Long, @PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        // 批量设置为非常用联系人
        customerService.batchUpdateCommonLinker(cstmId)
        var commLinker = customerService.existCommonLinker(cstmId, id)
        if (commLinker == null) {
            // 保存常用联系人
            commLinker = CommonCstmLinker()
            commLinker.cstmId = cstmId
            commLinker.linkerId = id
        }
        commLinker.status = 1
        customerService.commonLinkerSave(commLinker)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("linker/queryCombo")
    fun linkerAll(cstmId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        var list = customerService.findLinkerAll(cstmId) as ArrayList<Linker>
        var commLinker = customerService.commonLinkerByCstmId(cstmId)
        if (commLinker != null) {
            var linker = list.find { l -> l.id == commLinker.linkerId!! }
            if (linker != null) {
                linker!!.commMark = 1
                list = list.filter { l -> l.id != commLinker.linkerId!! } as ArrayList<Linker>
                list.add(linker!!)
            }
        }
        result["list"] = list
        return result
    }

    @PostMapping("linkerRecord")
    fun likerRcdList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findLinkRcdList(req.getParameter("compName"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("startTime"),
                req.getParameter("endTime"), req.getParameter("mainStatus"), req.getParameter("sex"), req.getParameter("position"), req.getParameter("uid").toLong(), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("linkerModify")
    fun likerMdyList(currentPage: Int, pageSize: Int, linkId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findLinkMdyList(linkId, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("communicate/queryCombo")
    fun cmutAll(cstmId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.findCmutAll(cstmId)
        return result
    }

    @PostMapping("communicate/create")
    fun cmutCreate(cmut: Communicate, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val acctId = req.getParameter("fkAcctId")
        val cstmId = req.getParameter("fkCstmId")
        val contactDateStr = req.getParameter("fkContactDate")
        cmut.fkAcct = settingService.acctFindById(acctId.toLong())
        cmut.fkCstm = customerService.findCustomerById(cstmId.toLong())
        if (contactDateStr.isNotBlank()) {
            cmut.contactDate = Timestamp(shortSdf.parse(contactDateStr).time)
        }
        val newobj = customerService.cmutSave(cmut)
        if (newobj.id!! > 0) {
            result["returnCode"] = 0
            result["newObj"] = newobj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "保存失败"
        }
        return result
    }

    @DeleteMapping("communicate/{id}")
    fun cmutDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.cmutDelete(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("bankInfo/queryCombo")
    fun bankInfoAll(cstmId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.findBankInfoAll(cstmId)
        return result
    }

    @PostMapping("bankInfo/createOrUpdate")
    fun bankInfoCreate(bkIf: BankInfo, cstmId: Long, uid: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        if (customerService.checkBankAcct(bkIf, cstmId)) {
            resp = false
            result["errMsg"] = "当前客户下银行账号不能重复"
        }
        if (resp) {
            val obj = customerService.bankInfoSave(bkIf, cstmId, uid.toLong())
            if (obj.mainAcct == 1) handleBankInfo(obj)
            result["newObj"] = obj
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @DeleteMapping("bankInfo/{id}")
    fun bankInfoDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.bankInfoDelete(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("cstmModify")
    fun cstModifyList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findCstmModifyList(req.getParameter("startTime"), req.getParameter("endTime"), req.getParameter("acctName"),
                req.getParameter("columnName"), req.getParameter("cstmId"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("cstmCall")
    fun cstmCallAll(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.findCstmCallAll(req.getParameter("compName"), req.getParameter("callResult"), req.getParameter("startTime"),
                req.getParameter("endTime"), req.getParameter("uid"), req.getParameter("mark"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("cstmCall/callCreate")
    fun cstmCallCreate(cstmId: Long, uid: Long, planVisitTime: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val visitDate = Timestamp(longSdf.parse(planVisitTime).time)
        val billDate = Timestamp(visitDate.time + 24 * 60 * 60 * 1000 * 7)//默认拜访七天后
        if (customerService.checkRepeatCall(cstmId, uid, visitDate)) {//重复性校验
            result["errMsg"] = "计划拜访时间重复，请完成当前计划后再添加"
            result["returnCode"] = -1
        } else {
            customerService.cstmCallSave(cstmId, uid, billDate, visitDate, null)
            result["returnCode"] = 0
        }
        return result
    }

    @PostMapping("cstmCall/updateStatus")
    fun updateStatus(cstmCall: CustomerCall): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.cstmCallUpdate(cstmCall)
        result["returnCode"] = 0
        return result
    }

    @GetMapping("cstmCall/{id}")
    fun callCount(@PathVariable("id") uid: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.callCount(uid)
        return result
    }

    @PostMapping("home/productSales")
    fun productSales(acctId: Long, dateType: Int, type: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.productSales(acctId, dateType, type)
        return result
    }

    @PostMapping("home/taskCompletion")
    fun taskCompletion(acctId: Long, dateType: Int, type: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.taskCompletion(acctId, dateType, type)
        return result
    }

    @GetMapping("home/{id}")
    fun summary(@PathVariable("id") acctId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.summary(acctId)
        return result
    }

    // 用户行为列表
    @PostMapping("cstmPortrait/list/{id}")
    fun cstmPortraitList(@PathVariable("id") id: Long, currentPage: Int, pageSize: Int, req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        val cstm = customerService.findCustomerById(id)
        if (cstm != null && cstm.ebusiAdminAcctNo != null) {
            val preq = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "time")
            val userIdStr = cstm.ebusiAdminAcctNo!!.substring(2, 8).toInt().toString()
            val optTimeStr = req.getParameter("optTime")
            log.info(">>>userIdStr:$userIdStr,optTimeStr:$optTimeStr")
            val pg = customerService.behaviorSearchByXy(userIdStr, req.getParameter("event"), optTimeStr, req.getParameter("source"), req.getParameter("orderId"), req.getParameter("orderNo"), req.getParameter("goodsName"), req.getParameter("standard"), req.getParameter("length"), req.getParameter("supply"), req.getParameter("warehouse"), req.getParameter("material"), req.getParameter("measure"), req.getParameter("toleranceRange"), req.getParameter("weightRange"), req.getParameter("toleranceStart"), req.getParameter("toleranceEnd"), req.getParameter("search"), preq)
            result.put("returnCode", 0)
            result.put("list", pg.content)
            result.put("total", pg.totalElements)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 客户画像加入购物车比例
    @GetMapping("cstmPortrait/orderCount/{id}")
    fun cstmPortraitOrderCount(@PathVariable("id") id: Long, startDate: String, endDate: String): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        val cust = customerService.findCustomerById(id)
        if (cust != null && cust.ebusiAdminAcctNo != null) {
            val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//使用局部变量，解决多线程问题
            val userIdStr = cust.ebusiAdminAcctNo!!.substring(2, 8).toInt().toString()
            val startTime = longSdf.parse("$startDate 00:00:00").time
            val endTime = longSdf.parse("$endDate 23:59:59").time
            log.info(">>>userIdStr:$userIdStr,startTime:$startTime,endTime:$endTime")
            val orderCount = customerService.goodsOrderCountByxy(userIdStr, startTime, endTime)
            val cartCount = customerService.goodsCartCountByXy(userIdStr, startTime, endTime)
            result.put("returnCode", 0)
            result.put("cartCount", cartCount)
            result.put("orderCount", orderCount)
            result.put("percent", if (cartCount > 0) orderCount.toDouble() * 100 / cartCount.toDouble() else 0)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 客户画像用户加入购物车品类次数
    @GetMapping("cstmPortrait/goodsCount/{id}")
    fun cstmPortraitGoodsCount(@PathVariable("id") id: Long, startDate: String, endDate: String): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        val cust = customerService.findCustomerById(id)
        if (cust != null && cust.ebusiAdminAcctNo != null) {
            val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//使用局部变量，解决多线程问题
            val userIdStr = cust.ebusiAdminAcctNo!!.substring(2, 8).toInt().toString()
            val startTime = longSdf.parse("$startDate 00:00:00").time
            val endTime = longSdf.parse("$endDate 23:59:59").time
            log.info(">>>userIdStr:$userIdStr,startTime:$startTime,endTime:$endTime")
            val list = customerService.goodsAddCartCountByXy(userIdStr, startTime, endTime)
            result.put("returnCode", 0)
            result.put("hBeam", list.filter { brd -> brd.getGoodsName().indexOf("H型钢") >= 0 }.count())
            result.put("channelSteel", list.filter { brd -> brd.getGoodsName().indexOf("槽钢") >= 0 }.count())
            result.put("iBeam", list.filter { brd -> brd.getGoodsName().indexOf("工字钢") >= 0 }.count())
            result.put("angleSteel", list.filter { brd -> brd.getGoodsName().indexOf("角钢") >= 0 }.count())
            result.put("flatSteel", list.filter { brd -> brd.getGoodsName().indexOf("扁钢") >= 0 }.count())
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 客户画像用户订单情况
    @GetMapping("cstmPortrait/orderTypeCount/{id}")
    fun cstmPortraitOrderTypeCount(@PathVariable("id") id: Long, startDate: String, endDate: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val cstm = customerService.findCustomerById(id)
        if (cstm != null && cstm.ebusiAdminAcctNo != null) {
            val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//使用局部变量，解决多线程问题
            val list = customerService.cstmOrderCount(cstm.ebusiAdminAcctNo!!, Timestamp(longSdf.parse("$startDate 00:00:00").time), Timestamp(longSdf.parse("$endDate 23:59:59").time))
            if (list.size > 0) {
                val pending = list.filter { cor -> cor.status.equals("进行中") }.count()
                val finish = list.filter { cor -> cor.status.equals("已完成") }.count()
                val cancel = list.filter { cor -> cor.status.equals("已取消") }.count()
                val violate = list.filter { cor -> cor.status.equals("违约") }.count()
                result.put("total", pending + finish + cancel + violate)
                result.put("pending", pending)
                result.put("finish", finish)
                result.put("cancel", cancel)
                result.put("violate", violate)
            } else {
                result.put("total", 0)
                result.put("pending", 0)
                result.put("finish", 0)
                result.put("cancel", 0)
                result.put("violate", 0)
            }
            result.put("returnCode", 0)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 物资品类统计
    @PostMapping("cstmPortrait/sales/{id}")
    fun cstmPortraitSales(@PathVariable("id") id: Long, startDate: String, endDate: String, searchType: String, req: HttpServletRequest): Map<String, Any> {
        var result = HashMap<String, Any>()
        val cust = customerService.findCustomerById(id)
        if (cust != null && cust.erpCode != null) {
            val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//使用局部变量，解决多线程问题
            val category = if (req.getParameter("category") == "全部") null else req.getParameter("category")
            val list = customerService.goodsSalesList(cust.erpCode!!, Timestamp(longSdf.parse("$startDate 00:00:00").time), Timestamp(longSdf.parse("$endDate 23:59:59").time), category)
            when (searchType) {
                "queryGoods" -> result = handleCstmSalesGoodsQuery(result, startDate, endDate, list)
                "pieSearch" -> result = handleCstmPieSearch(result, list)
                else -> result = handleCstmDealSearch(result, list)
            }
            result.put("returnCode", 0)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 用户物资品类下拉
    @GetMapping("cstmPortrait/salesType/{id}")
    fun cstmPortraitSelect(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val cust = customerService.findCustomerById(id)
        if (cust != null && cust.erpCode != null) {
            result.put("returnCode", 0)
            result.put("goodsType", customerService.erpCustGoodsType(cust.erpCode!!))
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "无效客户")
        }
        return result
    }

    // 客户分级列表
    @PostMapping("cstmRating")
    fun cstmRatingList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "summary")
        var startTime: Timestamp? = null
        var endTime: Timestamp? = null
        if (req.getParameter("endTime") != null && req.getParameter("startTime") != null) {
            startTime = Timestamp(longSdf.parse(req.getParameter("startTime") + " 00:00:00").time)
            endTime = Timestamp(longSdf.parse(req.getParameter("endTime") + " 23:59:59").time)
        }
        var min: Double? = null
        var max: Double? = null
        if (req.getParameter("min") != null && req.getParameter("max") != null) {
            min = req.getParameter("min").toDouble()
            max = req.getParameter("max").toDouble()
        }
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val summaryDate = shortSdf.format(calendar.time)
        val pg = customerService.cstmRatingList(req.getParameter("xyMark"), summaryDate, req.getParameter("compName"), startTime, endTime, req.getParameter("dptName"), req.getParameter("acctName"), min, max, req.getParameter("level"), pgRequest)
        result.put("returnCode", 0)
        result.put("list", pg.content)
        result.put("total", pg.totalElements)
        return result
    }

    @PostMapping("cstmRating/detail")
    fun cstmRatingDetailist(currentPage: Int, pageSize: Int, compName: String, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "summaryDate")
        var startTime: Timestamp? = null
        var endTime: Timestamp? = null
        if (req.getParameter("startTime") != null && req.getParameter("endTime") != null) {
            startTime = Timestamp(longSdf.parse(req.getParameter("startTime") + " 00:00:00").time)
            endTime = Timestamp(longSdf.parse(req.getParameter("endTime") + " 23:59:59").time)
        }
        var pg = customerService.cstmRatingDetailList(compName, startTime, endTime, pgRequest)
        var cstm = customerService.findCustomerByCompName(compName)
        result.put("returnCode", 0)
        result.put("list", pg.content)
        result.put("total", pg.totalElements)
        result.put("mark", if (cstm == null) -1 else cstm!!.mark)
        return result
    }

    @GetMapping("cstmRating/levelCombo")
    fun cstmRatingLevelCombo(): Map<String, Any> {
        val result = HashMap<String, Any>()
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        result.put("returnCode", 0)
        result.put("list", customerService.cstmRatingLevelComb(shortSdf.format(calendar.time)))
        return result
    }

    @PostMapping("busiOppty/create")
    fun busiOpptyCreate(busiOppty: BusinessOpportunity, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val uid = req.getParameter("uid")
        busiOppty.creator = settingService.acctFindById(uid.toLong())
        val newObj = customerService.opptyCreate(busiOppty)
        if (newObj.id!! > 0) {
            result["newObj"] = newObj
            result["returnCode"] = 0
        } else {
            result["errMsg"] = "保存失败"
            result["returnCode"] = -1
        }
        return result
    }

    @PostMapping("busiOppty")
    fun opptyAll(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = customerService.opptyAll(req.getParameter("opptyName"), req.getParameter("linkerName"), req.getParameter("linkerPhone"), req.getParameter("cstmName"), req.getParameter("dptName"), req.getParameter("acctName"), req.getParameter("createAt"), req.getParameter("opptyAddr"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }


    /**
     *  =====================================
     *  客户评估
     *  @author samy
     *  @date 2020/05/21
     */
    @PostMapping("evaluation/list")
    fun cstmEvaluationList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        val uid = req.getParameter("uid")
        if (uid == null) {
            result["returnCode"] = -1
            result["errMsg"] = "缺少uid"
        } else {
            val acct = accountService.findOne(uid.toLong())
            if (acct == null) {
                result["returnCode"] = -1
                result["errMsg"] = "非法用户"
            } else {
                val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "create_at")
//        compName,linkName,dptName, employeeName, propertyMark, startDate, endDate, mark
                var compName: String? = null
                var dptName: String? = null
                var linkName: String? = null
                var employeeName: String? = null
                if (req.getParameter("compName") != null) compName = "%" + req.getParameter("compName") + "%"
                if (req.getParameter("linkerName") != null) linkName = "%" + req.getParameter("linkerName") + "%"
                if (req.getParameter("dptName") != null) dptName = "%" + req.getParameter("dptName") + "%"
                if (req.getParameter("employeeName") != null) employeeName = "%" + req.getParameter("employeeName") + "%"
//                var stDate: Timestamp? = null
                var edDate: String? = null
//                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                if (req.getParameter("startDate") != null) stDate = Timestamp(sdf.parse(req.getParameter("startDate")).time)
                if (req.getParameter("endDate") != null) edDate = req.getParameter("endDate") + " 23:59:59"
                val page = when (acct.dataLevel) {
                    "业务员" -> {
                        if ("1".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListNeedUpdate(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), uid, null, pg) else if ("2".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListUpdated(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), uid, null, pg) else customerService.customerEvaluationList(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), uid, null, pg)
                    }
                    "部门" -> {
                        if ("1".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListNeedUpdate(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, acct.fkDpt.id.toString(), pg) else if ("2".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListUpdated(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, acct.fkDpt.id.toString(), pg) else customerService.customerEvaluationList(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, acct.fkDpt.id.toString(), pg)
                    }
                    else -> {
                        if ("1".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListNeedUpdate(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, null, pg) else if ("2".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListUpdated(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, null, pg) else customerService.customerEvaluationList(compName, linkName, dptName, employeeName, req.getParameter("customerPropertyMark"), req.getParameter("startDate"), edDate, req.getParameter("mark"), null, null, pg)
                    }
                }
//                val page = if ("1".equals(req.getParameter("showUpdate"))) customerService.customerEvaluationListNeedUpdate(compName, linkName, dptName, employeeName, req.getParameter("propertyMark"), stDate, edDate, req.getParameter("mark"), pg) else customerService.customerEvaluationList(compName, linkName, dptName, employeeName, req.getParameter("propertyMark"), stDate, edDate, req.getParameter("mark"), pg)
                result["list"] = page.content
                result["total"] = page.totalElements
            }
        }
        return result
    }

    /**
     * 客户评估保存并修改
     * @author samy
     * @date 2020/05/21
     */
    @PostMapping("evaluation")
    @Transactional(rollbackOn = [Exception::class])
    fun cstmEvaluationSaveOrUpdate(@Valid customerEvaluation: CustomerEvaluation, req: HttpServletRequest): Map<String, Any> {
        var result = HashMap<String, Any>()
        customerService.customerEvaluationSaveOrUpdate(customerEvaluation, req.getParameter("dataStr"))
        result["returnCode"] = 0
        return result
    }

    /**
     * 客户评估详情
     * @author samy
     * @date 2020/05/22
     */
    @GetMapping("evaluation/{id}")
    fun cstmEvaluationDetail(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        var objList = customerService.customerEvaluationOne(id)
        if (objList.count() == 0) {
            result["returnCode"] = -1
            result["errMsg"] = "非法id"
        } else {
            val obj = objList[0]
            result["obj"] = obj
            result["firstBillTime"] = obj.getFirstBillDate()
            result["firstLadTime"] = obj.getFirstDeliveryDate()
            val evalObj = customerService.customerEvaluationObj(id)
            if (evalObj != null) result["dataList"] = customerService.customerEvaluationDataList(evalObj.id!!)
            if (obj!!.getErpCode() != null) {
                val yearList = customerService.customerEvaluationYearSale(obj.getErpCode()!!, "2019", "2020")
                val jsonArray = JSONArray()
                val subPage = PageRequest(0, 3, Sort.Direction.DESC, "WEIGHT")
                yearList.map { p ->
                    val year = p.getYearStr()
                    val goodsList = customerService.customerEvaluationYearGoods(year, obj!!.getErpCode(), subPage)
                    val temp = JSONObject()
                    temp["yearStr"] = year
                    temp["billWeight"] = p.getBillWeight()
                    temp["ladWeight"] = p.getLadWeight()
                    temp["highSale"] = p.getHighSale()
                    val end = goodsList.content.size - 1
                    for (index in 0..end) {
                        temp["goods${index + 1}"] = goodsList.content[index].getName()
                    }
                    jsonArray.add(temp)
                }
                result["yearSaleList"] = jsonArray

//                val timeObj = customerService.customerEvaluationBillTime(obj!!.getErpCode())
//                if (timeObj.count() > 0) {
//                    result["firstBillTime"] = timeObj.get(0).getFirstBillTime()
//                    result["firstLadTime"] = timeObj.get(0).getFirstLadTime()
//                }
                val purchaseGoodsList = customerService.customerEvaluationCurrentYearMainPurchaseGoods(obj!!.getErpCode())
                if (purchaseGoodsList.count() > 0) {
                    result["purchaseGoods"] = purchaseGoodsList.joinToString(",")
                }
            }
        }
        return result
    }

    /**
     * 客户评估年品名排名
     * @author samy
     * @date 2020/05/26
     */
    @GetMapping("evaluation/year/goodsName")
    fun cstmEvaluationYearGoodsName(erpCode: String, startYear: String, endYear: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        val yearList = customerService.customerEvaluationYearSale(erpCode, startYear, endYear)
        val jsonArray = JSONArray()
        val subPage = PageRequest(0, 3, Sort.Direction.DESC, "WEIGHT")
        yearList.map { p ->
            val year = p.getYearStr()
            val goodsList = customerService.customerEvaluationYearGoods(year, erpCode, subPage)
            val temp = JSONObject()
            temp["yearStr"] = year
            temp["billWeight"] = p.getBillWeight()
            temp["ladWeight"] = p.getLadWeight()
            temp["highSale"] = p.getHighSale()
            val end = goodsList.content.size - 1
            for (index in 0..end) {
                temp["goods${index + 1}"] = goodsList.content[index].getName()
            }
            jsonArray.add(temp)
        }
        result["list"] = jsonArray
        return result
    }

    /**
     * 客户评估地区列表
     * @author samy
     * @date 2020/05/22
     */
    @PostMapping("evaluation/area/list")
    fun cstmEvaluationAreaList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        val uid = req.getParameter("uid")
        if (uid == null) {
            result["returnCode"] = -1
            result["errMsg"] = "缺少uid"
        } else {
            val acct = accountService.findOne(uid.toLong())
            if (acct == null) {
                result["returnCode"] = -1
                result["errMsg"] = "非法用户"
            } else {
                val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "id")
                var areaName: String? = null
                if (req.getParameter("areaName") != null) areaName = "%" + req.getParameter("areaName") + "%"
//            areaName: String?, dptName: String?, employeeName: String?, dptId: String?, acctId: String?
                var dptName: String? = null
                if (req.getParameter("dptName") != null) dptName = "%" + req.getParameter("dptName") + "%"
                var employeeName: String? = null
                if (req.getParameter("employeeName") != null) employeeName = "%" + req.getParameter("employeeName") + "%"
                val page = when (acct.dataLevel) {
                    "业务员" -> {
                        customerService.customerEvaluationAreaList(areaName, dptName, employeeName, null, uid, pg)
                    }
                    "部门" -> {
                        customerService.customerEvaluationAreaList(areaName, dptName, employeeName, acct.fkDpt.id.toString(), null, pg)
                    }
                    else -> {
                        customerService.customerEvaluationAreaList(areaName, dptName, employeeName, null, null, pg)
                    }
                }
                result["list"] = page.content
                result["total"] = page.totalElements
            }
        }
        return result
    }

    /**
     * 客户评估地区保存或修改
     * @author samy
     * @date 2020/05/22
     */
    @PostMapping("evaluation/area")
    fun cstmEvaluationAreaSaveOrUpdate(@Valid customerAreaRecord: CustomerAreaRecord, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        customerService.customerEvaluationAreaSaveOrUpdate(customerAreaRecord)
        return result
    }

    private fun handleCstmDealSearch(res: HashMap<String, Any>, list: List<GoodsSales>): HashMap<String, Any> {
        val onlineWeight = list.filter { gl -> gl.type == 1 }.map { gl -> gl.weight!! }.toList().sum()
        val onlineAmount = list.filter { gl -> gl.type == 1 }.map { gl -> gl.amount!! }.toList().sum()
        val offlineWeight = list.filter { gl -> gl.type == 0 }.map { gl -> gl.weight!! }.toList().sum()
        val offlineAmount = list.filter { gl -> gl.type == 0 }.map { gl -> gl.amount!! }.toList().sum()
        val totalWeight = onlineWeight + offlineWeight
        val totalAmount = onlineAmount + offlineAmount
        res["onlineWeight"] = onlineWeight
        res["onlineAmount"] = onlineAmount
        res["offlineWeight"] = offlineWeight
        res["offlineAmount"] = offlineAmount
        res["totalWeight"] = totalWeight
        res["totalAmount"] = totalAmount
        return res
    }

    private fun handleCstmPieSearch(res: HashMap<String, Any>, list: List<GoodsSales>): HashMap<String, Any> {
//        val arr = arrayOf("H型钢", "槽钢", "角钢", "圆钢", "扁钢", "开平板", "其他")
        val goodsTypeTable = ArrayList<Map<String, Any>>()
        val glist = list.groupBy { gl -> gl.category }
        val demf = DecimalFormat("#0.000")
        glist.keys.map { s ->
            val tempList = glist.get(s)!!
            val tempMap = HashMap<String, Any>()
            tempMap.put("type", s)
            val tempOnline = tempList.filter { gl -> gl.type == 1 }.map { gl -> gl.weight!! }.toList().sum()
            val tempOffline = tempList.filter { gl -> gl.type == 0 }.map { gl -> gl.weight!! }.toList().sum()
            val tempTotal = tempOffline + tempOnline
            tempMap["total"] = if (tempTotal == 0.0) "0" else demf.format(tempTotal)
            tempMap["online"] = if (tempOnline == 0.0) "0" else demf.format(tempOnline)
            tempMap["offline"] = if (tempOffline == 0.0) "0" else demf.format(tempOffline)
            goodsTypeTable.add(tempMap)
        }
        res["goodsTypeTable"] = goodsTypeTable
        return res
    }

    private fun handleCstmSalesGoodsQuery(res: HashMap<String, Any>, startDate: String, endDate: String, list: List<GoodsSales>): HashMap<String, Any> {
        var dateRange = commUtil.getDateRange(startDate, endDate)
        var totalList = ArrayList<String>()
        var onlineList = ArrayList<String>()
        var offlineList = ArrayList<String>()
        val sdf = SimpleDateFormat("MM月dd日")
        val demf = DecimalFormat("#.000")
        dateRange.map { r ->
            val temp = list.filter { gl -> sdf.format(gl.dealDate).equals(r) }
            if (temp.size > 0) {
                val tempOnline = temp.filter { gl -> gl.type == 1 }.map { gl -> gl.weight!! }.toList().sum()
                val tempOffline = temp.filter { gl -> gl.type == 0 }.map { gl -> gl.weight!! }.toList().sum()
                val tempTotal = tempOffline + tempOnline
                onlineList.add(if (tempOnline == 0.0) "0" else demf.format(tempOnline))
                offlineList.add(if (tempOffline == 0.0) "0" else demf.format(tempOffline))
                totalList.add(if (tempTotal == 0.0) "0" else demf.format(tempTotal))
            } else {
                onlineList.add("0")
                offlineList.add("0")
                totalList.add("0")
            }
        }
        res.put("dateRange", dateRange)
        res.put("totalList", totalList)
        res.put("onlineList", onlineList)
        res.put("offlineList", offlineList)
        return res
    }

    //处理客户报文，新增修改客户时用到
    private fun handleCustomer(customer: Customer, req: HttpServletRequest) {
        val busiRelations = req.getParameterValues("fkRelation[]")//必填,业务关系id数组
        val customerProId = req.getParameter("fkCustomPropertyId")//必填,客户性质id
        val deptId = req.getParameter("fkDptId")//必填,业务部门id
        val acctId = req.getParameter("fkAcctId")//必填,业务员id
        val supplyCatalogs = req.getParameterValues("fkPurchaseGoods[]")//采购物资品类name数组
        val purposes = req.getParameterValues("fkPurchaseUse[]")//采购用途name数组
        val hopeAddGoods = req.getParameterValues("fkHopeAddGoods[]")//希望增加采购物资品类name数组
        val dealGoods = req.getParameterValues("fkDealGoods[]")//经营物资品类name数组
        val dealPurpose = req.getParameterValues("fkDealPurposeUse[]")//经营用途name数组
        val processReqs = req.getParameterValues("fkProcessingRequirements[]")//加工需求name数组
        val setUpDateStr = req.getParameter("fkSetUpDate")//成立时间Timestamp类型单独处理
        val industry = req.getParameterValues("fkIndustry[]")//所属行业name数组
        val startTimeStr = req.getParameter("fkStartTime")//起始日期
        val busiRelSet = HashSet<BusiRelation>()
        val supplyCatalogSet = HashSet<SupplyCatalog>()
        val purposeSet = HashSet<Purpose>()
        val processReqSet = HashSet<ProcessRequirement>()
        val hopeAddGoodsSet = HashSet<SupplyCatalog>()
        val dealGoodsSet = HashSet<SupplyCatalog>()
        val dealPurposeSet = HashSet<Purpose>()
        val industrySet = HashSet<Industry>()

        //客户类型限制死只能 1 公司客户
        customer.customerType = 1

        log.info("start handling startTime>>$startTimeStr")
        if (!startTimeStr.isNullOrEmpty()) {
            customer.startTime = Timestamp(shortSdf.parse(startTimeStr).time)
        }

        log.info("start handling setupdate>>$setUpDateStr")
        if (!setUpDateStr.isNullOrEmpty()) {
            customer.setUpDate = Timestamp(shortSdf.parse(setUpDateStr).time)
        }

        log.info("start handling busiRelation>>$busiRelations")
        for (id in busiRelations) {
            //业务关系
            val busiRel = basicDataService.findBusiRelationById(id.toLong())
            busiRelSet.add(busiRel)
        }
        customer.busiRelation = busiRelSet

        log.info("start handling customProperty>>$customerProId")
        //客户性质
        customer.fkCustomProperty = basicDataService.findCustomPropertyById(customerProId.toLong())

        log.info("start handling dpt>>$deptId")
        //业务部门
        customer.fkDpt = settingService.dptFindById(deptId.toLong())

        log.info("start handling account>>$acctId")
        //业务员
        if (null != customer.id) {
            val oldCustomer = customerService.findCustomerById(customer.id)
            val oldAcct = oldCustomer.fkAcct
            //修改业务员,开过单的新客户变老客户
            if (oldAcct.id != acctId.toLong() && customer.cstmType == 0 && null != customer.startTime && oldCustomer.erpCode != null) {
                if (null != oldCustomer.firstBillDate) {
                    customer.startTime = Timestamp(Date().time - (365 + 2) * 24 * 3600 * 1000L)//推移2天防止闰年
                    customer.cstmType = 1
                }
            }
        }
        customer.fkAcct = settingService.acctFindById(acctId.toLong())
        log.info("start handling industry>>$industry")
        //所属行业
        if (null != industry) {
            handleIndustrySet(industry, industrySet)
        }
        customer.industry = industrySet

        log.info("start handling supplyCatalog>>$supplyCatalogs")
        //采购物资品类
        if (null != supplyCatalogs) {
            handleSupplyCatalogSet(supplyCatalogs, supplyCatalogSet)
        }
        customer.procurementGoods = supplyCatalogSet

        log.info("start handling purpose>>$purposes")
        //采购用途
        if (null != purposes) {
            handlePurposeSet(purposes, purposeSet)
        }
        customer.procurementPurpose = purposeSet

        log.info("start handling hopeAddGoods>>$hopeAddGoods")
        //希望增加采购物资品类
        if (null != hopeAddGoods) {
            handleSupplyCatalogSet(hopeAddGoods, hopeAddGoodsSet)
        }
        customer.hopeAddGoods = hopeAddGoodsSet

        log.info("start handling dealGoods>>$dealGoods")
        //经营物资品类
        if (null != dealGoods) {
            handleSupplyCatalogSet(dealGoods, dealGoodsSet)
        }
        customer.dealGoods = dealGoodsSet

        log.info("start handling dealPurpose>>$dealPurpose")
        //经营用途
        if (null != dealPurpose) {
            handlePurposeSet(dealPurpose, dealPurposeSet)
        }
        customer.dealPurpose = dealPurposeSet

        log.info("start handling processingRequirement>>$processReqs")
        //加工需求
        if (null != processReqs) {
            for (processName in processReqs) {
                val processReq = basicDataService.findProcessReqByName(processName)
                //基础数据不能在客户页面新增
                processReqSet.add(processReq)
            }
        }
        customer.processRequirement = processReqSet

        log.info("handleCustomer finished>>")
    }

    //处理物资品类
    private fun handleSupplyCatalogSet(array: Array<String>, set: HashSet<SupplyCatalog>) {
        for (goodsName in array) {
            //根据name查询物资品类
            val supplyCatalog = basicDataService.findSupplyCatalogByName(goodsName)
            //基础数据不能在客户页面新增
            set.add(supplyCatalog)
        }
    }

    //处理物资用途
    private fun handlePurposeSet(array: Array<String>, set: HashSet<Purpose>) {
        for (purposeName in array) {
            //根据name查询物资用途
            val purpose = basicDataService.findPurposeByName(purposeName)
            //基础数据不能在客户页面新增
            set.add(purpose)
        }
    }

    //处理所属行业
    private fun handleIndustrySet(array: Array<String>, set: HashSet<Industry>) {
        for (industryName in array) {
            //根据name查询所属行业
            val industry = basicDataService.findIndustryByname(industryName)
            //基础数据不能在客户页面新增
            set.add(industry)
        }
    }

    private fun handleBankInfo(obj: BankInfo) {
        val cstmObj = customerService.findCustomerById(obj.fkCstm.id)
        cstmObj.openAcctName = obj.name
        cstmObj.openBank = obj.openBank
        cstmObj.openAcct = obj.bankAcct
        customerService.customerSave(cstmObj)
    }

    //客户修改信息到记录表
    private fun handleColumnModify(customer: Customer, linker: Linker, req: HttpServletRequest) {
        val oldCstm = customerService.findCustomerById(customer.id)
        val uid = req.getParameter("uid").toLong()
        val remark = "客户更新"
        //处理业务关系
        val busiRelations = req.getParameterValues("fkRelation[]")//id数组
        customerService.handleBusiRelMdy(oldCstm, busiRelations, customer, uid, remark)
        //处理客户性质
        val customerProId = req.getParameter("fkCustomPropertyId").toLong()//id
        customerService.handleCstmProMdy(oldCstm, customerProId, customer, uid, remark)
        //处理业务部门
        val deptId = req.getParameter("fkDptId").toLong()//id
        customerService.handleDptMdy(oldCstm, deptId, customer, uid, remark)
        //处理业务员
        val acctId = req.getParameter("fkAcctId").toLong()
        customerService.handleAcctMdy(oldCstm, acctId, customer, uid, remark)
        //处理联系人
        customerService.handleLinkerMdy(oldCstm.id!!, customer, uid, linker, remark)
        //处理起始日期
        customerService.handleStartTime(oldCstm, customer, uid, remark)
        //处理客户状态
        customerService.handleStatus(oldCstm, customer, uid, remark)
        //处理关联主体
        customerService.handleMainCst(oldCstm, customer, uid, remark)
    }

    //校验客户
    private fun checkCustomer(customer: Customer): String {
        var msg = ""
        if (customer.id != null) {//更新
            val oldCtm = customerService.findCustomerById(customer.id)
            if (customer.compName != oldCtm.compName && customerService.compNameCount(customer.compName) > 0) msg = "公司名称不能重复"
            else if (!customer.busiLicenseCode.isNullOrBlank() && customer.busiLicenseCode != oldCtm.busiLicenseCode && customerService.busiLicenseCodeCount(customer.busiLicenseCode!!) > 0) msg = "工商证照编码不能重复"
            else if (!customer.tfn.isNullOrBlank() && customer.tfn != oldCtm.tfn && customerService.tfnCount(customer.tfn!!) > 0) msg = "税号不能重复"
            else if (!customer.openAcctName.isNullOrBlank() && customer.openAcctName != oldCtm.openAcctName && customerService.openAcctNameCount(customer.openAcctName!!) > 0) msg = "开户名称不能重复"
            else {
            }
        } else {//新增
            if (customerService.compNameCount(customer.compName) > 0) msg = "公司名称不能重复"
            else if (!customer.busiLicenseCode.isNullOrBlank() && customerService.busiLicenseCodeCount(customer.busiLicenseCode!!) > 0) msg = "工商证照编码不能重复"
            else if (!customer.tfn.isNullOrBlank() && customerService.tfnCount(customer.tfn!!) > 0) msg = "税号不能重复"
            else if (!customer.openAcctName.isNullOrBlank() && customerService.openAcctNameCount(customer.openAcctName!!) > 0) msg = "开户名称不能重复"
            else {
            }
        }
        if (msg == "") {
            if (customerService.checkAccount(customer.fkAcct.id!!)) msg = "业务员不是ERP用户"
            else {
            }
        }
        return msg
    }

    //检查客户信息完善度
    private fun checkCstmIntegrity(customer: Customer): String {
        var msg = ""
        if (customer.mark == 2) {//正式客户更新需要校验必填项
            var checkMap = mapOf<String, String>()
            if (customer.ebusiAdminAcctNo.isNullOrBlank()) {
                //线下客户编辑修改时，只需要填写红色必填项
            } else {
                //工商证照编码,公司地址,公司规模,公司类型,税号,开户名称,开户银行,开户账号
                checkMap = mapOf("BusiLicenseCode" to "工商证照编码", "CompAddr" to "公司地址", "CompSize" to "公司规模", "CompType" to "公司类型", "Tfn" to "税号", "OpenBank" to "开户银行", "OpenAcctName" to "开户名称", "OpenAcct" to "开户账号")
            }
            msg = customerService.checkCstmIntegrity(customer, checkMap)
        }
        return msg
    }

    /**
     * 客户经营区域覆盖图
     */
    @PostMapping("evaluation/chart/scopeChart")
    fun findCstScopeChart(compName: String?): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result["dataMap"] = customerService.findEvaluatedCst(compName)
        result["returnCode"] = 0
        return result
    }

    /**
     * 销售区域覆盖图
     */
    @PostMapping("evaluation/chart/saleAreaChart")
    fun findSaleAreaChart(startNy: String?, endNy: String?, wsFlag: String?): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result["dataMap"] = customerService.findAreaSale(startNy, endNy, wsFlag)
        result["returnCode"] = 0
        return result
    }

    /**
     * 客户区域分布图
     */
    @PostMapping("evaluation/chart/cstAreaChart")
    fun findCstAreaChart(): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result["dataMap"] = customerService.findCstAreaCount()
        result["returnCode"] = 0
        return result
    }

    /**
     * 客户物流分布图
     */
    @PostMapping("evaluation/chart/cstDeliveryChart")
    fun findCstDeliveryChart(): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result["dataMap"] = customerService.findCstDeliveryPercent()
        result["returnCode"] = 0
        return result
    }
}