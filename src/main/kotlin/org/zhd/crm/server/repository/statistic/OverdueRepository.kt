package org.zhd.crm.server.repository.statistic

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.statistic.OverdueProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

interface OverdueRepository :CrudRepository<BehaviorRecord, Long> {

    @Query( nativeQuery = true,
            value = "select wsFlag,billcode,datas_customername,linkman,linkmobile,goods_flag," +
            "real_undelivery,isckflag,start_date,end_date,overdue_date,undelivery_money," +
            "employee_code,employee_name,dept_name from V_OVERDUE_UNDELIVERY where billcode = ?1")
    fun queryByBillCode(billCode:String): List<OverdueProjection>


}