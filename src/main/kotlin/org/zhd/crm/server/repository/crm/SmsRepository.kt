package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Sms

interface SmsRepository : CrudRepository<Sms, Long> {
    @Query(value = "from Sms as s where s.parent.id = ?1")
    fun findByParent(id: Long): List<Sms>

    fun findByMsgId(msgId: Long): Sms?

    @Query(value = "from Sms as s where s.parent.id = ?1 and s.phone = ?2")
    fun findListByIdAndPhone(parentId: Long, phone: String): List<Sms>

    @Query(nativeQuery = true, countQuery = "select count(*) from v_sms_list s where s.parent_id = ?1 and (to_char(s.status) = ?2 or ?2 is null) and (s.comp_name like %?3% or ?3 is null) and (s.link_name like %?4% or ?4 is null) and (s.link_phone like %?5% or ?5 is null) and (to_char(s.msg_id) like %?6% or ?6 is null) order by ?#{#pageable}", value = "select * from v_sms_list s where s.parent_id = ?1 and (to_char(s.status) = ?2 or ?2 is null) and (s.comp_name like %?3% or ?3 is null) and (s.link_name like %?4% or ?4 is null) and (s.link_phone like %?5% or ?5 is null) and (to_char(s.msg_id) like %?6% or ?6 is null) order by ?#{#pageable}")
    fun findInnerSms(parentId: Long, status: String?, cstmName: String?, name: String?, phone: String?, msgId: String?, pageable: Pageable): Page<Any>

    @Query(value = "select s.name,s.phone,s.parent.id,s.status,s.msgId,s.createAt,s.updateAt from Sms s where s.sendType = 2 and s.parent.id = ?1 and (to_char(s.status) = ?2 or ?2 is null) and (s.name like %?3% or ?3 is null) and (s.phone like %?4% or ?4 is null) and (to_char(s.msgId) like %?5% or ?5 is null)")
    fun findOutSms(parentId: Long, status: String?, name: String?, phone: String?, msgId: String?, pageable: Pageable): Page<Any>
}