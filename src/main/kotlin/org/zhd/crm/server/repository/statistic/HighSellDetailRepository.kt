package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.HighSellDetailProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface HighSellDetailRepository: CrudRepository<BehaviorRecord, Long> {

    //高卖明细
    @Query(nativeQuery = true,
            countQuery = "select count(*) from v_fortc_mxgm where (ny >=?1 or ?1 is null) and (ny <=?2 or ?2 is null)" +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null) order by ?#{#pageable}",
            value = "select datas_customername,ny,dept_name,employee_name,gm" +
                    " from v_fortc_mxgm where (ny >=?1 or ?1 is null) and (ny <=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null) order by ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode: String?,deptName:String?,customer:String?,pageable: Pageable): Page<HighSellDetailProjection>

    //高卖明细汇总
    @Query(nativeQuery = true,
            value = "select nvl(sum(gm),0) as gm_sum from v_fortc_mxgm where (ny >=?1 or ?1 is null) and (ny <=?2 or ?2 is null)" +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null) ")
    fun allQuerySum(nyStart:String?,nyEnd:String?,empCode: String?,deptName:String?,customer:String?): Any

    //高卖明细
    @Query(nativeQuery = true,
            value = "select datas_customername,ny,dept_name,employee_name,gm" +
                    " from v_fortc_mxgm where (ny >=?1 or ?1 is null) and (ny <=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " and (datas_customername like ?5 or ?5 is null)")
    fun allQueryExport(nyStart:String?,nyEnd:String?,empCode: String?,deptName:String?,customer:String?): List<Any>

}