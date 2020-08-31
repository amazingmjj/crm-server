package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Account
import javax.transaction.Transactional

interface AccountRepository : CrudRepository<Account, Long> {
    fun findByLoginAcct(acct: String): Account?

    fun findByName(acct: String): Account?

    fun findByPlatformCode(acct: String): Account?

    //不包含停用账号，platformCode为空的账号，离职账号
    @Query(value = "from Account as at where at.status = 1 and at.platformCode is not null and at.demission = 0 and (at.name like %?1% or ?1 is null)")
    fun findAllExSuper(name: String?, pageable: Pageable): Page<Account>

    //根据权限查询所有账号
//    @Query(value = "select t.id,t.addr,t.avatar,t.birthday,t.data_level,t.demission," +
//            "t.edu,t.email,t.in_time,t.job_title,t.login_acct,t.marital_status," +
//            "case when (instr(t.name,'x')>0 and t.remark is not null) then t.remark else t.name end name," +
//            "t.national,t.native_place,t.phone,t.platform_code,t.political_landscape,t.position,t.professional," +
//            "t.pwd,t.remark,t.sex,t.status,t.telephone,t.work_group,t.fk_dpt_id,t.fk_role_id,t.pwd_level," +
//            "t.id_card_no from t_account t where t.status = 1 and t.platform_code is not null and t.demission = 0 " +
//            " and (t.name like %?1% or t.remark like %?1% or ?1 is null) and (t.id = ?2 or ?2 is null)" +
//            " and (t.fk_dpt_id = ?3 or ?3 is null) order by ?#{#pageable}",nativeQuery = true)
    @Query(value = "from Account t where t.status = 1 and t.platformCode is not null and t.demission = 0 " +
            " and (t.name like %?1% or t.remark like %?1% or ?1 is null) and (t.id = ?2 or ?2 is null)" +
            " and (t.fkDpt.name = ?3 or ?3 is null) order by ?#{#pageable}")
    fun findAllAccount(name: String?, id: Long?, dptName: String?, pageable: Pageable): Page<Account>

    @Query(value = "from Account a where a.fkDpt.id = 1")
    fun findAcctForXy(): List<Account>

    @Query(value = "from Account as at where at.id <>1 and (at.name like %?1% or ?1 is null) and (at.loginAcct like %?2% or ?2 is null) and (at.fkDpt.fkOrg.name like %?3% or ?3 is null) and (at.fkDpt.name like %?4% or ?4 is null) and (at.position like %?5% or ?5 is null) and (at.phone like %?6% or ?6 is null) and (at.fkRole.name like %?7% or ?7 is null) and (at.dataLevel like %?8% or ?8 is null)")
    fun findCommAll(name: String?, loginAcct: String?, orgName: String?, dptName: String?, position: String?, phone: String?, roleName: String?, dataLevel: String?, pg: Pageable): Page<Account>

    @Modifying
    @Query(nativeQuery = true, value = "update t_account set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)

    @Query(value = "select count (at.id) from Account as at where (at.phone like %?1% or ?1 is null) and (at.email like %?2% or ?2 is null)")
    fun acctCount(phone: String?, email: String?): Int

    @Query(value = "select at.platformCode from Account at where at.fkDpt.id = ?1 and at.platformCode is not null")
    fun findAcctByDpt(dptId: Long): List<String>

    @Query(value = "select at.platformCode from Account at where at.fkDpt.fkOrg.id = ?1 and at.platformCode is not null")
    fun findAcctByOrg(orgId: Long): List<String>

    @Query(value = "select count(at.id) from Account at where at.loginAcct = ?1")
    fun loginAcctCount(loginAcct: String): Int

    @Query(value = "from Account at where at.fkDpt.id = ?1")
    fun findListByDptId(dptId: Long): List<Account>

    fun findByIdCardNo(idcard: String): Account?

    @Query(value = "from Account t where t.fkRole.id = ?1")
    fun findByRole(RoleId:Long): List<Account>
}