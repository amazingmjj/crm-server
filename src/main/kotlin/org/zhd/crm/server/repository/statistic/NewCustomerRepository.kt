package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.NewCustomerProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface NewCustomerRepository: CrudRepository<BehaviorRecord, Long> {
    //新增客户明细
    @Query(nativeQuery = true,
            countQuery = "select count(*) from v_fortc_newcomany where (to_char(startdate,'yyyy-MM') >=?1 or ?1 is null)" +
                    " and (to_char(startdate,'yyyy-MM') <=?2 or ?2 is null) and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) and (company_name like ?5 or ?5 is null) order by ?#{#pageable}",
            value = "select company_name,startdate,dept_name,employee_name " +
                    " from v_fortc_newcomany where (to_char(startdate,'yyyy-MM') >=?1 or ?1 is null) " +
                    " and (to_char(startdate,'yyyy-MM') <=?2 or ?2 is null)  and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) and (company_name like ?5 or ?5 is null) order by ?#{#pageable}")
    fun allQuery(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?,pageable: Pageable): Page<NewCustomerProjection>

    //新增客户明细
    @Query(nativeQuery = true,
            value = "select company_name,startdate,dept_name,employee_name " +
                    " from v_fortc_newcomany where (to_char(startdate,'yyyy-MM') >=?1 or ?1 is null) " +
                    " and (to_char(startdate,'yyyy-MM') <=?2 or ?2 is null)  and (employee_code=?3 or ?3 is null) " +
                    " and (dept_name=?4 or ?4 is null) and (company_name like ?5 or ?5 is null)")
    fun allQueryExport(nyStart:String?,nyEnd:String?,empCode: String?,deptName: String?,customer:String?): List<Any>

}