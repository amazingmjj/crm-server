package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerExpt

interface CustomerExptRepository : CrudRepository<CustomerExpt, String> {

    @Query(value = "from CustomerExpt as cstmExpt where cstmExpt.erpCode=?1")
    fun findListByErpCode(erpCode: String): List<CustomerExpt>
}