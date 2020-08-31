package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.SupplyCatalog
import javax.transaction.Transactional

interface SupplyCatalogRepository : CrudRepository<SupplyCatalog, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "update t_supply_catalog set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(value = "from SupplyCatalog as br where (br.name like %?1% or ?1 is null) and (to_char(br.id) like %?2% or ?2 is null)")
    fun findAll(name: String?, id: String?, pageable: Pageable): Page<SupplyCatalog>

    fun findByName(name : String): SupplyCatalog
}