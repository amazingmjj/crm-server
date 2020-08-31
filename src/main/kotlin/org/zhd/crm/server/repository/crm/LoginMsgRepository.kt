package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.LoginMsg

interface LoginMsgRepository: CrudRepository<LoginMsg, Long>{

    @Query(value = "from LoginMsg a where (a.acctName like %?1% or ?1 is null) and ((to_char(a.loginDate,'yyyy-mm-dd') between ?2 and ?3) or (?2 is null and ?3 is null))")
    fun findLoginMsgList(acctName: String?,startTime: String?, endTime: String?, pageable: Pageable): Page<LoginMsg>
}