package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.SaleOtherWeight
import javax.transaction.Transactional

interface SaleOtherWeightRepository : CrudRepository<SaleOtherWeight,Long> {

    @Modifying
    @Query(value = "update SaleOtherWeight t set t.weight = ?2 where t.id = ?1")
    @Transactional
    fun updateById(id: Long, weight: Double)

    @Query(value = "from SaleOtherWeight t where (t.yearMonth = ?1 or ?1 is null) and (t.empId = ?2 or ?2 is null)")
    fun findByPage(ny:String?,empCode:String?,pageable: Pageable):Page<SaleOtherWeight>
}