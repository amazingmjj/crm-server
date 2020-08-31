package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerArrears

interface CustomerArrearsRepository : CrudRepository<CustomerArrears, Long> {
    @Query(nativeQuery = true, value = "select COALESCE(sum(arrear_amount), 0.0) from crm_customer_arrears where erp_code=?1")
    fun sumAmount(erpCode: String): Double
}