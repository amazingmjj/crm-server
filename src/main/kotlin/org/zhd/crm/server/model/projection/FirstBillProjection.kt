package org.zhd.crm.server.model.projection


interface FirstBillProjection {
    fun getCompanyCode():String
    fun getMinDeliveryDate(): String
    fun getMinSaleDate(): String
}