package org.zhd.crm.server.model.projection

interface CustomerPurchaseFrequencyProjection {
    fun getDptId(): Long
    fun getDptName(): String
    fun getAcctId(): Long
    fun getEmployeeName(): String
    fun getErpCode(): String
    fun getCompName(): String
    fun getPhone(): String
    fun getUnDealDays():Int
    fun getPeriod(): Int
    fun getOverDays(): Int
    fun getId(): Long
    fun getFeedBack(): String
    fun getUpdateAt(): String
    fun getRownum_(): String
}