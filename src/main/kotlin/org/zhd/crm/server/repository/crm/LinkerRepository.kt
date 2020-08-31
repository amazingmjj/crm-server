package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Linker
import org.zhd.crm.server.model.projection.LinkProjection
import org.zhd.crm.server.model.projection.SmsLinkProjection
import javax.transaction.Transactional

interface LinkerRepository : CrudRepository<Linker, Long> {
    @Query(nativeQuery = true, countQuery = "select count(*) from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) order by ?#{#pageable}", value = "select * from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) order by ?#{#pageable}")
    fun findAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?,
                dptName: String?, acctName: String?, uid: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) and a.erp_code is not null and a.xy_code is not null order by ?#{#pageable}", value = "select * from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) and a.erp_code is not null and a.xy_code is not null order by ?#{#pageable}")
    fun findXyAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?,
                  dptName: String?, acctName: String?, uid: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) and a.erp_code is not null and a.xy_code is null order by ?#{#pageable}", value = "select * from v_linker_cstm_list a where (to_char(a.acct_id) = ?11 or ?11 is null) and (to_char(a.dpt_id) = ?12 or ?12 is null) and (to_char(a.org_id) = ?13 or ?13 is null) and (a.comp_name like %?1% or ?1 is null) and (a.name like %?2% or ?2 is null) and (a.phone like %?3% or ?3 is null) and (to_char(a.create_at, 'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(a.create_at, 'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(a.main_status) = ?6 or ?6 is null) and (to_char(a.sex) = ?7 or ?7 is null) and (a.position like %?8% or ?8 is null) and (a.dpt_name like %?9% or ?9 is null) and (a.acct_name like %?10% or ?10 is null) and a.erp_code is not null and a.xy_code is null order by ?#{#pageable}")
    fun findErpAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?,
                   dptName: String?, acctName: String?, uid: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<Any>

//    @Query(nativeQuery=true, value = "select link from Linker as link join link.customers as cstm where cstm.id = ?1")
    @Query(nativeQuery=true, value = "select lk.*, CASE WHEN (select rclk.status from REF_CSTM_COMM_LINK rclk where lk.id = rclk.linker_id and rclk.cstm_id = ?1) IS NULL THEN 0 ELSE (select rclk.status from REF_CSTM_COMM_LINK rclk where lk.id = rclk.linker_id and rclk.cstm_id = ?1) END AS comm_mark from T_LINKER lk, REF_CSTM_LINKER rlk where rlk.LINKERS_ID = lk.id and rlk.CUSTOMERS_ID = ?1")
    fun findAll(id: Long): List<Linker>

    @Query(nativeQuery = true, value = "select count(*) from v_linker_cstm_list a where a.cstm_id = ?1 and a.main_status = 1")
    fun linkCountByCstmId(id: Long): Int

    @Query(nativeQuery = true, value = "select count(*) from v_linker_cstm_list a where a.link_id = ?1 and a.main_status = 1")
    fun cstmCountByLinkId(id: Long): Int

    @Query(value = "select link from Linker as link join link.customers as cstm where cstm.id = ?1 and link.mainStatus = 1")
    fun findMainLink(id: Long?): Linker

    fun findByPhone(phone: String): Linker? //fileController会用到，延后整改

    @Query(value = "from Linker as link where link.phone = ?1")
    fun findListByPhone(phone: String): List<Linker>

    // 同一个客户下，手机号不能重复
    @Query(nativeQuery = true, value = "select t.* from v_cstm_link t where t.link_phone=?1 and t.cstm_id = ?2")
    fun findCstmLinkRepeat(phone: String, cstmId: Long): List<LinkProjection>

    @Query(nativeQuery = true, value = "select t.* from v_cstm_link t where t.ebusi_member_code in ?1 order by t.comm_status desc, t.main_status desc")
    fun findCstmLinkInBusiCode(codes: List<String>): List<LinkProjection>

    @Query(nativeQuery = true, value = "select t.* from v_cstm_link t where t.erp_code in ?1 order by t.comm_status desc, t.main_status desc")
    fun findCstmLinkInErpCode(codes: List<String>): List<LinkProjection>

