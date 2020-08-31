package org.zhd.crm.server.model.projection

interface SmsLinkProjection {//jpa Projection
    fun getLinkName(): String
    fun getLinkPhone(): String
    fun getCompName(): String
    fun getAcctName(): String
    fun getDptName(): String
}