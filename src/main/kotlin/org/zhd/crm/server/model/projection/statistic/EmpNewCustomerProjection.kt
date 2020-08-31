package org.zhd.crm.server.model.projection.statistic


interface EmpNewCustomerProjection {
    fun getDatasCustomername(): String
    fun getStartdate(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getDataBweight(): String
    fun getGm(): String
    fun getGmTc(): String
    fun getRownum_(): String
}