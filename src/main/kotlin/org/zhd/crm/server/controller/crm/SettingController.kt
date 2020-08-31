package org.zhd.crm.server.controller.crm

import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.service.crm.MenuAuthService
import org.zhd.crm.server.service.crm.MenuService
import org.zhd.crm.server.util.CrmConstants
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@RestController
@RequestMapping("setting")
class SettingController : CrmBaseController() {

    private val log = LoggerFactory.getLogger(SettingController::class.java)

    @PostMapping("acct")
    fun acctList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.acctFindList(req.getParameter("name"), req.getParameter("loginAcct"), req.getParameter("orgName"), req.getParameter("dptName"), req.getParameter("position"), req.getParameter("phone"), req.getParameter("roleName"), req.getParameter("acctFindList"), pgRequest)
        val menuAuthList = menuAuthService.findByAcct(req.getParameter("name"), req.getParameter("loginAcct"), req.getParameter("orgName"), req.getParameter("dptName"), req.getParameter("position"), req.getParameter("phone"), req.getParameter("roleName"), req.getParameter("acctFindList"))
        result["returnCode"] = 0
        result["list"] = pg.content
        result["menuAuthList"] = menuAuthList
        result["total"] = pg.totalElements
        return result
    }


    @GetMapping("acct/{id}")
    fun getAcct(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        result.put("obj", settingService.acctFindById(id))
        return result
    }

    @PostMapping("acct/createOrUpdate")
    fun acctCreate(acct: Account, dptId: Long, roleId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val originLoginAcct = if (acct.id != null) settingService.acctFindById(acct.id!!).loginAcct else " "
        val loginAcctCount = settingService.loginAcctCount(acct.loginAcct)
        //校验登录账号是否存在
        var idAcct: Account? = null
        if (acct.idCardNo != "") idAcct = settingService.acctFindByIdCard(acct.idCardNo!!)
        if ((acct.id == null && loginAcctCount > 0) || (acct.id != null && originLoginAcct != acct.loginAcct && loginAcctCount > 0)) {
            result["errMsg"] = "登录账号已存在"
            result["returnCode"] = -1
        } else if (idAcct != null && acct.loginAcct != idAcct.loginAcct) {
            result["errMsg"] = "身份证号已存在"
            result["returnCode"] = -1
        } else {
            val resp = settingService.acctSave(acct, dptId, roleId)
            val jsonStr = request.getParameter("wxUsers")
            val acctObj = resp.id!! > 0
            settingService.batchAcctLinkerDel(resp.id!!)
            if (jsonStr != null) {
                // 保存选择的微信用户
                val jsonArr = jsonStr.split("|**|")
                for (s: String in jsonArr) {
                    val obj = JSONObject.parseObject(s)
                    val openId = obj.getString("openId")
                    var tempObj = settingService.acctWxLinker(openId, 2, resp)
                    if (tempObj == null) {
                        tempObj = WxLinker()
                    }
                    tempObj!!.name = obj.getString("nickname")
                    tempObj!!.subscribe = obj.getString("subscribe")
                    tempObj!!.type = 2
                    tempObj!!.acct = resp
                    tempObj!!.avatar = obj.getString("headImgUrl")
                    tempObj!!.appName = obj.getString("appName")
                    tempObj!!.openId = openId
                    tempObj!!.appKey = obj.getString("appKey")
                    settingService.wxLinkerSave(tempObj!!)
                }
            }
            result["returnCode"] = if (acctObj) 0 else -1
            result["currentUser"] = resp
            if (acct.id != null) result["originObj"] = resp else result["newObj"] = resp
            result["errMsg"] = if (acctObj) "" else "保存或更新失败"
        }
        return result
    }

    @GetMapping("acct/{id}/wxLinkers")
    fun acctWxLinkers(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = settingService.acctWxLinkers(id)
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
        } else if (oldPwd == newPwd) {
            resp = false
            result["errMsg"] = "新密码不能和旧密码相同"
        } else settingService.acctUpdatePwd(newPwd, uid)
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("acct/updateProfile")
    fun acctUpdateProfile(uid: Long, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        var email = req.getParameter("email")
        var phone = req.getParameter("phone")
        var avatar = req.getParameter("avatar")
        val orginAcct = settingService.acctFindById(uid)
        var resp = true
        if (!avatar.isNullOrBlank()) orginAcct.avatar = avatar
        if (phone != null && resp && (orginAcct.phone != phone)) {
            if (settingService.acctCount(phone, null) == 0) {
                orginAcct.phone = phone
            } else {
                resp = false
                result.put("errMsg", "手机号不能重复")
            }
        }
        if (!email.isNullOrBlank() && resp && (orginAcct.email != email)) {
            if (settingService.acctCount(null, email) == 0) {
                orginAcct.email = email
            } else {
                resp = false
                result.put("errMsg", "邮箱不能重复")
            }
        } else orginAcct.email = email
        if (resp) settingService.acctSave(orginAcct)
        result.put("returnCode", if (resp) 0 else -1)
        if (resp) result.put("currentUser", orginAcct)
        return result
    }

