package org.zhd.crm.server.model.projection

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Lob

interface CustomerEvaluationObjProjection {
    fun getId(): Long
    fun getCompName(): String
    fun getMark(): String
    fun getCreateAt(): String
    fun getDptName(): String
    fun getCustomerPropertyMark(): String
    fun getEmployeeName(): String
    fun getLinkerName(): String
    fun getCstmId(): Long
    fun getAreaName(): String
    fun getBusiScope(): String
    fun getCstmPropertyIds(): String
    fun getDeliveryName(): String
    fun getDeliveryPrefer(): String
    fun getErpCode(): String
    fun getGoodsNames(): String
    fun getHasStorage(): String
    fun getLossReason(): String
    fun getMainBusi(): String
    fun getMainDeliveryWay(): String
    fun getReason(): String
    fun getStorageCapacity(): String
    fun getYearPercent(): String
    fun getYearSaleWeight(): String
    @JsonIgnore
    fun getEmployeeId(): String
    @JsonIgnore
    fun getDptId(): String
    fun getExtraGoodsRequirement(): String
    fun getFirstBillDate(): String
    fun getFirstDeliveryDate(): String
    @JsonIgnore
    fun getStatus(): Int
}