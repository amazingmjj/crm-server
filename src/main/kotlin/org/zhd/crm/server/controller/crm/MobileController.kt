package org.zhd.crm.server.controller.crm

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.Account
import org.zhd.crm.server.model.crm.BusinessOpportunity
import org.zhd.crm.server.model.crm.CustomerCall
import org.zhd.crm.server.model.crm.Linker
import org.zhd.crm.server.util.CrmConstants
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@RestController
@RequestMapping("mobile")
class MobileController : CrmBaseController() {
    @PostMapping("login")
    fun login(acct: String, pwd: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        var accout = accountService.findByLoginAcct(acct)
        if (accout == null || accout.loginAcct == "admin") {//手机端不允许使用admin账号
            result.put("errMsg", "账户不存在")
            result.put("returnCode", -1)
        } else {
            if (accout.status == 0) {
                result.put("returnCode", -1)
                result.put("errMsg", "账号已被禁用")
            } else if (accout.pwd.equals(pwd)) {
                result.put("returnCode", 0)
                result.put("currentUser", accout)
                result.put("errMsg", "登录成功")
            } else {
                result.put("returnCode", -1)
                result.put("errMsg", "用户名或密码错误")
            }
        }
        return result
    }

    @PostMapping("acct/updatePwd")
    fun acctUpdatePwd(newPwd: String, uid: Long, oldPwd: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val obj = settingService.acctFindById(uid)
        if (obj.pwd != oldPwd) {
            resp = false
            result["errMsg"] = "旧密码输入错误"
        } else if (oldPwd == newPwd){
            resp = false
            result["errMsg"] = "新密码不能和旧密码相同"
        }
        else settingService.acctUpdatePwd(newPwd, uid)
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("cstmCall")
    fun cstmCallAll(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val type = req.getParameter("type")
        val sort = if (type == "0") Sort.Direction.DESC else Sort.Direction.ASC
        val property = if (type == "0") "updateAt" else "planVisitTime" //查历史 根据更新时间倒序
        val pg = PageRequest(currentPage, pageSize, sort, property)
        val mark = if (type == "0") "0" else "1"
        val startTime = if (type == "2") SimpleDateFormat("yyyy-MM-dd").format(Date()) else null
        val endTime = if (type == "2") SimpleDateFormat("yyyy-MM-dd").format(Date()) else null
        val page = customerService.findCstmCallAll(req.getParameter("compName"), null, startTime,
                endTime, req.getParameter("uid"), mark, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("cstmCallList")
    fun cstmCallList(currentPage: Int, pageSize: Int, uid: Long, cstmId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.ASC, "planVisitTime")
        val page = customerService.findCstmCallList(uid, cstmId, 0, pg)//进行中的
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("cstmCall/callCreate")
    fun cstmCallCreate(cstmId: Long, uid: Long, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val visitDate = Timestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(req.getParameter("planVisitTime")).time)
        val billDate = Timestamp(visitDate.time + 24 * 60 * 60 * 1000 * 7)//默认拜访七天后
        if (customerService.checkRepeatCall(cstmId, uid, visitDate)){//重复性校验
            result["errMsg"] = "计划拜访时间重复，请完成当前计划后再添加"
            result["returnCode"] = -1
        } else {
            customerService.cstmCallForMobile(cstmId, uid, billDate, visitDate, req.getParameter("callType"))
            result["returnCode"] = 0
        }
        return result
    }

    @GetMapping("cstmCall/{id}")
    fun queryCstmCall(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val obj = customerService.findCallById(id)
        val link = customerService.findMainLink(obj.customer.id)
        if (link != null) {
            val linkerSet = HashSet<Linker>()
            linkerSet.add(link)
            obj.customer.linkers = linkerSet
        }
        result["returnCode"] = 0
        result["list"] = obj
        return result
    }

    @PostMapping("cstmCall/update")
    fun cstmCallSignIn(cstmCall: CustomerCall): Map<String, Any> {
        val result = HashMap<String, Any>()
        customerService.cstmCallSignIn(cstmCall)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("home/taskCompletion")
    fun taskCompletion(uid: Long, type: Int): Map<String, Any>{
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = customerService.taskCompletionForMobile(uid, type)
        return result
    }

    @GetMapping("customer/{id}")
    fun customerFindById(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val obj = customerService.findCustomerById(id)
        //获取主联系人
        val link = customerService.findMainLink(id)
        if (link != null) {
            val linkerSet = HashSet<Linker>()
            linkerSet.add(link)
            obj.linkers = linkerSet
            result["obj"] = obj
        } else {
            resp = false
            result["errMsg"] = "客户${obj.id}不存在主联系人,请联系管理员处理"
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("customer")
    fun customerList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val arr = arrayOf(0,2,4,6,8)
        val orderType = req.getParameter("orderType").toInt()
        val sort = if (arr.contains(orderType)) Sort.Direction.ASC else Sort.Direction.DESC
        val prop = if (orderType in 0..1) "create_at" else if (orderType in 2..3) "clock_time" else if (orderType in 4..5) "comp_name_initial" else if (orderType in 6..7) "summary" else "update_at"
        val pg = PageRequest(currentPage, pageSize, sort, prop, "cstm_id")//id作为第二级排序
        val page = customerService.findCstmForMobile(req.getParameter("uid"),req.getParameter("billType"),req.getParameter("initial"),req.getParameter("compName"),req.getParameter("dataType"),req.getParameter("mark"),pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("acct/{id}")
    fun getAcct(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        result.put("obj", settingService.acctFindById(id))
        return result
    }

    @PostMapping("busiOppty/create")
    fun opptyCreate(busiOppty: BusinessOpportunity, req: HttpServletRequest): Map<String, Any>{
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
    fun opptyAll(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any>{
        val result = HashMap<String, Any>()
        val orderType = req.getParameter("orderType").toInt()
        val arr = arrayOf(0,2)
        val sort = if (arr.contains(orderType)) Sort.Direction.ASC else Sort.Direction.DESC
        val prop = if (orderType in 0..1) "createAt" else if (orderType in 2..3) "opptyName" else "updateAt"
        val pg = PageRequest(currentPage, pageSize, sort, prop)
        val page = customerService.opptyAll(req.getParameter("opptyName"), req.getParameter("dataLevel"), req.getParameter("uid"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("message/{id}")
    fun acctMessage(@PathVariable("id") uid: Long): Map<String, Any>{
        val result = HashMap<String, Any>()
        val list = customerService.acctMessage(uid, 0)
        result["returnCode"] = 0
        result["list"] = list
        result["total"] = list.size
        return result
    }

    @GetMapping("msgUpdate/{id}")
    fun msgUpdate(@PathVariable("id") id: Long): Map<String, Any>{
        val result = HashMap<String, Any>()
        customerService.msgUpdate(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("mobileInfo/create")
    fun mobileInfoCreate(acctId: Long, deviceNum: String, type: Int): Map<String, Any>{
        val result = HashMap<String, Any>()
        val newObj = customerService.mobInfoCreate(acctId, deviceNum, type)
        if (newObj.id!! > 0) {
            result["newObj"] = newObj
            result["returnCode"] = 0
        } else {
            result["errMsg"] = "保存失败"
            result["returnCode"] = -1
        }
        return result
    }

    @PostMapping("mobileInfo/logOut")
    fun logOut(deviceNum: String, type: Int): Map<String, Any>{
        val result = HashMap<String, Any>()
        val msg = customerService.mobLogOut(deviceNum, type)
        if (msg == "") {
            result["returnCode"] = 0
        } else {
            result["errMsg"] = msg
            result["returnCode"] = -1
        }
        return result
    }

    @PostMapping("dpt/queryCombo")
    fun deptCombo(pageSize: Int, dptName: String?): Map<String, Any> {
        val result = java.util.HashMap<String, Any>()
        val pg = PageRequest(0, pageSize, Sort.Direction.DESC, "updateAt")
        result["returnCode"] = 0
        result["list"] = settingService.findAllDpt(dptName, pg)
        return result
    }

    @PostMapping("smsCreate")
    fun verifySms(mobile: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val msg = callCenterService.verifySms(mobile, commUtil.verifyCode())
        if (msg == "") {
            result["returnCode"] = 0
        } else {
            result["errMsg"] = msg
            result["returnCode"] = -1
        }
        return result
    }

    @PostMapping("acct/create")
    fun acctCreate(dptId: Long, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val mobile = req.getParameter("mobile")
        val verifyCode = req.getParameter("verifyCode")
        val pwd = req.getParameter("pwd")
        val repeatPwd = req.getParameter("repeatPwd")
        val userName = req.getParameter("userName")
        var resp = true
        //校验
        if (callCenterService.checkMobile(mobile)) {
            resp = false
            result["errMsg"] = "手机号已被注册"
        } else if (callCenterService.checkVerifyCode(mobile, verifyCode)) {
            resp = false
            result["errMsg"] = "验证码不一致，请填写最新的验证码或者重新发送"
        } else if (pwd != repeatPwd) {
            resp = false
            result["errMsg"] = "两次密码填写不一致"
        }
        if (resp){
            val acct = Account()
            acct.name = userName
            acct.loginAcct = mobile
            acct.pwd = pwd
            acct.phone = mobile
            settingService.acctSave(acct, dptId, CrmConstants.DEFAULT_ROLE_ID)
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }
}