package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.BusinessOpportunity
import javax.transaction.Transactional


interface BusiOpptyRepository : CrudRepository<BusinessOpportunity, Long> {
    @Query(value = "from BusinessOpportunity as bo where (bo.opptyName like %?1% or ?1 is null) and (to_char(bo.creator.fkDpt.fkOrg.id) = ?2 or ?2 is null) and (to_char(bo.creator.fkDpt.id) = ?3 or ?3 is null) and (to_char(bo.creator.id) = ?4 or ?4 is null)")
    fun findBusiOpptyAll(opptyName: String?, orgId: String?, dptId: String?, acctId: String?, pg: Pageable): Page<BusinessOpportunity>

    @Modifying
    @Query(value = "update BusinessOpportunity bo set bo.cstmName = ?2 where bo.cstmCode = ?1")
    @Transactional
    fun updateByCstmCode(cstmId: String, cstmName: String)

    @Query(value = "from BusinessOpportunity bo where (bo.opptyName like %?1% or ?1 is null) and (bo.linkerName like %?2% or ?2 is null) and (bo.linkerPhone like %?3% or ?3 is null) and (bo.cstmName like %?4% or ?4 is null) and (bo.creator.fkDpt.name like %?5% or ?5 is null) and (bo.creator.name like %?6% or ?6 is null) and (to_char(bo.createAt,'yyyy-MM-dd') = ?7 or ?7 is null) and (bo.opptyAddr like %?8% or ?8 is null)")
    fun findBusiOpptyAll(opptyName: String?, linkerName: String?, linkerPhone: String?, cstmName: String?, dptName: String?, acctName: String?, createAt: String?, opptyAddr: String?, pg: Pageable): Page<BusinessOpportunity>
}