package org.zhd.crm.server.repository.erp

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.erp.PartsName

interface PartsNameRepository : CrudRepository<PartsName, Long> {
    //    @Query("from PartsName t where 1=1 and (t.name like %?1% or ?1 is null)")
    @Query(nativeQuery = true, value = "SELECT PNTREE_NAME AS PARTSNAME_NAME, max(partsname_id) AS partsname_id FROM BASIC_PARTSNAME where 1=1 and (pntree_name like %?1% or ?1 is null) GROUP BY pntree_name")
    fun findByName(name: String?): List<PartsName>
}