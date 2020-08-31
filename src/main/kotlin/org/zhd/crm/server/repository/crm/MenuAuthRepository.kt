package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Menu
import org.zhd.crm.server.model.crm.MenuAuth
import javax.transaction.Transactional

interface MenuAuthRepository :CrudRepository<MenuAuth,Long> {

    @Modifying
    @Query(value = "update MenuAuth t set t.authName = ?2,t.menu.id = ?3 where t.id = ?1")
    @Transactional
    fun updateById(id: Long, authName: String)

    @Query(value = "from MenuAuth t where (t.menu.id = ?1 or ?1 is null) and (t.authName like %?2% or ?2 is null)")
    fun findByPage(menuId:Long?,authName:String?,pageable: Pageable): Page<MenuAuth>

    @Query(value = "from MenuAuth t where t.menu.id = ?1 and t.authName= ?2")
    fun findUniqueMenuAuth(menuId: Long,authName: String):MenuAuth

    @Query(value = "from MenuAuth t where t.menu.id = ?1")
    fun findByMenuId(menuId: Long):List<MenuAuth>

    @Query(nativeQuery = true, value = "SELECT distinct ma.* FROM  t_menu_auth ma " +
            "left join t_auth a  on ma.menu_id=a.fk_menu_id " +
            "left join ref_role_auth rr on a.id=rr.auths_id " +
            "left join t_role r on r.id=rr.role_id " +
            "where r.id is not null " +
            " and (r.name like %?1% or ?1 is null) and (to_char(r.id) = ?2 or ?2 is null)")
    fun findByRole(name: String?, id: String?):List<MenuAuth>

    @Query(nativeQuery = true, value = "SELECT distinct ma.* FROM  t_menu_auth ma " +
            "left join t_auth a  on ma.menu_id=a.fk_menu_id left join ref_acct_auth rr on a.id=rr.auths_id " +
            "left join t_account at on at.id=rr.account_id left join t_dpt d on d.id=at.fk_dpt_id " +
            "left join t_organization o on o.id=d.fk_org_id left join t_role r on r.id=at.fk_role_id "+
            "where at.id <>1 and (at.name like %?1% or ?1 is null) " +
            "and (at.login_acct like %?2% or ?2 is null) " +
            "and (o.name like %?3% or ?3 is null) and (d.name like %?4% or ?4 is null) " +
            "and (at.position like %?5% or ?5 is null) and (at.phone like %?6% or ?6 is null) " +
            "and (r.name like %?7% or ?7 is null) and (at.data_level like %?8% or ?8 is null)")
    fun findByAcct(name: String?, loginAcct: String?, orgName: String?, dptName: String?, position: String?, phone: String?, roleName: String?, dataLevel: String?):List<MenuAuth>

}