package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Menu

interface MenuRepository : CrudRepository<Menu, Long> {
    @Query(nativeQuery = true, value = "select * from T_MENU where PARENT_ID is not null order by PARENT_ID")
    fun subMenuList(): List<Menu>

    @Query(value = "from Menu t where (t.name like %?1% or ?1 is null)")
    fun findMenuList(name: String?,pageable: Pageable): Page<Menu>

}