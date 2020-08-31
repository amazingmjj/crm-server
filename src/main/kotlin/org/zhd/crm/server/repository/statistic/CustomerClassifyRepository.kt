package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerClassify

interface CustomerClassifyRepository : CrudRepository<CustomerClassify, Long> {
    @Query(value = "from CustomerClassify as cstmClassify where cstmClassify.name = ?1 and to_char(cstmClassify.dealDate, 'YYYY-MM-DD')=?2 and cstmClassify.type=?3 and cstmClassify.erpCode=?4")
    fun findObject(name: String, date: String, type: String, erpCode: String): CustomerClassify?
}