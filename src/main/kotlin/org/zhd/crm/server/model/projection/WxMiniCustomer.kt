package org.zhd.crm.server.model.projection

import io.swagger.annotations.ApiModel
import java.sql.Timestamp

@ApiModel("客户对象(小程序)")
interface WxMiniCustomer {
    fun getId(): Long
    fun getName(): String
    fun getErpCode(): String
    fun getXyCode(): String
    fun getUpdateAt(): Timestamp
}