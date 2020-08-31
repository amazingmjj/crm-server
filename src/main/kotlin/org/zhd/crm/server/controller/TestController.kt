package org.zhd.crm.server.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.model.crm.Account
import org.zhd.crm.server.service.ScheduleService
import org.zhd.crm.server.service.WebSocketService
import org.zhd.crm.server.service.crm.EntityManageService
import org.zhd.crm.server.service.crm.TestService
import org.zhd.crm.server.service.statistic.GradingService
import java.io.IOException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("crmTest")
@Profile("dev")
class TestController{
    @Autowired
    private lateinit var gradingService: GradingService
    @Autowired
    private lateinit var scheduleService: ScheduleService
    @Autowired
    private lateinit var testService: TestService
    @Autowired
    private lateinit var entityManageService: EntityManageService
    @Autowired(required = false)
    private lateinit var webSocketService: WebSocketService

    private val log = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("schedule")
    fun customerClassify(): String{
        log.info(">>>开始导入客户分级")
        gradingService.batchSaveCstmClassify()
        return "success"
    }

    @GetMapping("linker")
    fun customerLost(): String{
        log.info(">>>开始自动流失正式客户")
        scheduleService.cstmLost()
        return "success"
    }

    @GetMapping("relationSplit")
    fun relationSplit(): String{
        log.info(">>>开始修复一个联系人对应多个客户的数据")
        testService.splitLinkAndCstmRelation()
        return "success"
    }

    @PostMapping("classifyRepeat")
    fun classifyRepeat(createAt: String): String{
        log.info(">>>开始修复客户分级重复两条的数据")
        testService.delRepeatData(createAt)
        return "success"
    }

    @GetMapping("handleData")
    fun handleData(): String{
        log.info(">>>开始修复一个客户有两条主联系人的数据")
        testService.handleMainLink()
        return "success"
    }

    @GetMapping("sqlTest")
    fun sqlTest(req: HttpServletRequest): Map<String, Any> {
        val mainStr = "select t.* from t_account t where 1=1 "
        val mainQueryMap = HashMap<String, Any>()
        mainQueryMap.put("name", "and t.name like '%#%'")
        val countStr = "select count(1) from t_account t where 1=1"
        return entityManageService.nativeQuery(req, mainStr,countStr, Account::class.java, mainQueryMap, arrayOf("fkDpt", "fkRole", "auths"))
    }

    @PostMapping("acct")
    fun acctAll(req: HttpServletRequest): Map<String, Any> {
        val mainStr = "select at.remark,at.id,at.name from Account at"
        val queryMap = HashMap<String, String>()
        queryMap["name"] = "and at.name like '%#%'"
        queryMap["loginAcct"] = "and at.loginAcct like '%#%'"
        queryMap["orgName"] = "and at.fkDpt.fkOrg.name like '%#%'"
        queryMap["dptName"] = "and at.fkDpt.name like '%#%'"
        queryMap["position"] = "and at.position like '%#%'"
        queryMap["phone"] = "and at.phone like '%#%'"
        queryMap["roleName"] = "and at.fkRole.name like '%#%'"
        queryMap["append"] = "and at.id <> 1 order by at.createAt desc"
        return entityManageService.pageForQuery(req,mainStr,queryMap,arrayOf("fkDpt", "fkRole", "auths", "customers"),false)
    }

    @PostMapping("cstmAll")
    fun cstmAll(req: HttpServletRequest): Map<String, Any> {
        val mark = req.getParameter("mark").toInt()
        req.setAttribute("acctId", 12)
        val mainStr = "select a.* from v_cstm_list a"
        val queryMap = HashMap<String, String>()
        queryMap["compName"] = "and a.comp_name like '%#%'"
        queryMap["linkName"] = "and a.linkName like '%#%'"
        queryMap["linkPhone"] = "and a.linkPhone like '%#%'"
        queryMap["startTime"] = "and to_char(a.create_at,'yyyy-MM-dd') >= '#'"
        queryMap["endTime"] = "and to_char(a.create_at,'yyyy-MM-dd') <= '#'"
        queryMap["dptName"] = "and a.dptName like '%#%'"
        queryMap["acctName"] = "and a.acctName like '%#%'"
        queryMap["mark"] = "and a.mark = '#'"
        queryMap["acctId"] = "and to_char(a.acctId) = '#'"
        queryMap["dptId"] = "and to_char(a.dptId) = '#'"
        queryMap["orgId"] = "and to_char(a.orgId) = '#'"
        queryMap["status"] = "and to_char(a.status) = '#'"
        queryMap["append"] = "order by update_at desc"
        return entityManageService.pageForQuery(req,mainStr,queryMap,null,true)
    }

    @PostMapping("busiOppty")
    fun opptyAll(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any>{
        val mainStr = "from BusinessOpportunity bo"
        val queryMap = HashMap<String, String>()
        queryMap["opptyName"] = "and bo.opptyName like '%#%'"
        queryMap["loginAcct"] = "and bo.linkerName like '%#%'"
        queryMap["orgName"] = "and bo.linkerPhone like '%#%'"
        queryMap["dptName"] = "and bo.cstmName like '%#%'"
        queryMap["position"] = "and bo.creator.fkDpt.name like '%#%'"
        queryMap["phone"] = "and bo.creator.name like '%#%'"
        queryMap["roleName"] = "and to_char(bo.createAt,'yyyy-MM-dd') = '#'"
        queryMap["roleName"] = "and bo.opptyAddr like '%#%'"
        queryMap["append"] = "order by bo.createAt desc"
        return entityManageService.pageForQuery(req,mainStr,queryMap, arrayOf("creator"),false)
    }

    //推送数据接口
    @GetMapping("/socket/push/{cid}")
    fun pushToWeb(@PathVariable cid: String?, message: String): String {
        try {
            WebSocketService.sendInfo(message, cid)
        } catch (e: IOException) {
            e.printStackTrace();
        }
        return ">>>$cid";
    }
}