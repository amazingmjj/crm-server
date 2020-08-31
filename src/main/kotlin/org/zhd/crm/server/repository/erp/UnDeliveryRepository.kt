package org.zhd.crm.server.repository.erp

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.erp.UnDeliveryRecord
import org.zhd.crm.server.model.projection.erp.UndeliveryProjection

interface UnDeliveryRepository :CrudRepository<UnDeliveryRecord,Long> {

    @Query(nativeQuery = true,
    countQuery ="select count(*) from undelivery_record where (to_char(starttime,'yyyy-MM-dd')>=?1 or ?1 is null) " +
            " and (to_char(starttime,'yyyy-MM-dd')<=?2 or ?2 is null) and (?3 is null  or data_source = ?3 )" +
            " and (billcode like %?4% or ?4 is null) and (datas_customername like %?5% or ?5 is null)" +
            " and (employee_code = ?6 or ?6 is null) and (dept_name = ?7 or ?7 is null) " +
            " and (?8 is null or  delevery_state = ?8) and (?9 is null or deal_type = ?9) order by ?#{#pageable}",
    value = "select data_source as wsFlag,billcode,datas_customername," +
            "linkman,linkmobile,delevery_state as goods_flag," +
            "case when isckflag=0 then to_char(overdue_undelivery,'FM999990.009')||'(未提)' else to_char(overdue_undelivery,'FM999990.009') end as real_undelivery," +
            "isckflag,to_char(starttime,'yyyy-MM-dd'),to_char(lifttime,'yyyy-MM-dd'),overdue_days," +
            "overdue_money,real_overdue_money,employee_name,dept_name,to_char(deal_date,'yyyy-MM-dd') as operate_time,deal_type "+
            " from undelivery_record where (to_char(starttime,'yyyy-MM-dd')>=?1 or ?1 is null) " +
            " and (to_char(starttime,'yyyy-MM-dd')<=?2 or ?2 is null) and (?3 is null  or data_source = ?3 )" +
            " and (billcode like %?4% or ?4 is null) and (datas_customername like %?5% or ?5 is null)" +
            " and (employee_code = ?6 or ?6 is null) and (dept_name = ?7 or ?7 is null) " +
            " and (?8 is null or  delevery_state = ?8) and (?9 is null or deal_type = ?9) order by ?#{#pageable}")
    fun findUnDeliveryRecord(startTime:String?,endTime:String?,sourceStr:String?,billCode:String?,
        customer:String?,empCode:String?,dptName:String?,goodsFlagStr:String?,dealTypeStr: String?,
        pageable: Pageable): Page<UndeliveryProjection>
}