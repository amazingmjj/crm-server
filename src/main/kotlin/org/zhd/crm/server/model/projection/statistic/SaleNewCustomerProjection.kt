package org.zhd.crm.server.model.projection.statistic


interface SaleNewCustomerProjection {
    fun getDatasCustomername(): String
    fun getStartdate(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getWeightGm(): String
    fun getMoneyGm(): String
    fun getTcGm(): String
    fun getWeightDm(): String
    fun getMoneyDm(): String
    fun getTcDm(): String
    fun getZfBweight(): String
    fun getWeightSum(): String
    fun getTcSum(): String
    fun getRownum_(): String
}