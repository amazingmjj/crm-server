package org.zhd.crm.server.model.projection.statistic

interface SalePerformanceNewCustomerProjection {
    fun getDatasCustomername(): String
    fun getStartdate(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getDataBweight(): String
    fun getZfBweight(): String
    fun getNewTc(): String
    fun getRownum_(): String
}