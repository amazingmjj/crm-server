package org.zhd.crm.server.model.projection

interface AreaSaleProjection {
    fun getAreaName(): String
    fun getProvinceName(): String
    fun getWsFlag(): String
    fun getNy():String
    fun getWeight(): String
}