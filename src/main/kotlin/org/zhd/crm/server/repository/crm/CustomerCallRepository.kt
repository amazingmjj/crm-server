package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerCall
import java.sql.Timestamp

interface CustomerCallRepository : CrudRepository<CustomerCall, Long> {
    @Query(value = "from CustomerCall as cc join cc.customer.linkers as link where link.mainStatus = 1 and to_char(cc.creator.id) = ?5 and(cc.customer.compName like %?1% or ?1 is null) and (to_char(cc.status) = ?2 or ?2 is null) and (to_char(cc.planVisitTime,'yyyy-MM-dd') >= ?3 or ?3 is null) and (to_char(cc.planVisitTime,'yyyy-MM-dd') <= ?4 or ?4 is null)")
    fun findAll(compName: String?, status: String?, startTime: String?, endTime: String?, uid: String,
                pageable: Pageable): Page<CustomerCall>

    @Query(value = "from CustomerCall as cc join cc.customer.linkers as link where link.mainStatus = 1 and to_char(cc.creator.id) = ?5 and cc.status in(1,2) and(cc.customer.compName like %?1% or ?1 is null) and (to_char(cc.status) = ?2 or ?2 is null) and (to_char(cc.planVisitTime,'yyyy-MM-dd') >= ?3 or ?3 is null) and (to_char(cc.planVisitTime,'yyyy-MM-dd') <= ?4 or ?4 is null)")
    fun findHistoryAll(compName: String?, callResult: String?, startTime: String?, endTime: String?, uid: String,
                       pageable: Pageable): Page<CustomerCall>

    @Query(value = "from CustomerCall as cc where cc.status = 0 and to_char(cc.planVisitTime,'yyyy-MM-dd') < to_char(current_timestamp,'yyyy-MM-dd')")
    fun findUnderwayAll(): List<CustomerCall>

    @Query(value = "select count(cc.id),count(case when cc.status = 1 then cc.id end),count(case when cc.status = 2 then cc.id end) from CustomerCall as cc where cc.status in(1,2) and cc.creator.id = ?1 ")
    fun callCount(uid: Long): List<Int>

    @Query(value = "from CustomerCall as cc where cc.status = ?1 and cc.planVisitTime between ?2 and ?3")
    fun findCallForMobile(status: Int, startTime: Timestamp, endTime: Timestamp): List<CustomerCall>

    @Query(value = "from CustomerCall as cc join cc.customer.linkers as link where link.mainStatus = 1 and cc.creator.id = ?1 and cc.customer.id = ?2 and cc.status = ?3")
    fun findCallList(uid: Long, cstmId: Long, status:Int, pageable: Pageable): Page<CustomerCall>

    @Query(value = "select count(cc.id) from CustomerCall as cc where cc.status = 0 and cc.customer.id = ?1 and cc.creator.id = ?2 and to_char(cc.planVisitTime,'yyyy-MM-dd') = ?3")
    fun repeatCallCount(cstmId: Long, uid: Long, planVisitTime: String): Int
}