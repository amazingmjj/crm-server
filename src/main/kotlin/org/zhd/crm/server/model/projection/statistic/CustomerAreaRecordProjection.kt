package org.zhd.crm.server.model.projection.statistic

import com.fasterxml.jackson.annotation.JsonIgnore

interface CustomerAreaRecordProjection {
    fun getId(): Long
    fun getAreaName(): String
    fun getEmployeeCode(): String
    fun getOldWeight(): String
    fun getNewWeight(): String
    fun getEmployeeName(): String
    fun getAcctId(): String
    fun getDptName(): String
    @JsonIgnore
    fun getDptId(): String
    fun getSaleEvaluationWeight(): String
    fun getDeliveryStatusInfo(): String
    fun getEffectForSale(): String
    fun getRownum_(): String
}