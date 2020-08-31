package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.EmpNewCustomerProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface EmpNewCustomerRepository: CrudRepository<BehaviorRecord, Long> {

    //业务员新增客户明细
    @Query(nativeQuery = true,
            countQuery = "select count(*) from (select count(*) from  v_fortc_comany_ywy " +
                    " where (substr(profit_date,0,7) >=?1 or ?1 is null) and (substr(profit_date,0,7) <=?2 or ?2 is null)" +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null)" +
                    " group by datas_customername,startdate,dept_name,employee_name order by ?#{#pageable}) ",
            value = "select datas_customername,startdate,dept_name,employee_name," +
                    "nvl(sum(data_bweight),0) as data_bweight,nvl(sum(gm),0) as gm,nvl(sum(gm_tc),0) as gm_tc" +
                    " from v_fortc_comany_ywy where (substr(profit_date,0,7) >=?1 or ?1 is null) and (substr(profit_date,0,7) <=?2 or ?2 is null)" +
                    " and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null)" +
                    " group by datas_customername,startdate,dept_name,employee_name order by ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?,pageable: Pageable): Page<EmpNewCustomerProjection>

    //业务员新增客户明细汇总
    @Query(nativeQuery = true,
            value = "select nvl(sum(data_bweight),0),nvl(sum(gm),0),nvl(sum(gm_tc),0) from " +
                    "(select nvl(sum(data_bweight),0) as data_bweight,nvl(sum(gm),0) as gm,nvl(sum(gm_tc),0) as gm_tc" +
                    " from v_fortc_comany_ywy where (substr(profit_date,0,7) >=?1 or ?1 is null) and (substr(profit_date,0,7) <=?2 or ?2 is null)" +
                    " and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) and (datas_customername like ?5 or ?5 is null)" +
                    " group by datas_customername,startdate,dept_name,employee_name) ")
    fun allQuerySum(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?): Array<Array<Any>>

}