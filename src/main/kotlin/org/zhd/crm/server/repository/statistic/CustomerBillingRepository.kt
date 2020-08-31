package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerBilling

interface CustomerBillingRepository : CrudRepository<CustomerBilling, Long> {
    @Query(value = "select count(billing.erpCode) from CustomerBilling as billing where billing.erpCode=?1")
    fun billingCount(erpCode: String): Int
}