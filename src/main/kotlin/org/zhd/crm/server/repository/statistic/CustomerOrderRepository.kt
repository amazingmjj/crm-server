package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.CustomerOrder
import java.sql.Timestamp

interface CustomerOrderRepository : CrudRepository<CustomerOrder, Long> {
    @Query(value = "from CustomerOrder as cod where cod.acctNo=?1 and cod.orderTime between ?2 and ?3")
    fun orderCount(acctNo: String, startTime: Timestamp, endTime: Timestamp): List<CustomerOrder>

    fun findByOrderNo(orderNo: String): CustomerOrder?

    @Query(value = "select count(cod.id),count(case when cod.type = 'pc' then cod.id end),count(case when cod.type = 'mobile' then cod.id end),count(case when cod.status = '违约' then cod.id end),count(case when cod.status = '进行中' then cod.id end) from CustomerOrder as cod where (to_char(cod.dptId) = ?1 or ?1 is null) and (to_char(cod.orgId) = ?2 or ?2 is null) and (to_char(cod.crmAcctId) = ?3 or ?3 is null) and to_char(cod.orderTime,'yyyy-MM-dd') between to_char(?4,'yyyy-MM-dd') and to_char(?5,'yyyy-MM-dd')")
    fun orderCounting(dptId: String?, orgId: String?, acctId: String?, startTime: Timestamp, endTime: Timestamp): List<Double>

}