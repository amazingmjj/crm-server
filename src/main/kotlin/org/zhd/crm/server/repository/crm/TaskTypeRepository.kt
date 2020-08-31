package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.TaskType

interface TaskTypeRepository: CrudRepository<TaskType, Long>{
    @Query(value = "from TaskType a where (instr(a.name,?1)>0 or ?1 is null) and (instr(to_char(a.id),?2)>0 or ?2 is null)")
    fun findTaskTpList(name: String?, id: String?, pageable: Pageable): Page<TaskType>
}