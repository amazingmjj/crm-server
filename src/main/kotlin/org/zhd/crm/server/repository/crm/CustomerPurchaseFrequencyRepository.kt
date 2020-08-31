package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerPurchaseFrequency
import org.zhd.crm.server.model.projection.CustomerPurchaseFrequencyProjection

interface CustomerPurchaseFrequencyRepository : CrudRepository<CustomerPurchaseFrequency, Long> {
    @Query(nativeQuery = true,
            countQuery = "select count(1) from v_cstm_purchase_freq where 1=1 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5) and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7)",
            value = "select * from v_cstm_purchase_freq where 1=1 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5) and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7) order by ?#{#pageable}")
    fun findAllViewByPg(dptName: String?, employeeName: String?, compName: String?, dptId: String?, acctId: String?, startDate: String?, endDate: String?, pageable: Pageable): Page<CustomerPurchaseFrequencyProjection>

    @Query(nativeQuery = true,
            countQuery = "select count(*) from v_cstm_purchase_freq t where 1=1 and (t.dpt_name like %?1% or ?1 is null) order by ?#{#pageable}",
            value = "select t.id, t.dpt_name, t.comp_name, t.employee_name from v_cstm_purchase_freq t where 1=1 and (t.dpt_name like %?1% or ?1 is null) order by ?#{#pageable}")
    fun findAllViewByPg(dptName: String?, pageable: Pageable): Page<CustomerPurchaseFrequencyProjection>

    /**
     * 已跟踪列表
     */
    @Query(nativeQuery = true,
            countQuery = "select count(1) from v_cstm_purchase_freq where id > 0 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5)  and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7)",
            value = "select * from v_cstm_purchase_freq where id > 0 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5) and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7) order by ?#{#pageable}")
    fun findAllViewRecordByPg(dptName: String?, employeeName: String?, compName: String?, dptId: String?, acctId: String?, startDate: String?, endDate: String?, pageable: Pageable): Page<CustomerPurchaseFrequencyProjection>

    /**
     * 未跟踪列表
     */
    @Query(nativeQuery = true,
            countQuery = "select count(1) from v_cstm_purchase_freq where id = 0 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5) and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7)",
            value = "select * from v_cstm_purchase_freq where id = 0 and (dpt_name like %?1% or ?1 is null) and (employee_name like %?2% or ?2 is null) and (comp_name like %?3% or ?3 is null) and (?4 is null or to_char(dpt_id) = ?4) and (?5 is null or to_char(acct_id) = ?5) and (?6 is null or update_at >= ?6) and (?7 is null or update_at <= ?7) order by ?#{#pageable}")
    fun findAllViewUnRecordByPg(dptName: String?, employeeName: String?, compName: String?, dptId: String?, acctId: String?, startDate: String?, endDate: String?, pageable: Pageable): Page<CustomerPurchaseFrequencyProjection>

}