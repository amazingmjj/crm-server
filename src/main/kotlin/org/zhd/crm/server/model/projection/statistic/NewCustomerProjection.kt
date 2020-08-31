package org.zhd.crm.server.model.projection.statistic


interface NewCustomerProjection {
    fun getCompanyName(): String
    fun getStartDate(): String
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getRownum_(): String
}