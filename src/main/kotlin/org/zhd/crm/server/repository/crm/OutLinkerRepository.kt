package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.OutLinker

interface OutLinkerRepository: CrudRepository<OutLinker, Long>{
    @Query(value = "select count(ol.id) from OutLinker ol where ol.phone = ?1")
    fun countPhone(phone: String): Int

    @Query(value = "from OutLinker ol where (ol.name like %?1% or ?1 is null) and (ol.phone like %?2% or ?2 is null) and (ol.label like %?3% or ?3 is null) and (ol.remark like %?4% or ?4 is null)")
    fun outLinker(name: String?, phone: String?, label: String?, remark: String?, pageable: Pageable): Page<OutLinker>

    @Query(value = "from OutLinker ol where (ol.name like %?1% or ?1 is null) and (ol.phone like %?2% or ?2 is null) and (ol.label like %?3% or ?3 is null) and (ol.remark like %?4% or ?4 is null)")
    fun outLinkerAll(name: String?, phone: String?, label: String?, remark: String?): List<OutLinker>

    fun findByPhone(phone: String): OutLinker?
}