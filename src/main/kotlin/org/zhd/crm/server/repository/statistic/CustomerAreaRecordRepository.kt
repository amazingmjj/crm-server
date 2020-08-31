package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.CustomerAreaRecordProjection
import org.zhd.crm.server.model.statistic.CustomerAreaRecord

interface CustomerAreaRecordRepository : CrudRepository<CustomerAreaRecord, Long> {
    @Query(nativeQuery = true, value = "select * from v_crm_area_weight where 1=1 and (?1 is null or area_name like ?1) and (?2 is null or dpt_name like ?2) and (?3 is null or employee_name like ?3) and (?4 is null or dpt_id=?4) and (?5 is null or acct_id=?5) order by ?#{#pageable}", countQuery = "select count(1) from v_crm_area_weight where 1=1 and (?1 is null or area_name like ?1) and (?2 is null or dpt_name like ?2) and (?3 is null or employee_name like ?3) and (?4 is null or dpt_id=?4) and (?5 is null or acct_id=?5) order by ?#{#pageable}")
    fun findByPg(areaName: String?, dptName: String?, employeeName: String?, dptId: String?, acctId: String?, pageable: Pageable): Page<CustomerAreaRecordProjection>
}