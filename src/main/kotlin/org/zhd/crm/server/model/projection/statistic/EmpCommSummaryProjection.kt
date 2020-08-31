package org.zhd.crm.server.model.projection.statistic


interface EmpCommSummaryProjection {
    fun getDeptName(): String
    fun getEmployeeName(): String
    fun getNy(): String
    fun getRwweight(): String
    fun getDataBweight(): String
    fun getNewCount(): String
    fun getTcPrice(): String
    fun getZfBweight(): String
    fun getDhBweight(): String
    fun getNewBweight(): String
    fun getNewSumbweight(): String
    fun getDmBweight(): String
    fun getSjPrice(): String
    fun getJbTc(): String
    fun getNewTc(): String
    fun getDmTc(): String
    fun getGmTc(): String
    fun getZfTc(): String
    fun getSuccess(): String
    fun getGmTcReal(): String
    fun getGmTcReduce(): String
    fun getHjTc(): String
    fun getRownum_(): String
}