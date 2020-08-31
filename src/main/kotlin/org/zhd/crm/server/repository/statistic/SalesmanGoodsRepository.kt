package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.SalesmanGoods
import java.sql.Timestamp

interface SalesmanGoodsRepository : CrudRepository<SalesmanGoods, Long> {
    @Query(value = "select COALESCE(sum(sg.weight),0.0),COALESCE(sum(case when sg.type = 1 then sg.weight end),0.0),COALESCE(sum(case when sg.type = 0 then sg.weight end),0.0),COALESCE(sum(case when sg.category = '板材' then sg.weight end),0.0) from crm_salesman_goods sg where (sg.employeeCode = ?1 or ?1 is null) and to_char(sg.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun goodsCount(employeeCode: String?, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(value = "select COALESCE(sum(sg.weight),0.0),COALESCE(sum(case when sg.type = 1 then sg.weight end),0.0),COALESCE(sum(case when sg.type = 0 then sg.weight end),0.0),COALESCE(sum(case when sg.category = '板材' then sg.weight end),0.0) from crm_salesman_goods sg where sg.employeeCode in(?1) and to_char(sg.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun goodsCount(empList: List<String>, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(value = "select COALESCE(sum(sg.weight),0.0),COALESCE(sum(case when sg.type = 1 then sg.weight end),0.0),COALESCE(sum(case when sg.type = 0 then sg.weight end),0.0),COALESCE(sum(sg.amount),0.0),COALESCE(sum(case when sg.type = 1 then sg.amount end),0.0),COALESCE(sum(case when sg.type = 0 then sg.amount end),0.0) from crm_salesman_goods sg where (sg.employeeCode = ?1 or ?1 is null) and to_char(sg.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun goodsCounting(employeeCode: String?, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(value = "select COALESCE(sum(sg.weight),0.0),COALESCE(sum(case when sg.type = 1 then sg.weight end),0.0),COALESCE(sum(case when sg.type = 0 then sg.weight end),0.0),COALESCE(sum(sg.amount),0.0),COALESCE(sum(case when sg.type = 1 then sg.amount end),0.0),COALESCE(sum(case when sg.type = 0 then sg.amount end),0.0) from crm_salesman_goods sg where sg.employeeCode in(?1) and to_char(sg.dealDate,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun goodsCounting(empCodeList: List<String>, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(value = "select COALESCE(sum(sg.weight),0.0),COALESCE(sum(sg.amount),0.0) from crm_salesman_goods sg where sg.employeeCode = ?1")
    fun goodsCounting(employeeCode: String): List<Double>

    @Query(value = "from crm_salesman_goods sg where to_char(sg.dealDate,'yyyy-MM-dd') between ?2 and ?3 and (sg.employeeCode = ?1 or ?1 is null)")
    fun goodsList(employeeCode: String?,startTime: String, endTime: String): ArrayList<SalesmanGoods>

    @Query(value = "from crm_salesman_goods sg where to_char(sg.dealDate,'yyyy-MM-dd') between ?2 and ?3 and sg.employeeCode in(?1)")
    fun goodsList(empCodeList: List<String>,startTime: String, endTime: String): ArrayList<SalesmanGoods>
}