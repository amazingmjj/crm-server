package org.zhd.crm.server.repository.erp

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.erp.WarehouseSbill
import java.util.*

interface WarehouseSbillRepository:CrudRepository<WarehouseSbill,Long> {

    @Query(nativeQuery = true,value = "select max(sbill_date) from warehouse_sbill where sbill_billcode=?1")
    fun findLastBillDate(erpCode:String):Date
}