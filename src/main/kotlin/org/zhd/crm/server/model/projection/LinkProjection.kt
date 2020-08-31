package org.zhd.crm.server.model.projection

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "联系人对象")
interface LinkProjection {
    @ApiModelProperty(name = "linkName", value = "联系人姓名", required = true)
    fun getLinkName(): String

    @ApiModelProperty(name = "mainStatus", value = "主联系人标识(1 主联系 2 非主联系人)", required = false, example = "1")
    fun getMainStatus(): Int

    @ApiModelProperty(name = "linkPhone", value = "联系人姓名", required = true)
    fun getLinkPhone(): String

    @ApiModelProperty(name = "linkId", value = "联系人姓名", required = false, example = "1")
    fun getLinkId(): Long?

    @ApiModelProperty(name = "cstmId", value = "联系人姓名", required = true, example = "1")
    fun getCstmId(): Long

    @ApiModelProperty(name = "compName", value = "联系人姓名", required = false)
    fun getCompName(): String

    @ApiModelProperty(name = "ebsiMemberCode", value = "联系人姓名", required = false)
    fun getEbusiMemberCode(): String

    @ApiModelProperty(name = "erpCode", value = "ERP编号", required = false)
    fun getErpCode(): String

    @ApiModelProperty(name = "commStatus", value = "常用联系人(1 常用联系人 0 非常用联系人)", required = false, example = "0")
    fun getCommStatus(): Int

}