package org.zhd.crm.server.model.projection.statistic


interface DeliveryDetailProjection {
    fun getDatasCustomername(): String
    fun getProfitDate(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getZfBweight(): String
    fun getDhBweight(): String
    fun getTcje(): String
    fun getRownum_(): String
}