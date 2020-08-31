package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerDealCount

interface CustomerDealCountRepository : CrudRepository<CustomerDealCount, Long> {

    @Query(value = "select COALESCE(sum(dc.dealCount),0.0) from CustomerDealCount as dc where dc.erpCode=?1")
    fun sumCount(erpCode: String): Int
}