package org.zhd.crm.server.model.projection

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 型云客户单位性质
 * @author samy
 * @date 2019/10/14
 */
@ApiModel("型云客户对象(包含客户单位性质)")
interface EsCustomerUnitProperty {
    @ApiModelProperty(name = "compName", value = "客户公司名称")
    fun getCompName(): String

    @ApiModelProperty(name = "ebusiMemberCode", value = "型云电商编号", example = "000109")
    fun getEbusiMemberCode(): String

    @ApiModelProperty(name = "unitProperty", value = "客户单位性质(1 全款支付 2 定金支付 3 白条支付)", example = "1,2")
    fun getUnitProperty(): String

    @ApiModelProperty(name = "depositRate", value = "订金比例(单位%)", example = "20")
    fun getDepositRate(): String
}