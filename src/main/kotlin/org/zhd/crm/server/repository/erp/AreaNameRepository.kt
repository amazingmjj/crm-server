package org.zhd.crm.server.repository.erp

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.erp.AreaName

interface AreaNameRepository : CrudRepository<AreaName, Long> {
    @Query("select new map(max(t.id) as id, t.name as name) from AreaName t where 1=1 and (t.name like %?1% or ?1 is null) group by t.name")
//    @Query(nativeQuery = true, value = "select new max(AREA_ID) as id, AREA_NAME from BASIC_AREA where 1=1 and (AREA_NAME like %?1% or ?1 is null) group by AREA_NAME")
    fun findByName(name: String?): List<Any>
}