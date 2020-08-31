package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CombineRecord

interface CombineRecordRepository: CrudRepository<CombineRecord, Long> {
    @Query(value = "from CombineRecord as cr where (cr.oldCustName like %?1% or ?1 is null) and (cr.newCustName like %?2% or ?2 is null)")
    fun combineRecordList(oldCustName: String?, newCustName: String?, pageable: Pageable): Page<CombineRecord>
}