package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Communicate

interface CommunicateRepository: CrudRepository<Communicate, Long> {
	@Query(value = "from Communicate as cmut where cmut.fkCstm.id = ?1")
	fun findAll(id:Long): List<Communicate>
}