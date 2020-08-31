package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.LinkerModify

interface LinkerModifyRepository : CrudRepository<LinkerModify,Long>{
	@Query(value = "from LinkerModify as lm where lm.linker.id = ?1")
	fun findAll(id: Long,pageable: Pageable) : Page<LinkerModify>
}