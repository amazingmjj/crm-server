package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Role
import javax.transaction.Transactional

interface RoleRepository : CrudRepository<Role, Long> {
    // 非超级管理权限列表
    @Query(value = "from Role as r where r.id <> 1 and (r.name like %?1% or ?1 is null) and (to_char(r.id) like %?2% or ?2 is null)")
    fun findCommAll(name: String?, id: String?, pg: Pageable): Page<Role>

    @Query(value="from Role as r where r.id <> 1 and r.status = 1 order by r.updateAt desc")
    fun findAllExSuper(): List<Role>

    @Modifying
    @Query(nativeQuery = true, value = "update t_role set status=?1 where id in ?2")
    @Transactional
    fun batchUpdateStatus(status: Int, ids: List<Long>)
}