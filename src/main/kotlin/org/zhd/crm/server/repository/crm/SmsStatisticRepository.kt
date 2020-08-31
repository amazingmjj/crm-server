package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.SmsStatistic
import java.sql.Timestamp

interface SmsStatisticRepository: CrudRepository<SmsStatistic, Long>{
    @Query(nativeQuery = true, countQuery = "select count(distinct a.id) from v_sms_statistic_list a where (to_char(a.id) like %?1% or ?1 is null) and (a.acct_name like %?2% or ?2 is null) and (to_char(a.type) = ?3 or ?3 is null) and (to_char(a.status) = ?4 or ?4 is null) and (a.comp_name like %?5% or ?5 is null) and (a.link_name like %?6% or ?6 is null) and (a.link_phone like %?7% or ?7 is null) and (to_char(a.create_at,'yyyy-MM-dd HH:mm') >= ?8 or ?8 is null) and (to_char(a.create_at,'yyyy-MM-dd HH:mm') <= ?9 or ?9 is null) and (a.content like %?10% or ?10 is null) order by ?#{#pageable}", value = "select distinct a.id,a.acct_name,a.create_at,a.update_at,a.delay_time,a.content,a.send_count,a.status,a.failure_num from v_sms_statistic_list a where (to_char(a.id) like %?1% or ?1 is null) and (a.acct_name like %?2% or ?2 is null) and (to_char(a.type) = ?3 or ?3 is null) and (to_char(a.status) = ?4 or ?4 is null) and (a.comp_name like %?5% or ?5 is null) and (a.link_name like %?6% or ?6 is null) and (a.link_phone like %?7% or ?7 is null) and (to_char(a.create_at,'yyyy-MM-dd HH:mm') >= ?8 or ?8 is null) and (to_char(a.create_at,'yyyy-MM-dd HH:mm') <= ?9 or ?9 is null) and (a.content like %?10% or ?10 is null) order by ?#{#pageable}")
    fun findInnerSmsStat(id: String?, acctName: String?, type: String?, status: String?, cstmName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, pageable: Pageable): Page<Any>

    @Query(value = "select distinct a.id,a.creator.name,a.createAt,a.updateAt,a.delayTime,a.content,a.sendCount,a.status,a.failureNum,a.mobileArray from SmsStatistic a,Sms b where a.id = b.parent.id and a.sendType = 2 and (to_char(a.id) like %?1% or ?1 is null) and (a.creator.name like %?2% or ?2 is null) and (to_char(a.type) = ?3 or ?3 is null) and (to_char(a.status) = ?4 or ?4 is null) and (b.name like %?5% or ?5 is null) and (b.phone like %?6% or ?6 is null) and (to_char(a.createAt,'yyyy-MM-dd HH:mm') >= ?7 or ?7 is null) and (to_char(a.createAt,'yyyy-MM-dd HH:mm') <= ?8 or ?8 is null) and (a.content like %?9% or ?9 is null)")
    fun findOutSmsStat(id: String?, acctName: String?, type: String?, status: String?, name: String?, phone: String?, startTime: String?, endTime: String?, content: String?, pageable: Pageable): Page<Any>

    @Query(value = "select a.content from SmsStatistic a where a.mobileArray = ?1 and a.sendType = 3 and a.deadTime between ?2 and ?3 order by a.createAt desc")
    fun verifyCodeList(mobile: String, startTime: Timestamp, endTime: Timestamp): List<String>
}