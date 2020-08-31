package org.zhd.crm.server.model.projection.statistic


interface HighSellDetailProjection {
    fun getDatasCustomername(): String
    fun getNy(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getGm(): String
    fun getRownum_(): String
}