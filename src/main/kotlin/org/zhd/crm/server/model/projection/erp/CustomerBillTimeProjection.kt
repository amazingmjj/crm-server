package org.zhd.crm.server.model.projection.erp

/**
 * 客户评估开单时间
 * @author samy
 * @date 2020/05/22
 */
interface CustomerBillTimeProjection {
    fun getErpCode(): String
    fun getFirstBillTime(): String
    fun getFirstLadTime(): String
}