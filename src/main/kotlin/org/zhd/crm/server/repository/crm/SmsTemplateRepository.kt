package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.SmsTemplate

interface SmsTemplateRepository: CrudRepository<SmsTemplate, Long>{
    @Query(value = "from SmsTemplate as st where (st.groupName like %?1% or ?1 is null) and (st.name like %?2% or ?2 is null) and (st.content like %?3% or ?3 is null)")
    fun findTemplateAll(groupName: String?, name: String?, content: String?, pageable: Pageable): Page<SmsTemplate>

    @Query(value = "select distinct st.groupName from SmsTemplate st")
    fun queryGroupName(): List<String>

    @Query(value = "select count(st.id) from SmsTemplate st where st.groupName = ?1 and st.name = ?2")
    fun countTemplate(groupName: String, name: String): Int
}