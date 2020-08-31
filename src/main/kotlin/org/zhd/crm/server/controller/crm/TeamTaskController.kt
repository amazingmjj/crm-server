package org.zhd.crm.server.controller.crm

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.Project
import org.zhd.crm.server.model.crm.TaskCoefficient
import org.zhd.crm.server.model.crm.TaskType
import org.zhd.crm.server.model.crm.TeamTask
import org.zhd.crm.server.util.CrmConstants
import org.zhd.crm.server.util.MathUtil
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@RestController
@RequestMapping("taskManage")
class TeamTaskController : CrmBaseController() {

    private val log = LoggerFactory.getLogger(TeamTaskController::class.java)

    @PostMapping("project")
    fun projectList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = teamTaskService.findPrjList(req.getParameter("name"), req.getParameter("leader"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("project/createOrUpdate")
    fun projectCreate(project: Project, leaderId: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val newObj = teamTaskService.projectSave(project, leaderId)
        if (newObj.id!! > 0) {
            result["returnCode"] = 0
            result["newObj"] = newObj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "保存失败"
        }
        return result
    }

    @DeleteMapping("project/{id}")
    fun projectDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val str = teamTaskService.projectDelete(id)
        if (str == "") {
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = str
        }
        return result
    }

    @GetMapping("project/queryCombo")
    fun projectCombo(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = teamTaskService.findPrjAll()
        return result
    }

    @PostMapping("taskType")
    fun taskTypeList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = teamTaskService.findTaskTpList(req.getParameter("name"), req.getParameter("id"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("taskType/createOrUpdate")
    fun taskTypeCreate(taskType: TaskType): Map<String, Any> {
        val result = HashMap<String, Any>()
        val newObj = teamTaskService.taskTpSave(taskType)
        if (newObj.id!! > 0) {
            result["returnCode"] = 0
            result["newObj"] = newObj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "保存失败"
        }
        return result
    }

    @DeleteMapping("taskType/{id}")
    fun taskTypeDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val str = teamTaskService.taskTpDelete(id)
        if (str == "") {
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = str
        }
        return result
    }

    @GetMapping("taskType/queryCombo")
    fun taskTypeCombo(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = teamTaskService.findTaskTpAll()
        return result
    }

    @PostMapping("teamTask")
    fun teamTaskList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "create_at")
        val page = teamTaskService.findTeamTaskList(req.getParameter("queryMark").toInt(), req.getParameter("taskName"), req.getParameter("projectId"), req.getParameter("taskTypeId"), req.getParameter("operatorId"), req.getParameter("status"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("teamTask/{id}")
    fun findTeamTask(@PathVariable("id") id: Long): Map<String, Any>{
        val result = HashMap<String, Any>()
        val list = teamTaskService.findTaskListById(id)
        result["returnCode"] = 0
        result["list"] = list
        return result
    }

    @PostMapping("teamTask/create")
    fun teamTaskCreate(teamTask: TeamTask, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val nameMark = req.getParameter("nameMark").toInt()
        val fkParentId = req.getParameter("fkParentId")
        //处理名称,状态和所属
        val taskName = if (nameMark == 1) "测试-${teamTask.taskName}" else if (nameMark == 2) "再次修改-${teamTask.taskName}" else if (nameMark == 3) "再次测试-${teamTask.taskName}" else teamTask.taskName
        teamTask.taskName = taskName
        val status = if (nameMark == 1 || nameMark == 3) CrmConstants.TASK_UNDER_TEST else CrmConstants.TASK_PENDING
        teamTask.status = status
        teamTask.belong = if (nameMark == 1 || nameMark == 3) 1 else 0
        //更新子状态
        if (nameMark != 0) {
            if (fkParentId == null){
                resp = false
                result["errMsg"] = "不存在父id"
            } else {
                val parentObj = teamTaskService.findTeamTaskById(fkParentId.toLong())
                log.info(">>>更新父记录中的子状态")
                parentObj.subStatus = status
                val obj = teamTaskService.teamTaskSave(parentObj)
                teamTask.fkParent = obj
            }
        }
        if (resp) {
            //获取字段
            val fkTaskTypeId = req.getParameter("fkTaskTypeId").toLong()
            val fkProjectId = req.getParameter("fkProjectId").toLong()
            val reporterId = req.getParameter("reporterId").toLong()
            val operatorId = if (req.getParameter("operatorId") != null) req.getParameter("operatorId").toLong() else reporterId
            //处理时间
            val estimatedTimeStr = req.getParameter("estimatedTimeStr")
            val deliveryTimeStr = req.getParameter("deliveryTimeStr")
            val longSdf = SimpleDateFormat("yyyy-MM-dd HH")
            teamTask.estimatedTime = if (estimatedTimeStr == null) 0.0 else estimatedTimeStr.toDouble()
            teamTask.deliveryTime = if (deliveryTimeStr == null) null else Timestamp(longSdf.parse(deliveryTimeStr).time)
            //保存
            val newObj = teamTaskService.teamTaskSave(teamTask, fkTaskTypeId, fkProjectId, reporterId, operatorId, nameMark)
            log.info(">>>create succeed")
            result["newObj"] = newObj
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @PostMapping("teamTask/update")
    fun teamTaskUpdate(teamTask: TeamTask, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        var resp = true
        val reasonMap = mapOf(CrmConstants.TASK_NO_PROCESSING to "暂不处理", CrmConstants.TASK_RETURN to "退回", CrmConstants.TASK_CLOSE to "关闭")
        val timeArr = arrayOf(CrmConstants.TASK_UNDER_WAY, CrmConstants.TASK_IN_TEST)
        val finishArr = arrayOf(CrmConstants.TASK_COMPLETE, CrmConstants.TASK_FINISH, CrmConstants.TASK_RETURN)
        val longSdf = SimpleDateFormat("yyyy-MM-dd HH")
        val estimatedTimeStr = req.getParameter("estimatedTimeStr")
        val actualTimeStr = req.getParameter("actualTimeStr")
        val originObj = teamTaskService.findTeamTaskById(teamTask.id!!)
        log.info(">>>${originObj.status},${teamTask.status}")
        if (originObj.status != teamTask.status) {//状态变更
            if (teamTask.status in reasonMap.keys && teamTask.reason == null) {
                resp = false
                result["errMsg"] = "请填写${reasonMap[teamTask.status]}原因"
            }
            if (teamTask.status in timeArr && originObj.estimatedTime == 0.0 && estimatedTimeStr == null) {
                resp = false
                result["errMsg"] = "请填写预计时间"
            }
            if (teamTask.status in finishArr && originObj.actualTime == 0.0 && actualTimeStr == null) {
                resp = false
                result["errMsg"] = "请填写实际时间"
            }
        }
        if (resp) {
            val reporterId = req.getParameter("reporterId").toLong()
            val operatorId = req.getParameter("operatorId")
            val deliveryTimeStr = req.getParameter("deliveryTimeStr")
            if (operatorId != null && originObj.operator.id != operatorId.toLong()) {//有值且变化表明移交操作
                originObj.reporter = settingService.acctFindById(reporterId.toLong())//操作员
                originObj.operator = settingService.acctFindById(operatorId.toLong())
            }
            //时间单独处理
            if (!deliveryTimeStr.isNullOrBlank()) originObj.deliveryTime = Timestamp(longSdf.parse(deliveryTimeStr).time)
            if (!estimatedTimeStr.isNullOrBlank()) originObj.estimatedTime = estimatedTimeStr.toDouble()
            if (!actualTimeStr.isNullOrBlank()) originObj.actualTime = actualTimeStr.toDouble()
            if (teamTask.status in timeArr) originObj.startTime = Timestamp(Date().time)
            if (teamTask.status in finishArr) originObj.endTime = Timestamp(Date().time)
            teamTaskService.teamTaskUpdate(teamTask, originObj)
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @DeleteMapping("teamTask/{id}")
    fun teamTaskDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val str = teamTaskService.teamTaskDelete(id)
        if (str == "") {
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = str
        }
        return result
    }

    @GetMapping("taskStatistic/{id}")
    fun teamTaskStatistic(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val teamTask = teamTaskService.findTeamTaskById(id)
        //质量=实际时间*系数
        var quality = 0.0
        if (teamTask.actualTime != 0.0) {
            val cof = teamTaskService.findTaskCoefficient(teamTask.fkProject.id!!, teamTask.id!!)
            val cofs = cof.split(",").map { s -> s.toDouble() }
            val count = teamTaskService.taskRepeatCount(teamTask.id!!)
            quality = if (count == 1) MathUtil.mul(teamTask.actualTime, cofs[0]) else if (count == 2) MathUtil.mul(teamTask.actualTime, cofs[1]) else if (count > 2) MathUtil.mul(teamTask.actualTime, cofs[2]) else 0.0
        }
        //效率=实际时间/（结束时间-开始时间）
        var efficiency = 0.0
        if (teamTask.startTime != null && teamTask.endTime != null){
            val diff = MathUtil.longDataDiv((teamTask.endTime!!.time - teamTask.startTime!!.time),(1000*60*60))
            efficiency = MathUtil.div(teamTask.actualTime, diff, MathUtil.DECIMAL_PLACE_THREE) * 100
        }
        val statusMap = mapOf(0 to "待处理", 1 to "暂不处理", 2 to "进行中", 3 to "开发完成", 4 to "待验证", 5 to "测试中", 6 to "已完成", 7 to "退回", 8 to "关闭", -1 to "无")
        result["returnCode"] = 0
        result["quality"] = quality
        result["efficiency"] = efficiency
        result["status"] = statusMap[teamTask.status]!!
        result["subStatus"] = statusMap[teamTask.subStatus]!!
        return result
    }

    @PostMapping("taskCof")
    fun taskCofList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = teamTaskService.findTaskTpList(req.getParameter("prjName"), req.getParameter("taskId"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("taskCof/createOrUpdate")
    fun taskCofCreate(taskCof: TaskCoefficient, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val fkProjectId = req.getParameter("fkProjectId")
        var resp = true
        if (fkProjectId == null && taskCof.taskId == null){
            resp = false
            result["errMsg"] = "关联项目或者关联任务至少选择一个"
        }
        if (resp){
            if (fkProjectId != null){
                taskCof.fkProject = teamTaskService.findPrjById(fkProjectId.toLong())
                taskCof.type = 1
            }
            if (taskCof.taskId != null) taskCof.type = 0
            val newObj = teamTaskService.taskCofSave(taskCof)
            result["newObj"] = newObj
        }
        result["returnCode"] = if (resp) 0 else -1
        return result
    }

    @DeleteMapping("taskCof/{id}")
    fun taskCofDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        teamTaskService.taskCofDelete(id)
        result["returnCode"] = 0
        return result
    }
}