package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.SmsReceive

interface SmsReceiveRepository : CrudRepository<SmsReceive, Long> {
    @Query(value = "from SmsReceive as sr where sr.type = 1 and (to_char(sr.msgId) like %?1% or ?1 is null) and (sr.acctName like %?2% or ?2 is null) and (sr.dptName like %?3% or ?3 is null) and (sr.cstmName like %?4% or ?4 is null) and (sr.name like %?5% or ?5 is null) and (sr.phone like %?6% or ?6 is null) and (to_char(sr.replyTime,'yyyy-MM-dd HH:mm') >= ?7 or ?7 is null) and (to_char(sr.replyTime,'yyyy-MM-dd HH:mm') <= ?8 or ?8 is null) and (sr.content like %?9% or ?9 is null)")
    fun findInnerAll(msgId: String?, acctName: String?, dptName: String?, cstmName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, pageable: Pageable): Page<SmsReceive>

    @Query(value = "from SmsReceive as sr where sr.type in(2,3) and (to_char(sr.msgId) like %?1% or ?1 is null) and (sr.acctName like %?2% or ?2 is null) and (sr.dptName like %?3% or ?3 is null) and (sr.cstmName like %?4% or ?4 is null) and (sr.name like %?5% or ?5 is null) and (sr.phone like %?6% or ?6 is null) and (to_char(sr.replyTime,'yyyy-MM-dd HH:mm') >= ?7 or ?7 is null) and (to_char(sr.replyTime,'yyyy-MM-dd HH:mm') <= ?8 or ?8 is null) and (sr.content like %?9% or ?9 is null)")
    fun findOutAll(msgId: String?, acctName: String?, dptName: String?, cstmName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, pageable: Pageable): Page<SmsReceive>
}