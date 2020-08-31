package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.SaleCommSummaryProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface SaleCommSummaryRepository: CrudRepository<BehaviorRecord, Long> {

    //提成汇总
    @Query(nativeQuery = true,
            countQuery = "select count(*) from v_fortc_xszymxb where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null)  order by ?#{#pageable}",
            value = "select dept_name,employee_name,ny,upweight,rwweight,data_bweight,other_weight," +
                    "real_weight,zf_bweight,new_bweight,new_tc,zf_tc,xl_tc," +
                    "case when data_bweight >= rwweight then '是' " +
                    " else '否' end success," +
                    " case when data_bweight >= rwweight then gm_tc " +
                    " else 0 end gm_tc_real , " +
                    " case when data_bweight >= rwweight then 0 " +
                    " else gm_tc end gm_tc_reduce," +
                    " gm_tc,(new_tc+zf_tc+xl_tc+(case when data_bweight >= rwweight then gm_tc else 0 end)) as hj_tc" +
                    " from v_fortc_xszymxb where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " order by decode(dept_name ,'营销一部', 1,'营销二部', 2,'营销三部', 3,'营销四部', 4,'营销五部', 5,99) asc ," +
                    "employee_name asc, ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,pageable: Pageable): Page<SaleCommSummaryProjection>

    //提成汇总
    @Query(nativeQuery = true,
            value = "select dept_name,employee_name,ny,upweight,rwweight,data_bweight,other_weight," +
                    "real_weight,zf_bweight,new_bweight,new_tc,zf_tc,xl_tc," +
                    "case when data_bweight >= rwweight then '是' " +
                    " else '否' end success," +
                    " case when data_bweight >= rwweight then gm_tc " +
                    " else 0 end gm_tc_real , " +
                    " case when data_bweight >= rwweight then 0 " +
                    " else gm_tc end gm_tc_reduce," +
                    " gm_tc,(new_tc+zf_tc+xl_tc+(case when data_bweight >= rwweight then gm_tc else 0 end)) as hj_tc" +
                    " from v_fortc_xszymxb where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " order by decode(dept_name ,'营销一部', 1,'营销二部', 2,'营销三部', 3,'营销四部', 4,'营销五部', 5,99) asc ," +
                    "employee_name asc")
    fun allQueryExport(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?): List<Any>

}