package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Customer
import org.zhd.crm.server.model.projection.*
import java.sql.Timestamp
import javax.transaction.Transactional

interface CustomerRepository : CrudRepository<Customer, Long> {
    //多对多hql另外一种写法 :select s from Student s,Course c where c.id in elements (s.courses) and
    //hql分页@Query(value = "select distinct cstm.id,cstm,link from Customer as cstm left join cstm.linkers link left join cstm.busiRelation a left join cstm.procurementGoods b left join cstm.procurementPurpose c left join cstm.hopeAddGoods d left join cstm.dealGoods e left join cstm.dealPurpose f left join cstm.processRequirement g where link.mainStatus = 1 and cstm.status != 2 and ...")
    //sql分页@Query(nativeQuery = true, value = "select * from (select custm.*, ROWNUM RN from (select cstm.id,cstm.comp_name,lk.name as linkName,lk.phone as linkPhone,cstm.create_at,cstm.bill_date,dpt.name as dptName,acct.name as acctName,(select at.name from t_account at where at.id = cstm.create_acct_id) as creatorName,cstm.mark as mark,org.id as orgId,dpt.id as dptId,acct.id as acctId,(select count(cc.id) from t_customer_call cc where cc.status in (2,3,4) and cc.customer_id = cstm.id) as visitCount from t_customer cstm,t_linker lk,ref_cstm_linker rsl,t_dpt dpt,t_organization org,t_account acct where rsl.customers_id = cstm.id and rsl.linkers_id = lk.id and lk.main_status = 1 and dpt.id = cstm.fk_dpt_id and dpt.fk_org_id = org.id and acct.id = cstm.fk_acct_id and cstm.status != 2 and (cstm.comp_name like %?1% or ?1 is null) and (lk.name like %?2% or ?2 is null) and (lk.phone like %?3% or ?3 is null) and (to_char(cstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(cstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (dpt.name like %?6% or ?6 is null) and (acct.name like %?7% or ?7 is null) and cstm.mark = ?8 and (to_char(acct.id) = ?9 or ?9 is null) and (to_char(dpt.id) = ?10 or ?10 is null) and (to_char(org.id) = ?11 or ?11 is null)) custm where ROWNUM <= ?13) where RN > ?12")
    @Query(nativeQuery = true, countQuery = "select count(*) from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) order by ?#{#pageable}", value = "select vcstm.* from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) order by ?#{#pageable}")
    fun findCstmAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, dptName: String?,
                    acctName: String?, mark: Int, acctId: String?, dptId: String?, orgId: String?, status: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) and vcstm.erp_code is not null and vcstm.xy_code is not null order by ?#{#pageable}", value = "select vcstm.* from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) and vcstm.erp_code is not null and vcstm.xy_code is not null order by ?#{#pageable}")
    fun findXyCstmAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, dptName: String?,
                      acctName: String?, mark: Int, acctId: String?, dptId: String?, orgId: String?, status: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) and vcstm.erp_code is not null and vcstm.xy_code is null order by ?#{#pageable}", value = "select vcstm.* from v_cstm_list vcstm where (vcstm.comp_name like %?1% or ?1 is null) and (vcstm.linkName like %?2% or ?2 is null) and (vcstm.linkPhone like %?3% or ?3 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(vcstm.create_at,'yyyy-MM-dd') <= ?5 or ?5 is null) and (vcstm.dptName like %?6% or ?6 is null) and (vcstm.acctName like %?7% or ?7 is null) and vcstm.mark = ?8 and (to_char(vcstm.acctId) = ?9 or ?9 is null) and (to_char(vcstm.dptId) = ?10 or ?10 is null) and (to_char(vcstm.orgId) = ?11 or ?11 is null) and (to_char(vcstm.status) = ?12 or ?12 is null) and vcstm.erp_code is not null and vcstm.xy_code is null order by ?#{#pageable}")
    fun findErpCstmAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, dptName: String?,
                       acctName: String?, mark: Int, acctId: String?, dptId: String?, orgId: String?, status: String?, pageable: Pageable): Page<Any>

    fun findByCompName(compName: String): Customer?

    @Query(nativeQuery = true, value = "SELECT count(t.id) FROM T_CUSTOMER t WHERE t.COMP_NAME IN ?1 AND t.SETTLE_DELAY = 1")
    fun settleCountByCompName(names: List<String>): Int

    fun findByEbusiAdminAcctNo(eAcctNo: String): Customer?

    fun findByErpCode(erpCode: String): Customer?

    fun findByEbusiMemberCode(eMemberCode: String): Customer?

    @Query(value="select t.id, t.compName as name, t.erpCode, t.ebusiMemberCode as xyCode, t.updateAt from Customer as t where (t.compName like %?1% or ?1 is null) and t.status=1")
    fun findCstmListInWxMini(name: String?, pageable: Pageable): Page<WxMiniCustomer>

    @Query(value="select t.id, t.compName as name, t.erpCode, t.ebusiMemberCode as xyCode, t.updateAt from Customer as t where (t.compName like %?1% or ?1 is null) and t.status=1 and t.ebusiMemberCode is not null")
    fun findXyCstmListInWxMini(name: String?, pageable: Pageable): Page<WxMiniCustomer>

    @Query(value = "from Customer as cstm where cstm.billDate is not null and to_char(cstm.billDate,'yyyy-MM-dd') < ?1 and cstm.status != 2 and cstm.mark = 2 and cstm.lockStatus = 0")
    fun findCstmList(day: String): List<Customer>

    @Query(value = "from Customer as cstm where cstm.status != 2 and (cstm.compName like %?1% or ?1 is null)")
    fun findCombo(compName: String?, pageable: Pageable): Page<Customer>

    @Query(value = "from Customer as cstm where cstm.status = 1 and cstm.mark in (2, 3) and cstm.erpCode is not null and (cstm.compName like %?1% or ?1 is null)")
    fun findAll(compName: String?, pageable: Pageable): Page<Customer>

    @Query(value = "from Customer as cstm where cstm.status != 2 and (cstm.compName like %?1% or ?1 is null) and (cstm.fkAcct.id = ?2 or ?2 is null)")
    fun findComboByAcctId(compName: String?, acctId: Long?, pageable: Pageable): Page<Customer>

    @Query(value = "from Customer as cstm where cstm.status != 2 and (cstm.compName like %?1% or ?1 is null) and (cstm.fkDpt.id = ?2 or ?2 is null)")
    fun findComboByDptId(compName: String?, dptId: Long?, pageable: Pageable): Page<Customer>

    @Query(value = "from Customer as cstm where cstm.status != 2 and (cstm.compName like %?1% or ?1 is null) and (cstm.fkDpt.fkOrg.id = ?2 or ?2 is null)")
    fun findComboyOrgId(compName: String?, orgId: Long?, pageable: Pageable): Page<Customer>

    @Query(value = "select DISTINCT cstm.region from Customer as cstm where cstm.status != 2 and cstm.mark = 2 and cstm.region is not null and (cstm.region like %?1% or ?1 is null) and cstm.region not in (?2)")
    fun findRegionList(region: String?, list: List<String>, pageable: Pageable): Page<String>

    @Query(value = "select new map(cstm.compName as compName, cstm.publicCompName as publicName, cstm.erpCode as erpCode, (to_char(cstm.billDate, 'YYYY-MM-dd')) as billDate, cstm.fkDpt.name as dptName, cstm.fkAcct.name as acctName, cstm.region as region, cstm.fkCustomProperty.name as cstmProperty, (to_char(cstm.mark)) as mark, (to_char(cstm.createAt, 'YYYY-MM-DD')) as createAt, cstm.ebusiAdminAcctNo as eUserId) from Customer as cstm where cstm.erpCode is not null and cstm.mark !=1 and cstm.status != 2")
    fun simpleNameList(): List<Map<String, String>>

    @Query(value = "select count(cstm.id) from Customer as cstm left join cstm.fkAcct as acct where cstm.mark = 2 and cstm.status != 2 and (acct.platformCode = ?1 or ?1 is null) and to_char(cstm.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun newCstmCount(platformCode: String?, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select count(cstm.id) from Customer as cstm left join cstm.fkAcct as acct where cstm.mark = 2 and cstm.status != 2 and acct.platformCode in(?1) and to_char(cstm.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun newCstmCount(empList: List<String>, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select cstm.ebusiAdminAcctNo from Customer as cstm where cstm.fkAcct.id = ?1 and cstm.ebusiAdminAcctNo is not null and cstm.status != 2")
    fun findCodeByAcct(acctId: Long?): List<String>

    @Query(value = "select cstm.ebusiAdminAcctNo from Customer as cstm where cstm.fkAcct.platformCode in(?1) and cstm.ebusiAdminAcctNo is not null and cstm.status != 2")
    fun findCodeByAcct(acctCode: List<String>): List<String>

    @Query(value = "select count(cstm.id),count(case when cstm.ebusiAdminAcctNo is null then cstm.id end),count(case when cstm.ebusiAdminAcctNo is not null then cstm.id end) from Customer as cstm where cstm.mark = 2 and cstm.status != 2 and (cstm.fkAcct.platformCode = ?1 or ?1 is null) and to_char(cstm.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun cstmCounting(platformCode: String?, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(value = "select count(cstm.id),count(case when cstm.ebusiAdminAcctNo is null then cstm.id end),count(case when cstm.ebusiAdminAcctNo is not null then cstm.id end) from Customer as cstm where cstm.mark = 2 and cstm.status != 2 and cstm.fkAcct.platformCode in(?1) and to_char(cstm.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun cstmCounting(empCodeList: List<String>, startTime: Timestamp, endTime: Timestamp): List<Double>

    @Query(nativeQuery = true, value = "select count(cstm.id) from v_cstm_list cstm where cstm.mark = 2 and cstm.empCode = ?1")
    fun cstmCounting(platformCode: String): Int

    @Query(nativeQuery = true, countQuery = "select count(*) from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) order by ?#{#pageable}", value = "select a.* from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) order by ?#{#pageable}")
    fun findCstmList(compName: String?, startTime: String?, endTime: String?, initial: String?, mark: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (((to_char(a.acct_id)= ?2 or ?2 is null) and (to_char(a.dpt_id)= ?3 or ?3 is null) and (to_char(a.org_id) = ?4 or ?4 is null) and a.mark != 3) or a.mark = 3) order by ?#{#pageable}", value = "select a.* from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (((to_char(a.acct_id)= ?2 or ?2 is null) and (to_char(a.dpt_id)= ?3 or ?3 is null) and (to_char(a.org_id) = ?4 or ?4 is null) and a.mark != 3) or a.mark = 3) order by ?#{#pageable}")
    fun findCstmListAll(compName: String?, acctId: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.acct_id = ?6 or ?6 is null)order by ?#{#pageable}", value = "select a.* from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.acct_id = ?6 or ?6 is null) order by ?#{#pageable}")
    fun findCstmByAcctId(compName: String?, startTime: String?, endTime: String?, initial: String?, mark: String?, acctId: Long?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.dpt_id = ?6 or ?6 is null) order by ?#{#pageable}", value = "select a.* from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.dpt_id = ?6 or ?6 is null) order by ?#{#pageable}")
    fun findCstmByDptId(compName: String?, startTime: String?, endTime: String?, initial: String?, mark: String?, dptId: Long?, pageable: Pageable): Page<Any>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.org_id = ?6 or ?6 is null) order by ?#{#pageable}", value = "select a.* from v_mobile_cstm_list a where (a.comp_name like %?1% or ?1 is null) and (to_char(a.bill_date,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(a.bill_date,'yyyy-MM-dd') <= ?3 or ?3 is null) and (a.comp_name_initial = ?4 or ?4 is null) and (to_char(a.mark) = ?5 or ?5 is null) and (a.org_id = ?6 or ?6 is null) order by ?#{#pageable}")
    fun findCstmByOrgId(compName: String?, startTime: String?, endTime: String?, initial: String?, mark: String?, orgId: Long?, pageable: Pageable): Page<Any>

    @Query(value = "from Customer as cm where cm.compNameInitial is null and cm.status != 2")
    fun findCstmInitial(): List<Customer>

    @Query(value = "select count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?2 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?3 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?4 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?5 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?6 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?7 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?8 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') between ?9 and ?10 then cstm.id end) from Customer as cstm left join cstm.fkAcct as acct where cstm.mark = 2 and cstm.status != 2 and (acct.platformCode = ?1 or ?1 is null)")
    fun newCstmCountList(platformCode: String?, firstTime: String, secondTime: String, thirdTime: String, fourthTime: String, fifthTime: String, sixthTime: String, seventhTime: String, startTime: String, endTime: String): List<Double>

    @Query(value = "select count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?2 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?3 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?4 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?5 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?6 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?7 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') = ?8 then cstm.id end),count(case when to_char(cstm.createAt,'yyyy-MM-dd') between ?9 and ?10 then cstm.id end) from Customer as cstm left join cstm.fkAcct as acct where cstm.mark = 2 and cstm.status != 2 and acct.platformCode in(?1)")
    fun newCstmCountList(empList: List<String>, firstTime: String, secondTime: String, thirdTime: String, fourthTime: String, fifthTime: String, sixthTime: String, seventhTime: String, startTime: String, endTime: String): List<Double>

    @Query(value = "select count(cm.id) from Customer as cm where cm.status != 2 and cm.compName = ?1")
    fun compNameCount(compName: String): Int

    @Query(value = "select count(cm.id) from Customer as cm where cm.status != 2 and cm.busiLicenseCode = ?1")
    fun busiLicenseCodeCount(busiLicenseCode: String): Int

    @Query(value = "select count(cm.id) from Customer as cm where cm.status != 2 and cm.tfn = ?1")
    fun tfnCount(tfn: String): Int

    @Query(value = "select count(cm.id) from Customer as cm where cm.status != 2 and cm.openAcctName = ?1")
    fun openAcctNameCount(openAcctName: String): Int

    @Query(value = "from Customer as cm where cm.status != 2 and cm.fkAcct.id = ?1")
    fun findListByAcctId(acctId: Long): List<Customer>

    @Query(value = "from Customer as cm where cm.status != 2 and cm.fkDpt.id = ?1")
    fun findListByDptId(dptId: Long): List<Customer>

    @Query(nativeQuery = true, value="select t.comp_name, t.ebusi_member_code, t.unit_property, t.deposit_rate from t_customer t where t.status = 1 and t.ebusi_member_code in ?1")
    fun findUnitPropertyInEbusiMemberCode(esCodes: List<String>):List<EsCustomerUnitProperty>

    @Query(nativeQuery = true, value="select t.comp_name, t.ebusi_member_code, t.unit_property, t.deposit_rate from t_customer t where t.status = 1 and t.ebusi_member_code is not null and t.unit_property like '%,3'")
    fun findEsIousCstms(): List<EsCustomerUnitProperty>

    @Query(value = "from Customer t where t.cstmType = 0 and t.status != 2 and t.erpCode is not null")
    fun findNewCstList(): List<Customer>

    @Modifying
    @Transactional
    @Query(value = "update Customer t set t.cstmType=?2,t.startTime=?3,t.billDate=?4,t.mark=?5 " +
            "where t.id = ?1 or t.mainCstm.id=?1")
    fun updateFollowCst(mainCstId:Long,cstType:Int,startTime: Timestamp,billDate:Timestamp,mark: Int)

    @Query(value = "from Customer t where (t.id = ?1 or t.mainCstm.id=?1) and (t.id <> ?2 or ?2 is null)")
    fun findFollowCst(mainCstId:Long,exceptId:Long?):List<Customer>

    @Query(value = "from Customer t where t.compArea like '%市'")
    fun findCstCityArea(pageable: Pageable):Page<Customer>

    @Query(value = "from Customer t where t.compName = ?1 and t.id<>?2")
    fun findOtherSameName(name: String,thisId:Long) : List<Customer>

//    @Query(nativeQuery = true,value = "select company_code,to_char(min_ddate,'yyyy-MM-dd') as minDeliveryDate," +
//            "to_char(min_sdate,'yyyy-MM-dd') as minSaleDate FROM v_cst_min_sbilldate where 1=1 " +
//            "and (company_code = ?1 or ?1 is null)")
//    fun findCstFirstBillDate(erpCode: String): List<FirstBillProjection>

    // 对于聚合函数 #pageable不能识别正确的别名，比如sum(XX) as AA想要的是order by AA,结果是order by a.AA,a.AA不存在所以报错
//    @Query(nativeQuery = true, countQuery = "select count(*) from t_customer a left join (select b.member_code as code1, sum(b.weight) as sum1 from crm_goods_sales@crmstatdev b where to_char(b.deal_date, 'yyyy-MM-dd') >= ?1 and to_char(b.deal_date, 'yyyy-MM-dd') <= ?2 group by b.member_code) bb on bb.code1 = a.erp_code left join (select c.member_code as code2, sum(c.weight) as sum2 from crm_goods_sales@crmstatdev c where to_char(c.deal_date, 'yyyy-MM-dd') >= ?3 and to_char(c.deal_date, 'yyyy-MM-dd') <= ?4 group by c.member_code) cc on cc.code2 = a.erp_code left join t_account d on a.fk_acct_id = d.id left join t_dpt e on a.fk_dpt_id = e.id left join t_organization f on e.fk_org_id = f.id where a.mark = 2 and a.status = 1 and a.erp_code is not null and (a.comp_name like %?5% or ?5 is null) and (d.name like %?6% or ?6 is null) and (e.name like %?7% or ?7 is null) and (to_char(d.id) = ?8 or ?8 is null) and (to_char(e.id) = ?9 or ?9 is null) and (to_char(f.id) = ?10 or ?10 is null) order by ?#{#pageable}", value = "select a.comp_name,d.name as acct_name,e.name as dpt_name,a.erp_code,COALESCE(bb.sum1, 0.0) as this_month_weight,COALESCE(cc.sum2, 0.0) as last_month_weight from t_customer a left join (select b.member_code as code1, sum(b.weight) as sum1 from crm_goods_sales@crmstatdev b where to_char(b.deal_date, 'yyyy-MM-dd') >= ?1 and to_char(b.deal_date, 'yyyy-MM-dd') <= ?2 group by b.member_code) bb on bb.code1 = a.erp_code left join (select c.member_code as code2, sum(c.weight) as sum2 from crm_goods_sales@crmstatdev c where to_char(c.deal_date, 'yyyy-MM-dd') >= ?3 and to_char(c.deal_date, 'yyyy-MM-dd') <= ?4 group by c.member_code) cc on cc.code2 = a.erp_code left join t_account d on a.fk_acct_id = d.id left join t_dpt e on a.fk_dpt_id = e.id left join t_organization f on e.fk_org_id = f.id where a.mark = 2 and a.status = 1 and a.erp_code is not null and (a.comp_name like %?5% or ?5 is null) and (d.name like %?6% or ?6 is null) and (e.name like %?7% or ?7 is null) and (to_char(d.id) = ?8 or ?8 is null) and (to_char(e.id) = ?9 or ?9 is null) and (to_char(f.id) = ?10 or ?10 is null) order by ?#{#pageable},last_month_weight desc")
//    fun findFirstSortSales(firstTime: String, secondTime: String, thirdTime: String, fourthTime: String, compName: String?, acctName: String?, dptName: String?, id: String?, dptId: String?, orgId: String?, percent: String?, warningType: String?, pageable: Pageable): Page<Any>
}
