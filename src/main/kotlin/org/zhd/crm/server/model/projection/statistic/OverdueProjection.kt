package org.zhd.crm.server.model.projection.statistic

interface OverdueProjection {
    fun getWsFlag(): String
    fun getBillcode(): String
    fun getDatasCustomername():String
    fun getLinkman():String
    fun getLinkmobile(): String
    fun getGoodsFlag(): String
    fun getRealUndelivery(): String
    fun getIsckflag():String
    fun getStartDate(): String
    fun getEndDate(): String
    fun getOverdueDate(): String
    fun getUndeliveryMoney(): String
    fun getEmployeeCode(): String
    fun getEmployeeName(): String
    fun getDeptName(): String
    fun getRownum_(): String
}