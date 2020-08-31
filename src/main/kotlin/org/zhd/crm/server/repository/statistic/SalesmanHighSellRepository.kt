package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.SalesmanHighSell
import java.sql.Timestamp

interface SalesmanHighSellRepository : CrudRepository<SalesmanHighSell, Long> {
    @Query(nativeQuery = true, value = "select COALESCE(sum(shl.high_amount),0.0) from CRM_SALESMAN_HIGH_SELL shl where shl.erp_code = ?1")
    fun sumAmount(erpCode: String): Double

    @Query(value = "select COALESCE(sum(sh.highAmount),0.0) from crm_salesman_high_sell sh where (sh.employeeCode = ?1 or ?1 is null) and to_char(sh.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun highSellCount(employeeCode: String?, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select COALESCE(sum(sh.highAmount),0.0) from crm_salesman_high_sell sh where sh.employeeCode in(?1) and to_char(sh.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun highSellCount(empList: List<String>, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "from crm_salesman_high_sell sh where to_char(sh.dealDate,'yyyy-MM-dd') between ?2 and ?3 and (sh.employeeCode = ?1 or ?1 is null)")
    fun highSellList(employeeCode: String?,startTime: String, endTime: String): ArrayList<SalesmanHighSell>

    @Query(value = "from crm_salesman_high_sell sh where to_char(sh.dealDate,'yyyy-MM-dd') between ?2 and ?3 and sh.employeeCode in(?1)")
    fun highSellList(empList: List<String>,startTime: String, endTime: String): ArrayList<SalesmanHighSell>
}