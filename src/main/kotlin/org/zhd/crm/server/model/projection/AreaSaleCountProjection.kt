package org.zhd.crm.server.model.projection

interface AreaSaleCountProjection {
    fun getAreaName():String
    fun getAreaShortName():String
    fun getProvinceName():String
    fun getSumCount():String
    fun getMyCount():String
    fun getZdCount():String
    fun getJgCount():String
    fun getWlCount():String
    fun getQtCount():String
}