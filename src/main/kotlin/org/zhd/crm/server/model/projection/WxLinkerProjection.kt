package org.zhd.crm.server.model.projection

import io.swagger.annotations.ApiModel

@ApiModel("微信联系对象")
interface WxLinkerProjection {
    fun getName(): String
    fun getAppName(): String
    fun getAppKey(): String
    fun getAvatar(): String
    fun getOpenId(): String
    fun getSubscribe(): String
    fun getCstmId(): Long?
}