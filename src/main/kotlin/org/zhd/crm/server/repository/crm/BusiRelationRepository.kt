package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.BusiRelation
import javax.transaction.Transactional

interface BusiRelationRepository : CrudRepository<BusiRelation, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "update t_busi_relation set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT ROWNUM as rowno, t.* FROM T_BUSI_RELATION t where ROWNUM <= ?2 order by UPDATE_AT desc) table_alias WHERE table_alias.rowno >= ?1 ")
    fun findListPaginate(firstNow: Int, lastRow: Int): List<BusiRelation>

    @Query(value = "from BusiRelation as br where (br.name like %?1% or ?1 is null) and (to_char(br.id) like %?2% or ?2 is null)")
    fun findAll(name: String?, id: String?, pageable: Pageable): Page<BusiRelation>

    fun findByName(name: String): BusiRelation
}