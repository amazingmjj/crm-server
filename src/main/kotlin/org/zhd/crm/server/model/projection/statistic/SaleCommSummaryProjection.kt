package org.zhd.crm.server.model.projection.statistic


interface SaleCommSummaryProjection {
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getNy(): String
    fun getUpweight(): String
    fun getRwweight(): String
    fun getDataBweight(): String
    fun getOtherWeight(): String
    fun getRealWeight(): String
    fun getZfBweight(): String
    fun getNewBweight(): String
    fun getNewTc(): String
    fun getZfTc(): String
    fun getXlTc(): String
    fun getSuccess(): String
    fun getGmTcReal(): String
    fun getGmTcReduce(): String
    fun getGmTc(): String
    fun getHjTc(): String
    fun getRownum_(): String
}