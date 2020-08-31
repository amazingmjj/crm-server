package org.zhd.crm.server.model.projection.erp

/**
 * 客户评估年度销量
 * @author samy
 * @date 2020/05/22
 */
interface CustomerYearSaleProjection {
    fun getYearStr(): String
    fun getErpCode(): String
    fun getBillWeight(): String
    fun getLadWeight(): String
    fun getHighSale(): String
}