//    @PostMapping("acct/queryCombo")
//    fun accountCombo(pageSize: Int, acctName: String?): Map<String, Any> {
//        val result = HashMap<String, Any>()
//        val pg = PageRequest(0, pageSize, Sort.Direction.DESC, "updateAt")
//        val page = settingService.acctFindAll(acctName, pg)
//        result["returnCode"] = 0
//        result["list"] = page.content
//        return result
//    }

    /**
     * 根据权限查询业务员(姓名带"x"会转备注的中文名,传uid则根据权限查询)
     * @author mjj
     * @Date 2020-04-09
     */
    @PostMapping("acct/queryCombo")
    fun accountComboAuth(pageSize: Int, acctName: String?, uid: Long?): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(0, pageSize, Sort.Direction.DESC, "updateAt")
        val page = settingService.acctFindAllAuth(acctName, pg, uid)
        result["returnCode"] = 0
        result["list"] = settingService.changeName(page.content)
        return result
    }

    @GetMapping("acct/acctForXy")
    fun acctForXy(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = settingService.findAcctForXy()
        return result
    }

    @PostMapping("acct/batchUpdate")
    fun accountBatchUpdate(ids: String, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val lids = ids.split(",").map { s -> s.toLong() }
        settingService.acctBatchUpdateStatus(status, lids)
        result.put("returnCode", 0)
        return result
    }

    @DeleteMapping("acct/{id}")
    fun accountDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.acctDel(id)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("org")
    fun orgList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.orgFindList(req.getParameter("name"), req.getParameter("id"), req.getParameter("simpleName"), req.getParameter("remark"), pgRequest)
        result.put("returnCode", 0)
        result.put("list", pg.content)
        result.put("total", pg.totalElements)
        return result
    }

    @GetMapping("org/queryCombo")
    fun orgAll(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        result.put("list", settingService.orgAll())
        return result
    }

    @PostMapping("org/create")
    fun orgCreate(org: Organization): Map<String, Any> {
        val result = HashMap<String, Any>()
        log.info("org.name:>>${org.name}")
        val newObj = settingService.orgSave(org)
        if (newObj.id!! > 0) {
            result.put("returnCode", 0)
            result.put("newObj", newObj)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "保存失败")
        }
        return result
    }

    @PostMapping("org/update")
    fun orgUpdate(org: Organization): Map<String, Any> {
        val result = HashMap<String, Any>()
        val obj = settingService.orgUpdate(org)
        result.put("returnCode", 0)
        result.put("originObj", obj)
        return result
    }

    @DeleteMapping("org/{id}")
    fun orgDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.orgDel(id)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("org/batchUpdate")
    fun orgBatchUpdate(ids: String, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val lids = ids.split(",").map { s -> s.toLong() }
        settingService.orgBatchUpdateStatus(status, lids)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("dpt")
    fun dptList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.dptFindList(req.getParameter("name"), req.getParameter("id"), req.getParameter("orgName"), req.getParameter("orgShortName"), req.getParameter("remark"), pgRequest)
        result.put("returnCode", 0)
        result.put("list", pg.content)
        result.put("total", pg.totalElements)
        return result
    }

    @PostMapping("dpt/create")
    fun dptCreate(dpt: Dpt, orgId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val newObj = settingService.dptSave(dpt, orgId)
        if (newObj.id!! > 0) {
            result.put("returnCode", 0)
            result.put("newObj", newObj)
        } else {
            result.put("returnCode", -1)
            result.put("errMsg", "保存失败")
        }
        return result
    }

    @PostMapping("dpt/update")
    fun dptUpdate(dpt: Dpt, orgId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val obj = settingService.dptUpdate(dpt, orgId)
        result.put("returnCode", 0)
        result.put("originObj", obj)
        return result
    }

    @DeleteMapping("dpt/{id}")
    fun dptDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.dptDel(id)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("dpt/batchUpdate")
    fun dptBatchUpdate(ids: String, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val lids = ids.split(",").map { s -> s.toLong() }
        settingService.dptBatchUpdateStatus(status, lids)
        result.put("returnCode", 0)
        return result
    }

    @GetMapping("dpt/queryCombo")
    fun deptCombo(): Map<String, Any> {
        val result = java.util.HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = settingService.dptFindAll()
        return result
    }

    @GetMapping("menu")
    fun menuList(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("list", settingService.subMenuList())
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("role")
    fun roleList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.roleFindCommonList(req.getParameter("name"), req.getParameter("id"), pgRequest)
        val roleList = pg.content
        val menuAuthList = menuAuthService.findByRole(req.getParameter("name"), req.getParameter("id"))
        result["returnCode"] = 0
        result["list"] = roleList
        result["menuAuthList"] = menuAuthList
        result["total"] = pg.totalElements
        return result
    }

    @GetMapping("role/queryCombo")
    fun roleCombo(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        result.put("list", settingService.roleFindAllExSuper())
        return result
    }

    // 更新和修改一起
    @PostMapping("auth/role")
    fun authArrangeRole(ids: String, @RequestParam("authArr[]") authArr: Array<String>, role: Role): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        log.info("ids:>>>" + ids)
        log.info("array size:>> ${authArr.size}")
        authArr.map { s ->
            log.info("array item:>> $s")
        }
        val menuArray = ids.split(",")
        if (authArr.size != menuArray.size) {
            result.put("returnCode", -1)
            result.put("errMsg", "入参格式不正确")
        } else {
            // 保存角色并赋权限
            val resp = settingService.saveRoleAuth(role, menuArray, authArr)
            result.put("returnCode", if (resp) 0 else -1)
            result.put("errMsg", if (resp) "" else "保存失败")
        }
        return result
    }

    @PostMapping("auth/acct")
    fun accountAuthUpdate(ids: String, @RequestParam("authArr[]") authArr: Array<String>, acctId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("returnCode", 0)
        log.info("ids:>>>$ids")
        log.info("array size:>> ${authArr.size}")
        authArr.map { s ->
            log.info("array item:>> $s")
        }
        val menuArray = ids.split(",")
        if (authArr.size != menuArray.size) {
            result.put("returnCode", -1)
            result.put("errMsg", "入参格式不正确")
        } else {
            // 更新账号权限
            settingService.updateAcctAuth(acctId, menuArray, authArr)
            result.put("returnCode", 0)
        }
        return result
    }

    @DeleteMapping("role/{id}")
    fun roleDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.roleDelte(id)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("role/batchUpdate")
    fun roleBatchUpdate(ids: String, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val lids = ids.split(",").map { s -> s.toLong() }
        settingService.roleBatchUpdateStatus(status, lids)
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("task")
    fun taskList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.taskList(req.getParameter("year"), req.getParameter("month"), req.getParameter("type"), req.getParameter("orgName"), req.getParameter("dptName"), req.getParameter("acctName"), pgRequest)
        result["returnCode"] = 0
        result["list"] = pg.content
        result["total"] = pg.totalElements
        return result
    }

    @PostMapping("task/createOrUpdate")
    fun taskCreate(task: TaskPlanning): Map<String, Any> {
        val result = HashMap<String, Any>()
        task.compName = "江苏智恒达投资集团"
        var resp = checkTask(result, task)
        if (resp) {
            val newObj = settingService.taskCreateOrUpdate(task)
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

    @DeleteMapping("task/{id}")
    fun taskDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.taskDel(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("grading")
    fun gradeList(parentId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        if (parentId == CrmConstants.GRADE_COEFFICIENT_FORMULA) { //系数公式单独处理
            val grade = settingService.findParentGrade(parentId)
            result["grade"] = grade
        } else {
            val list = settingService.findGradeList(parentId)
            result["total"] = list.size
            result["list"] = list
        }
        result["returnCode"] = 0
        return result
    }

    @PostMapping("grading/createOrUpdate")
    fun gradeSave(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val grades = req.getParameterValues("grades[]")//格式['name|coefficient','id|name|coefficient']
        val parentId = req.getParameter("parentId")
        val type = req.getParameter("type")//1新增 2修改
        val name = req.getParameter("name")//系数公式时使用
        if (parentId != null) {
            settingService.gradeCreateOrUpdate(type, parentId.toLong(), grades, name)
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "保存失败"
        }
        return result
    }

    @DeleteMapping("grading/{id}")
    fun gradeDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        settingService.gradeDel(id)
        result["returnCode"] = 0
        return result
    }

    @GetMapping("grading/queryParent")
    fun queryParent(): Map<String, Any> {
        val result = HashMap<String, Any>()
        val list = settingService.findParentList()
        result["returnCode"] = 0
        result["list"] = list
        return result
    }

    @PostMapping("errorLog")
    fun queryErrLog(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = settingService.queryErrLog(req.getParameter("content"), req.getParameter("startTime"), req.getParameter("endTime"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("mqMsg")
    fun queryMqMsg(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = settingService.queryMqMsg(req.getParameter("content"), req.getParameter("startTime"), req.getParameter("endTime"), req.getParameter("msgType"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("mqResend/{id}")
    fun mqResend(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val msg = settingService.mqResend(id)
        if (msg == "") result["returnCode"] = 0
        else {
            result["returnCode"] = -1
            result["errMsg"] = msg
        }
        return result
    }

    @PostMapping("loginMsg")
    fun queryLoginMsg(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = settingService.loginMsgList(req.getParameter("acctName"), req.getParameter("startTime"), req.getParameter("endTime"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("loginMsg/create")
    fun loginMsgSave(loginAcct: String, ip: String, deviceType: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val acct = accountService.findByLoginAcct(loginAcct)
        if (acct != null) {
            val msg = LoginMsg()
            msg.acctId = acct.id
            msg.acctName = acct.name
            msg.ip = ip
            msg.deviceType = deviceType
            msg.loginAcct = loginAcct
            msg.loginDate = Timestamp(Date().time)
            val obj = settingService.loginMsgSave(msg)
            result["newObj"] = obj
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "登录账号[$loginAcct]不存在"
        }
        return result
    }

    //校验必填项，校验同年同月下机构/部门/业务员的数据只能有一个
    fun checkTask(result: HashMap<String, Any>, task: TaskPlanning): Boolean {
        var resp = true
        var orgName: String? = null
        var dptName: String? = null
        var acctCode: String? = null
        var newName: String? = null
        val type = task.type
        when (type) {
            1 -> {
                if (task.orgName != null) {
                    orgName = task.orgName
                    newName = orgName
                } else {
                    resp = false
                    result["errMsg"] = "机构名称不能为空"
                }
            }
            2 -> {
                if (task.dptName != null && task.dptCode != null) {
                    dptName = task.dptName
                    newName = dptName
                } else {
                    resp = false
                    result["errMsg"] = "部门名称不能为空"
                }
            }
            3 -> {
                if (task.acctName != null && task.acctCode != null) {
                    acctCode = task.acctCode
                    newName = task.acctName
                } else {
                    resp = false
                    result["errMsg"] = "业务员名称不能为空"
                }
            }
            else -> {
            }
        }
        val count = settingService.taskCountRepeats(task.year, task.month, type, null, orgName, dptName, acctCode)
        if (resp && type != 0) {
            if (task.id != null) { //修改
                val oraginObj = settingService.findTask(task.id!!)
                val oldName = if (task.type == 1) oraginObj.orgName else if (task.type == 2) oraginObj.dptName else oraginObj.acctName
                if (oldName != newName && count > 0) {
                    resp = false
                    result["errMsg"] = "${task.year}年${task.month}月下已存在${newName}的任务,不能修改"
                }
            } else { //新增
                if (count > 0) {
                    resp = false
                    result["errMsg"] = "${task.year}年${task.month}月下已存在${newName}的任务,不能新增"
                }
            }
        }
        return resp
    }

    /**
     * 2019年最新地址列表(省、市、区)
     * @author samy
     * @date 2020/06/08
     */
    @PostMapping("address/list")
    fun getAddressList(currentPage: Int, pageSize: Int, req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val pg = settingService.addressFindAllPg(req.getParameter("type"), req.getParameter("name"), pgRequest)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        return result
    }

    @PostMapping("address/city/list")
    fun getAddressCityList(currentPage: Int, pageSize: Int, req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        val pgRequest = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "update_At")
        val pg = settingService.addressFindByCityAllPg(req.getParameter("name"), pgRequest)
        result["returnCode"] = 0
        result["list"] = pg.content
        result["total"] = pg.totalElements
        return result
    }

    @PostMapping("menuAuth/save")
    fun saveMenuAuth(menuId: Array<Long> , req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        var exec = true
        var msg = ""
        val authName = req.getParameter("menuAuth[authName]")
        val authCode = req.getParameter("menuAuth[authCode]")
        val cssName = req.getParameter("menuAuth[cssName]")
        val methodName = req.getParameter("menuAuth[methodName]")
        if (StringUtils.isEmpty(authName)){
            msg += "权限名称为空!"
        }
        menuId.map { thisId ->{
            val theMenu = menuService.findById(thisId)
            if (theMenu?.id == null){
                msg += "菜单[$thisId]不存在!"
            }
            //校验名称
            val uniqueMenuAuth = menuAuthService.findUniqueMenuAuth(thisId,authName)
            if (uniqueMenuAuth?.id != null){
                msg += "权限名称不能重复!"
            }
        } }
        if (StringUtils.isNotEmpty(msg)){
            exec = false
            result["errMsg"] = msg
        }
        if (exec){
            for (thisId in menuId){
                val newMenuAuth = MenuAuth()
                newMenuAuth.menu =  menuService.findById(thisId)
                newMenuAuth.authName = authName
                newMenuAuth.authCode = authCode
                newMenuAuth.cssName = cssName
                newMenuAuth.methodName = methodName
                val newObj = menuAuthService.saveMenuAuth(newMenuAuth)
                if (newObj.id!! <= 0) {
                    exec = false
                    result["errMsg"] = "保存失败"
                    break
                }
            }
        }
        result["returnCode"] = if (exec) 0 else -1
        return result
    }


    @PostMapping("menuAuth/update")
    fun updateMenuAuth(menuId: Long , req: HttpServletRequest): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        var exec = true
        val id = req.getParameter("menuAuth[id]").toLong()
        val authName = req.getParameter("menuAuth[authName]")
        val authCode = req.getParameter("menuAuth[authCode]")
        val cssName = req.getParameter("menuAuth[cssName]")
        val methodName = req.getParameter("menuAuth[methodName]")
        val oldMenuAuth = menuAuthService.findById(id)
        if (oldMenuAuth?.id == null){
            exec = false
            result["errMsg"] = "单据不存在!"
        }
        val temp = menuAuthService.findUniqueMenuAuth(menuId,authName)
        if (null!= temp && temp.id!=id){
            exec = false
            result["errMsg"] = "权限名称不能重复!"
        }
        if (exec){
            oldMenuAuth.menu = menuService.findById(menuId)
            oldMenuAuth.authName = authName
            oldMenuAuth.authCode = authCode
            oldMenuAuth.cssName = cssName
            oldMenuAuth.methodName = methodName
            menuAuthService.saveMenuAuth(oldMenuAuth)
        }
        result["returnCode"] = if (exec) 0 else -1
        return result
    }

    @DeleteMapping("menuAuth/delete/{id}")
    fun deleteMenuAuth(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val menuAuth = menuAuthService.findById(id)
        var msg = ""
        if (menuAuth?.id == null){
            msg += "权限不存在!"
        }
        val extraAuthList =settingService.findExtraAuthByMenuId(id)
        if (extraAuthList.isNotEmpty()){
            msg +="该权限已被引用无法删除!"
        }
        if (StringUtils.isNotEmpty(msg)){
            result["returnCode"] = -1
            result["errMsg"] = msg
        }else{
            menuAuthService.deleteById(id)
            result["returnCode"] = 0
        }
        return result
    }

    @PostMapping("menuAuth/menuAuthList")
    fun findMenuAuthList(currentPage: Int,pageSize: Int,menuId:Long?,authName:String?):Map<String,Any>{
        var result = HashMap<String,Any>()
        var page = PageRequest(currentPage,pageSize)
        var pg = menuAuthService.findByPage(menuId,authName,page)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }


    @PostMapping("menu/menuList")
    fun findMenuList(currentPage: Int,pageSize: Int,name:String?):Map<String,Any>{
        var result = HashMap<String,Any>()
        var page = PageRequest(currentPage,pageSize)
        var pg = menuService.findMenuList(name,page)
        result["list"] = pg.content
        result["total"] = pg.totalElements
        result["returnCode"] = 0
        return result
    }


}