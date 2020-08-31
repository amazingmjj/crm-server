package org.zhd.crm.server.repository.erp

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.erp.BasicFeeItem

interface BasicFeeItemRepository :CrudRepository<BasicFeeItem,Long> {

    @Query(value="from BasicFeeItem t where t.feeItemName=?1 and t.feeClassName=?2")
    fun findByItemName(name:String,className:String):BasicFeeItem
}