package org.zhd.crm.server.model.projection

interface CityProvProjection {
    fun getCity():String
    fun getProv():String
    fun getType():String
    fun getParentCode():String
}