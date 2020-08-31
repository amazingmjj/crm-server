package org.zhd.crm.server.model.projection.erp

interface UndeliveryProjection {
    fun getWsFlag(): String
    fun getBillcode(): String
    fun getDatasCustomername():String
    fun getLinkman():String
    fun getLinkmobile(): String
    fun getGoodsFlag(): String
    fun getRealUndelivery(): String
    fun getIsckflag(): String
    fun getStartDate(): String
    fun getEndDate(): String
    fun getOverdueDate(): String
    fun getUndeliveryMoney(): String
    fun getRealUndeliveryMoney(): String
    fun getEmployeeName(): String
    fun getDeptName(): String
    fun getOperateTime(): String
    fun getState(): String
    fun getRownum_(): String
}