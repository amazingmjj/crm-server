package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.ExtraAuth

interface ExtraAuthRepository:CrudRepository<ExtraAuth,Long> {

    @Query("from ExtraAuth t where t.fkMenuAuth.id = ?1")
    fun findByMenuId(id:Long) : List<ExtraAuth>
}