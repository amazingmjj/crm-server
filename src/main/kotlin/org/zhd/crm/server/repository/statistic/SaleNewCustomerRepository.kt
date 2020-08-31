package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.SaleNewCustomerProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface SaleNewCustomerRepository: CrudRepository<BehaviorRecord, Long> {

    //销售专员新增客户明细
    @Query(nativeQuery = true,
            countQuery = "select count(*) from (select count(*) from v_fortc_comany_zy " +
                    "where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) " +
                    "and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null) " +
                    "group by datas_customername, startdate, dept_name, employee_name order by ?#{#pageable})",
            value = "select datas_customername, startdate, dept_name, employee_name,nvl(sum(weight_gm),0) weight_gm," +
                    "nvl(sum(money_gm),0) money_gm,nvl(sum(weight_gm)*6,0) tc_gm,nvl(sum(weight_dm),0) weight_dm," +
                    "nvl(sum(money_dm),0) money_dm,nvl(sum(weight_dm)*3,0) tc_dm,nvl(sum(zf_weight),0) as zf_bweight," +
                    "(nvl(sum(weight_gm), 0) + nvl(sum(weight_dm), 0)) as weight_sum," +
                    "(nvl(sum(weight_gm) * 6, 0) + nvl(sum(weight_dm) * 3, 0)) as tc_sum from v_fortc_comany_zy " +
                    "where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) " +
                    "and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null) " +
                    "group by datas_customername, startdate, dept_name, employee_name order by ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?,pageable: Pageable): Page<SaleNewCustomerProjection>

    //销售专员新增客户明细汇总
    @Query(nativeQuery = true,
            value = "select nvl(sum(weight_gm),0),nvl(sum(money_gm),0),nvl(sum(tc_gm),0),nvl(sum(weight_dm),0),nvl(sum(money_dm),0)," +
                    "nvl(sum(tc_dm),0),nvl(sum(zf_bweight),0),nvl(sum(weight_sum),0),nvl(sum(tc_sum),0) "+
                    "from (select datas_customername, startdate, dept_name, employee_name,nvl(sum(weight_gm),0) weight_gm," +
                    "nvl(sum(money_gm),0) money_gm,nvl(sum(weight_gm)*6,0) tc_gm,nvl(sum(weight_dm),0) weight_dm," +
                    "nvl(sum(money_dm),0) money_dm,nvl(sum(weight_dm)*3,0) tc_dm,nvl(sum(zf_weight),0) as zf_bweight," +
                    "(nvl(sum(weight_gm), 0) + nvl(sum(weight_dm), 0)) as weight_sum," +
                    "(nvl(sum(weight_gm) * 6, 0) + nvl(sum(weight_dm) * 3, 0)) as tc_sum from v_fortc_comany_zy " +
                    "where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) " +
                    "and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null) " +
                    "group by datas_customername, startdate, dept_name, employee_name) ")
    fun allQuerySum(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?): Array<Array<Any>>

    //销售专员新增客户明细
    @Query(nativeQuery = true,
            value = "select datas_customername, startdate, dept_name, employee_name," +
                    "nvl(sum(weight_gm),0) weight_gm,nvl(sum(weight_gm)*6,0) tc_gm," +
                    "nvl(sum(weight_dm),0) weight_dm,nvl(sum(weight_dm)*3,0) tc_dm," +
                    "nvl(sum(zf_weight),0) as zf_bweight,(nvl(sum(weight_gm), 0) + nvl(sum(weight_dm), 0)) as weight_sum," +
                    "(nvl(sum(weight_gm) * 6, 0) + nvl(sum(weight_dm) * 3, 0)) as tc_sum from v_fortc_comany_zy " +
                    "where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) " +
                    "and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null) " +
                    "group by datas_customername, startdate, dept_name, employee_name")
    fun allQueryExport(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?): List<Any>

}