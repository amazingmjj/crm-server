package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Organization
import javax.transaction.Transactional

interface OrganizationRepository : CrudRepository<Organization, Long> {

    fun findByName(name: String): Organization?

    @Query(value = "from Organization as org where (org.name like %?1% or ?1 is null) and (to_char(org.id) like %?2% or ?2 is null) and (org.simpleName like %?3% or ?3 is null) and (org.remark like %?4% or ?4 is null)")
    fun findAll(name: String?, id: String?, simpleName: String?, remark: String?, pageable: Pageable): Page<Organization>

    @Modifying
    @Query(nativeQuery = true, value = "update t_organization set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(value = "from Organization as org where org.status = 1")
    fun findCommAll(): List<Organization>
}