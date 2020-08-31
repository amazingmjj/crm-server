package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Project

interface ProjectRepository: CrudRepository<Project, Long>{
    @Query(value = "from Project a where (instr(a.name,?1) > 0 or ?1 is null) and (instr(a.leader.name,?2) > 0 or ?2 is null)")
    fun findPrjList(name: String?, leaderName: String?, pageable: Pageable): Page<Project>
}