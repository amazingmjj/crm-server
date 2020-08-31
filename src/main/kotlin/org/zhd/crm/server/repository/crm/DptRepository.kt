package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Dpt
import javax.transaction.Transactional

interface DptRepository : CrudRepository<Dpt, Long> {

    fun findByName(name: String): Dpt?

    @Modifying
    @Query(nativeQuery = true, value = "update t_dpt set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(value = "from Dpt as d where (d.name like %?1% or ?1 is null) and (to_char(d.id) like %?2% or ?2 is null) and (d.fkOrg.name like %?3% or ?3 is null) and (d.fkOrg.simpleName like %?4% or ?4 is null) and (d.remark like %?5% or ?5 is null)")
    fun findAll(name: String?, id: String?, orgName: String?, orgShortName: String?, remark: String?, pageable: Pageable): Page<Dpt>

    @Query(value = "from Dpt as d where d.status = 1")
    fun findCommAll(): List<Dpt>

    @Query(value = "from Dpt as d where d.fkOrg.id = ?1")
    fun findListByOrgId(orgId: Long): List<Dpt>

    //不包含停用部门
    @Query(value = "from Dpt as d where d.status = 1 and (d.name like %?1% or ?1 is null)")
    fun findAllDpt(name: String?, pageable: Pageable): Page<Dpt>
}