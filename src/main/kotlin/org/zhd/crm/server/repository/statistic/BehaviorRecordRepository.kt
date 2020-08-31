package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.BehaviorProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord
import java.sql.Timestamp

interface BehaviorRecordRepository : CrudRepository<BehaviorRecord, Long> {
    fun findByRowkey(rowkey: String): BehaviorRecord?

    @Query(value = "from BehaviorRecord as br where br.userId = ?1 and (br.event like %?2% or ?2 is null) and (to_char(br.time, 'yyyy-MM-dd') = ?3 or ?3 is null) and (br.source like %?4% or ?4 is null) and (br.orderId like %?5% or ?5 is null) and (br.orderNo like %?6% or ?6 is null) and (br.goodsName like %?7% or ?7 is null) and (br.standard like %?8% or ?8 is null) and (br.length like %?9% or ?9 is null) and (br.supply like %?10% or ?10 is null) and (br.warehouse like %?11% or ?11 is null)  and (br.material like %?12% or ?12 is null)  and (br.measure like %?13% or ?13 is null) and (br.toleranceRange like %?14% or ?14 is null) and (br.weightRange like %?15% or ?15 is null) and (br.toleranceStart like %?16% or ?16 is null) and (br.toleranceEnd like %?17% or ?17 is null) and (br.search like %?18% or ?18 is null)")
    fun findAll(userId: String, event: String?, time: String?, source: String?, orderId: String?, orderNo: String?, goodsName: String?, standard: String?, length: String?, supply: String?, warehouse: String?, material: String?, measure: String?, toleranceRange: String?, weightRange: String?, toleranceStart: String?, toleranceEnd: String?, search: String?, pageable: Pageable): Page<BehaviorRecord>

    @Query(value = "from BehaviorRecord as br where br.eventEn='add_cart' and br.userId=?1 and br.time between ?2 and ?3")
    fun goodsAddCart(userId: String, startDate: Timestamp, endDate: Timestamp): List<BehaviorRecord>

    @Query(nativeQuery = true, value = "select count(user_id) from crm_behavior_record where event_en = 'add_cart' and user_id = ?1 and time between ?2 and ?3")
    fun goodsCartCount(userId: String, startDate: Timestamp, endDate: Timestamp): Int

    @Query(nativeQuery = true, value = "select count(user_id) from crm_behavior_record where event_en = 'make_deal' and user_id = ?1 and time between ?2 and ?3")
    fun goodsOrderCount(userId: String, startDate: Timestamp, endDate: Timestamp): Int

    // 客户登录活跃度
    @Query(value = "select count(br.userId) from BehaviorRecord as br where br.userId=?1 and br.eventEn='login' and br.time >= to_date('2018-01-01', 'YYYY-MM-DD')")
    fun loginCount(userId: String): Int

    @Query(nativeQuery = true, countQuery = "select count(*) from v_behavior_record a where a.user_ = ?1 and (a.event_str like %?2% or ?2 is null) and (TO_CHAR(a.time / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH:MI:SS'), 'YYYY-MM-DD') = ?3 or ?3 is null) and (a.source_ like %?4% or ?4 is null) and (a.order_id like %?5% or ?5 is null) and (a.order_no like %?6% or ?6 is null) and (a.goods_name like %?7% or ?7 is null) and (a.standard_ like %?8% or ?8 is null) and (a.length_ like %?9% or ?9 is null) and (a.supply like %?10% or ?10 is null) and (a.warehouse like %?11% or ?11 is null)  and (a.material like %?12% or ?12 is null) and (a.measure like %?13% or ?13 is null) and (a.tolerance_range like %?14% or ?14 is null) and (a.weight_range like %?15% or ?15 is null) and (a.tolerance_start like %?16% or ?16 is null) and (a.tolerance_end like %?17% or ?17 is null) and (a.search like %?18% or ?18 is null) order by ?#{#pageable}", value = "select a.* from v_behavior_record a where a.user_ = ?1 and (a.event_str like %?2% or ?2 is null) and (TO_CHAR(a.time / (1000 * 60 * 60 * 24) + TO_DATE('1970-01-01 08:00:00', 'YYYY-MM-DD HH:MI:SS'), 'YYYY-MM-DD') = ?3 or ?3 is null) and (a.source_ like %?4% or ?4 is null) and (a.order_id like %?5% or ?5 is null) and (a.order_no like %?6% or ?6 is null) and (a.goods_name like %?7% or ?7 is null) and (a.standard_ like %?8% or ?8 is null) and (a.length_ like %?9% or ?9 is null) and (a.supply like %?10% or ?10 is null) and (a.warehouse like %?11% or ?11 is null)  and (a.material like %?12% or ?12 is null) and (a.measure like %?13% or ?13 is null) and (a.tolerance_range like %?14% or ?14 is null) and (a.weight_range like %?15% or ?15 is null) and (a.tolerance_start like %?16% or ?16 is null) and (a.tolerance_end like %?17% or ?17 is null) and (a.search like %?18% or ?18 is null) order by ?#{#pageable}")
    fun findAllByXy(userId: String, event: String?, time: String?, source: String?, orderId: String?, orderNo: String?, goodsName: String?, standard: String?, length: String?, supply: String?, warehouse: String?, material: String?, measure: String?, toleranceRange: String?, weightRange: String?, toleranceStart: String?, toleranceEnd: String?, search: String?, pageable: Pageable): Page<BehaviorProjection>

    @Query(nativeQuery = true, value = "select a.* from v_behavior_record a where a.event ='add_cart' and a.user_= ?1 and to_char(a.time) between ?2 and ?3")
    fun goodsAddCartByXy(userId: String, startDate: Long, endDate: Long): List<BehaviorProjection>

    @Query(nativeQuery = true, value = "select count(a.user_) from v_behavior_record a where a.event = 'add_cart' and a.user_ = ?1 and to_char(a.time) between ?2 and ?3")
    fun goodsCartCountByXy(userId: String, startDate: Long, endDate: Long): Int

    @Query(nativeQuery = true, value = "select count(a.user_) from v_behavior_record a where a.event = 'make_deal' and a.user_ = ?1 and to_char(a.time) between ?2 and ?3")
    fun goodsOrderCountByXy(userId: String, startDate: Long, endDate: Long): Int

    // 客户登录活跃度
    @Query(nativeQuery = true, value = "select count(a.user_) from v_behavior_record a where a.user_ = ?1 and a.event = 'login' and a.time >= '1514736000000'")
    fun loginCountByXy(userId: String): Int
}