package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.DeliveryDetailProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface DeliveryDetailRepository: CrudRepository<BehaviorRecord, Long> {

    //直发调货明细
    @Query(nativeQuery = true,
            countQuery = "select count(*) from v_fortc_zfmx where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null)" +
                    " and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null)  and (datas_customername like ?5 or ?5 is null) order by ?#{#pageable}",
            value = "select datas_customername,profit_date,dept_name,employee_name,zf_bweight,dh_bweight,tcje" +
                    " from v_fortc_zfmx where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) " +
                    " and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null)  order by ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode:String?,deptName:String?,customer:String?,pageable: Pageable): Page<DeliveryDetailProjection>

    //直发调货明细汇总
    @Query(nativeQuery = true,
            value = "select nvl(sum(zf_bweight),0) as zf_bweight_sum," +
                    "nvl(sum(dh_bweight),0) as dh_bweight_sum,nvl(sum(tcje),0) as tcje_sum" +
                    " from v_fortc_zfmx where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) " +
                    " and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null) ")
    fun allQuerySum(nyStart:String?,nyEnd:String?,empCode: String?,deptName:String?,customer:String?): Array<Array<Any>>

    //直发调货明细
    @Query(nativeQuery = true,
            value = "select datas_customername,to_char(profit_date,'yyyy-MM') as profit_date,dept_name,employee_name,zf_bweight,dh_bweight,tcje" +
                    " from v_fortc_zfmx where (to_char(profit_date,'yyyy-MM') >=?1 or ?1 is null) " +
                    " and (to_char(profit_date,'yyyy-MM') <=?2 or ?2 is null) and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null) ")
    fun allQueryExport(nyStart:String?,nyEnd:String?,empCode:String?,deptName:String?,customer:String?): List<Any>

}