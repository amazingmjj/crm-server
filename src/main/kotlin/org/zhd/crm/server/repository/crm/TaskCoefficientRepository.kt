package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.TaskCoefficient

interface TaskCoefficientRepository: CrudRepository<TaskCoefficient, Long>{
    @Query(value = "from TaskCoefficient a where a.taskId = ?1 and a.status = 0 and a.type = 0")
    fun findByTaskId(taskId: Long): TaskCoefficient?

    @Query(value = "from TaskCoefficient a where a.fkProject.id = ?1 and a.status = 0 and a.type = 1")
    fun findByProjectId(projectId: Long): TaskCoefficient?

    @Query(value = "from TaskCoefficient a where (instr(a.fkProject.name,?1)>0 or ?1 is null) and (instr(to_char(a.taskId),?2)>0 or ?2 is null)")
    fun findTaskCofList(prjName: String?, taskId: String?, pg: Pageable): Page<TaskCoefficient>
}