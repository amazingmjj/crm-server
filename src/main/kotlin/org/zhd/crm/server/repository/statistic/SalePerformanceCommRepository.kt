package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.SalePerformanceCommProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface SalePerformanceCommRepository: CrudRepository<BehaviorRecord, Long> {

    @Query(nativeQuery = true,
            countQuery = "select count(*) from V_SALE_TCHZ" +
                    " where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) order by ?#{#pageable}",
            value = "select dept_name,employee_name,ny,data_bweight,new_count,new_price,new_weight,new_tc," +
                    "ZF_PRICE,ZF_BWEIGHT,zf_tc,DH_PRICE,DH_BWEIGHT,dh_tc,OLD_PRICE,old_weight,old_tc,tcje " +
                    " from V_SALE_TCHZ where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) " +
                    " order by ?#{#pageable}")
    fun queryAll(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,pageable: Pageable): Page<SalePerformanceCommProjection>

    @Query(nativeQuery = true,
            value = "select dept_name,employee_name,ny,data_bweight,new_count,new_price,new_weight,new_tc," +
                    "ZF_PRICE,ZF_BWEIGHT,zf_tc,DH_PRICE,DH_BWEIGHT,dh_tc,OLD_PRICE,old_weight,old_tc,tcje " +
                    " from V_SALE_TCHZ where (ny>=?1 or ?1 is null) and (ny<=?2 or ?2 is null) " +
                    " and (employee_code=?3 or ?3 is null) and (dept_name=?4 or ?4 is null) ")
    fun queryAllExport(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?): List<Any>
}