//    @Query(nativeQuery = true, countQuery = "select count(*) from (select a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.main_status from v_cstm_linker_list a where (a.link_name like %?1% or ?1 is null) and (a.link_phone like %?2% or ?2 is null) and (to_char(a.main_status) = ?3 or ?3 is null) and (a.comp_name like %?4% or ?4 is null) and (a.region like %?5% or ?5 is null) and (a.dpt_name like %?6% or ?6 is null) and (a.acct_name like %?7% or ?7 is null) and (a.busi_name = ?8 or ?8 is null) and (a.pro_name = ?9 or ?9 is null) and (a.summary_level = ?10 or ?10 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?11 or ?11 is null) and (to_char(a.bill_date,'yyyy-MM-dd') < ?12 or ?12 is null) and (to_char(a.mark) = ?13 or ?13 is null) and (to_char(a.acct_id) = ?14 or ?14 is null) and (to_char(a.dpt_id) = ?15 or ?15 is null) and (to_char(a.org_id) = ?16 or ?16 is null) group by a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.create_at,a.main_status order by ?#{#pageable})", value = "select a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.main_status from v_cstm_linker_list a where (a.link_name like %?1% or ?1 is null) and (a.link_phone like %?2% or ?2 is null) and (to_char(a.main_status) = ?3 or ?3 is null) and (a.comp_name like %?4% or ?4 is null) and (a.region like %?5% or ?5 is null) and (a.dpt_name like %?6% or ?6 is null) and (a.acct_name like %?7% or ?7 is null) and (a.busi_name = ?8 or ?8 is null) and (a.pro_name = ?9 or ?9 is null) and (a.summary_level = ?10 or ?10 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?11 or ?11 is null) and (to_char(a.bill_date,'yyyy-MM-dd') < ?12 or ?12 is null) and (to_char(a.mark) = ?13 or ?13 is null) and (to_char(a.acct_id) = ?14 or ?14 is null) and (to_char(a.dpt_id) = ?15 or ?15 is null) and (to_char(a.org_id) = ?16 or ?16 is null) group by a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.create_at,a.main_status order by ?#{#pageable}")
//    fun findCstmLinker(linkName: String?, linkPhone: String?, mainStatus: String?, compName: String?, region: String?, dptName: String?, acctName: String?, busiName: String?, proName: String?, summaryLevel: String?, startTime: String?, endTime: String?, mark: String?, acctId: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<Any>
//
//    @Query(nativeQuery = true, value = "select a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.main_status from v_cstm_linker_list a where (a.link_name like %?1% or ?1 is null) and (a.link_phone like %?2% or ?2 is null) and (to_char(a.main_status) = ?3 or ?3 is null) and (a.comp_name like %?4% or ?4 is null) and (a.region like %?5% or ?5 is null) and (a.dpt_name like %?6% or ?6 is null) and (a.acct_name like %?7% or ?7 is null) and (a.busi_name = ?8 or ?8 is null) and (a.pro_name = ?9 or ?9 is null) and (a.summary_level = ?10 or ?10 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?11 or ?11 is null) and (to_char(a.bill_date,'yyyy-MM-dd') < ?12 or ?12 is null) and (to_char(a.mark) = ?13 or ?13 is null) and (to_char(a.acct_id) = ?14 or ?14 is null) and (to_char(a.dpt_id) = ?15 or ?15 is null) and (to_char(a.org_id) = ?16 or ?16 is null) group by a.link_name,a.link_phone,a.acct_name,a.dpt_name,a.region,a.comp_name,a.create_at,a.main_status")
//    fun findLinkerAll(linkName: String?, linkPhone: String?, mainStatus: String?, compName: String?, region: String?, dptName: String?, acctName: String?, busiName: String?, proName: String?, summaryLevel: String?, startTime: String?, endTime: String?, mark: String?, acctId: String?, dptId: String?, orgId: String?): List<Any>

    @Query(nativeQuery = true, value = "select a.link_name,a.link_phone,a.comp_name,a.acct_name,a.dpt_name from v_cstm_linker_list a where a.link_phone = ?1 group by a.link_name,a.link_phone,a.comp_name,a.acct_name,a.dpt_name")
    fun findViewByPhone(linkPhone: String): List<SmsLinkProjection>

    @Query(nativeQuery = true, value = "select a.name as linkName,a.main_status as mainStatus,a.phone as linkPhone,b.linkers_id as linkId,b.customers_id as cstmId,c.comp_name as compName from t_linker a, ref_cstm_linker b, t_customer c where a.id = b.linkers_id and b.customers_id = c.id and b.linkers_id in (select re.linkers_id from ref_cstm_linker re group by re.linkers_id having count(re.customers_id) > 1) order by a.id asc")
    fun findLinkAndCstmRelation(): List<LinkProjection>//jpa Projection

    @Query(nativeQuery = true, value = "select distinct a.erp_code from v_main_link a")
    fun findLinkerPhone(): List<String>

    @Query(nativeQuery = true, value = "select a.id from v_main_link a where a.erp_code = ?1")
    fun findLinkerPhone(erpCode: String): List<Long>

    @Modifying
    @Query(nativeQuery = true, value = "update t_linker set main_status =?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(nativeQuery=true, value="SELECT DISTINCT (t.name) FROM (SELECT tc.COMP_NAME, tc.STATUS, td.name FROM REF_CSTM_LINKER a, T_CUSTOMER tc, T_DPT td WHERE tc.id = a.CUSTOMERS_ID AND td.id = tc.FK_DPT_ID AND LINKERS_ID IN (SELECT ID FROM T_LINKER WHERE phone = ?1) AND tc.STATUS =1) t")
    fun getLinkerDptNameByPhone(phone: String): List<String>
}