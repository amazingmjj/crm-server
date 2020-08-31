package org.zhd.crm.server.model.projection.statistic

interface SalePerformanceCommProjection {
    fun getDeptName():String
    fun getEmployeeName():String
    fun getNy():String
    fun getDataBweight():String
    fun getNewCount():String
    fun getNewPrice():String
    fun getNewWeight():String
    fun getNewTc():String
    fun getZfPrice():String
    fun getZfBweight():String
    fun getZfTc():String
    fun getDhPrice():String
    fun getDhBweight():String
    fun getDhTc():String
    fun getOldPrice():String
    fun getOldWeight():String
    fun getOldTc():String
    fun getTcje():String
    fun getRownum_(): String
}