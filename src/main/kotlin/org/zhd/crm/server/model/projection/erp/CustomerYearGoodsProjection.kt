package org.zhd.crm.server.model.projection.erp

interface CustomerYearGoodsProjection {
    fun getYearStr(): String
    fun getErpCode(): String
    fun getName(): String
    fun getRownum_(): String
}