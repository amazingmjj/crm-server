package org.zhd.crm.server.service.crm

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.Project
import org.zhd.crm.server.model.crm.TaskCoefficient
import org.zhd.crm.server.model.crm.TaskType
import org.zhd.crm.server.model.crm.TeamTask
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.util.CommUtil
import javax.transaction.Transactional

@Service
@Transactional
class TeamTaskService {
    @Autowired
    lateinit var commUtil: CommUtil
    @Autowired
    lateinit var acctRepo: AccountRepository
    @Autowired
    lateinit var prjRepo: ProjectRepository
    @Autowired
    lateinit var taskTpRepo: TaskTypeRepository
    @Autowired
    lateinit var teamTaskRepo: TeamTaskRepository
    @Autowired
    lateinit var taskCofRepository: TaskCoefficientRepository

    private val log = LoggerFactory.getLogger(SettingService::class.java)

    //新增或修改项目信息
    fun projectSave(prj: Project, leaderId: Long): Project {
        var obj = prj
        val leader = acctRepo.findOne(leaderId)
        if (prj.id != null) {//更新
            obj = prjRepo.findOne(prj.id)
            obj.leader = leader
            obj = commUtil.autoSetClass(prj, obj, arrayOf("id", "updateAt", "createAt", "leader")) as Project
        } else {//新增
            obj.leader = leader
        }
        return prjRepo.save(obj)
    }

    //删除项目信息
    fun projectDelete(id: Long): String {
        val teamTask = teamTaskRepo.findByProjectId(id)
        var str = ""
        if (teamTask.isNotEmpty()) str = "已被任务关联，无法删除"
        else prjRepo.delete(id)
        return str
    }

    //分页查询项目信息
    fun findPrjList(name: String?, leader: String?, pg: Pageable) = prjRepo.findPrjList(name, leader, pg)

    //查询所有项目信息
    fun findPrjAll() = prjRepo.findAll()

    //查询单个项目
    fun findPrjById(id: Long) = prjRepo.findOne(id)

    //新增或修改任务类型
    fun taskTpSave(taskTp: TaskType): TaskType {
        var obj = taskTp
        if (taskTp.id != null) {//更新
            obj = taskTpRepo.findOne(taskTp.id)
            obj = commUtil.autoSetClass(taskTp, obj, arrayOf("id", "updateAt", "createAt")) as TaskType
        }
        return taskTpRepo.save(obj)
    }

    //删除任务类型
    fun taskTpDelete(id: Long): String {
        val teamTask = teamTaskRepo.findByTaskTpId(id)
        var str = ""
        if (teamTask.isNotEmpty()) str = "已被任务关联，无法删除"
        else taskTpRepo.delete(id)
        return str
    }

    //分页查询任务类型
    fun findTaskTpList(name: String?, id: String?, pg: Pageable) = taskTpRepo.findTaskTpList(name, id, pg)

    //查询所有任务类型
    fun findTaskTpAll() = taskTpRepo.findAll()

    //分页查询任务系数
    fun findTaskCofList(prjName: String?, taskId: String?, pg: Pageable) = taskCofRepository.findTaskCofList(prjName, taskId, pg)

    //删除任务系数
    fun taskCofDelete(id: Long) = taskCofRepository.delete(id)

    //新建或修改任务系数
    fun taskCofSave(taskCof: TaskCoefficient): TaskCoefficient{
        var obj = taskCof
        if (taskCof.id != null){
            val orgObj = taskCofRepository.findOne(taskCof.id)
            obj = commUtil.autoSetClass(taskCof, orgObj, arrayOf("id", "updateAt", "createAt")) as TaskCoefficient
        }
        return taskCofRepository.save(obj)
    }

    //保存任务
    fun teamTaskSave(teamTask: TeamTask) = teamTaskRepo.save(teamTask)

    //保存任务
    fun teamTaskSave(teamTask: TeamTask, fkTaskTypeId: Long, fkProjectId: Long, reporterId: Long, operatorId: Long, nameMark: Int): TeamTask {
        //处理关联
        val taskType = taskTpRepo.findOne(fkTaskTypeId)
        val project = prjRepo.findOne(fkProjectId)
        val reporter = acctRepo.findOne(reporterId)
        val operator = acctRepo.findOne(operatorId)
        teamTask.fkTaskType = taskType
        teamTask.fkProject = project
        teamTask.reporter = reporter
        teamTask.operator = operator
        return teamTaskRepo.save(teamTask)
    }

    //修改任务
    fun teamTaskUpdate(teamTask: TeamTask, originObj: TeamTask): TeamTask {
        //所有时间都单独处理
        val excludeArr = arrayOf("id", "updateAt", "createAt", "fkTaskType", "startTime", "endTime", "estimatedTime", "actualTime", "deliveryTime", "fkProject", "reporter", "operator", "fkParent")
        val obj = commUtil.autoSetClass(teamTask, originObj, excludeArr) as TeamTask
        if (obj.fkParent != null) {
            val parentObj = teamTaskRepo.findOne(obj.fkParent!!.id)
            log.info(">>>更新父记录中的子状态")
            parentObj.subStatus = obj.status
            teamTaskRepo.save(parentObj)
        }
        return teamTaskRepo.save(obj)
    }

    //查某个任务
    fun findTeamTaskById(id: Long) = teamTaskRepo.findOne(id)

    //删除任务
    fun teamTaskDelete(id: Long): String{
        val list = teamTaskRepo.findSubTaskList(id)
        var str = ""
        if (list.isNotEmpty()) str = "存在子节点,无法删除"
        else {
            val originObj = teamTaskRepo.findOne(id)
            originObj.fkParent = null
            val obj = teamTaskRepo.save(originObj)
            teamTaskRepo.delete(obj.id)
        }
        return str
    }

    //分页查询任务
    fun findTeamTaskList(queryMark: Int, taskName: String?, projectId: String?, taskTypeId: String?, operatorId: String?, status: String?, pg: Pageable): Page<TeamTask> {
        if (queryMark == 0) return teamTaskRepo.findDeveloperTask(taskName, projectId, taskTypeId, operatorId, status, pg)
        else return teamTaskRepo.findTesterTask(taskName, projectId, taskTypeId, operatorId, status, pg)
    }

    //查询当前以及相关任务
    fun findTaskListById(id: Long): List<TeamTask>{
        val list = ArrayList<TeamTask>()
        val originObj = teamTaskRepo.findOne(id)
        if (originObj != null){
            if (originObj.fkParent != null){//子记录需要获取所属主记录
                val mainObj = teamTaskRepo.findOne(originObj.fkParent!!.id)
                list.add(mainObj)
                list.add(originObj)
            } else {//主记录需要获取旗下所有子记录
                list.add(originObj)
                val subObj = teamTaskRepo.findSubTaskList(id)
                subObj.map { obj ->
                    list.add(obj)
                }
            }
        } else {
            log.info(">>>找不到当前{$id}记录")
        }
        return list
    }

    //查询系数，优先取自定义系数，否则取项目系数
    fun findTaskCoefficient(projectId: Long, taskId: Long): String{
        var result = ""
        val cof = taskCofRepository.findByTaskId(taskId)
        if (cof == null) {
            log.info(">>>不存在自定义系数")
            val prjCof = taskCofRepository.findByProjectId(projectId)
            result = if (prjCof == null) "1.0,0.8,0.5" else prjCof.value
        } else result = cof.value
        return result
    }

    //获取同一单子开发次数
    fun taskRepeatCount(taskId: Long) = teamTaskRepo.taskRepeatCount(taskId)
}