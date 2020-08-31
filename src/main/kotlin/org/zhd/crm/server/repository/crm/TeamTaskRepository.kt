package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.TeamTask

interface TeamTaskRepository: CrudRepository<TeamTask, Long>{
    @Query(value = "from TeamTask a where a.fkProject.id = ?1")
    fun findByProjectId(id: Long): List<TeamTask>

    @Query(value = "from TeamTask a where a.fkTaskType.id = ?1")
    fun findByTaskTpId(id: Long): List<TeamTask>

    @Query(value = "from TeamTask a where (a.taskName like %?1% or ?1 is null) and (to_char(a.fkProject.id) = ?2 or ?2 is null) and (to_char(a.fkTaskType.id) = ?3 or ?3 is null) and (to_char(a.operator.id) = ?4 or ?4 is null) and (to_char(a.status) = ?5 or ?5 is null)")
    fun findTeamTaskList(taskName: String?, projectId: String?, taskTypeId: String?, operatorId: String?, status: String?, pageable: Pageable): Page<TeamTask>

    @Query(value = "from TeamTask a where a.fkParent.id = ?1 order by a.createAt asc")
    fun findSubTaskList(id: Long): List<TeamTask>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_developer_task a where (a.task_name like %?1% or ?1 is null) and (to_char(a.fk_project_id) = ?2 or ?2 is null) and (to_char(a.fk_task_type_id) = ?3 or ?3 is null) and (to_char(a.operator_id) = ?4 or ?4 is null) and (to_char(a.status) = ?5 or ?5 is null) order by ?#{#pageable}", value = "select * from v_developer_task a where (a.task_name like %?1% or ?1 is null) and (to_char(a.fk_project_id) = ?2 or ?2 is null) and (to_char(a.fk_task_type_id) = ?3 or ?3 is null) and (to_char(a.operator_id) = ?4 or ?4 is null) and (to_char(a.status) = ?5 or ?5 is null) order by ?#{#pageable}")
    fun findDeveloperTask(taskName: String?, projectId: String?, taskTypeId: String?, operatorId: String?, status: String?, pageable: Pageable): Page<TeamTask>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_tester_task a where (a.task_name like %?1% or ?1 is null) and (to_char(a.fk_project_id) = ?2 or ?2 is null) and (to_char(a.fk_task_type_id) = ?3 or ?3 is null) and (to_char(a.operator_id) = ?4 or ?4 is null) and (to_char(a.status) = ?5 or ?5 is null) order by ?#{#pageable}", value = "select * from v_tester_task a where (a.task_name like %?1% or ?1 is null) and (to_char(a.fk_project_id) = ?2 or ?2 is null) and (to_char(a.fk_task_type_id) = ?3 or ?3 is null) and (to_char(a.operator_id) = ?4 or ?4 is null) and (to_char(a.status) = ?5 or ?5 is null) order by ?#{#pageable}")
    fun findTesterTask(taskName: String?, projectId: String?, taskTypeId: String?, operatorId: String?, status: String?, pageable: Pageable): Page<TeamTask>

    @Query(nativeQuery = true, value = "select count(1) from (select a.id from t_team_task a where a.id = ?1 and a.belong = 0 union select b.id from t_team_task b where b.fk_parent_id = ?1 and b.belong = 0)")
    fun taskRepeatCount(taskId: Long): Int
}