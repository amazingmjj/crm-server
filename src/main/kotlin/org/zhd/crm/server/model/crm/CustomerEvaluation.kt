package org.zhd.crm.server.model.crm

import org.hibernate.validator.constraints.Length
import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "t_customer_evaluation")
class CustomerEvaluation(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @NotNull(message = "客户id不能为空")
    var cstmId: Long? = null
    var erpCode: String? = null
    /**
     * 流失原因
     */
    var lossReason: String? = null
    /**
     * 原因描述(限制50个字符)
     */
    @Length(max = 50, message = "不能超过50个字符")
    var reason: String? = null

    @NotNull(message = "所在地区不能为空")
    var areaName: String? = null

    /**
     * 客户性质
     * 多个用逗号分割
     */
    var cstmPropertyIds: String? = null

    /**
     * 有无库存(默认0)
     */
    @Column(columnDefinition = "int default 0")
    var hasStorage: Boolean = false

    /**
     * 库存规模
     */
    @Column(columnDefinition = "number default 0.00")
    var storageCapacity: Double = 0.0

    /**
     * 主营业务
     */
    @NotNull(message = "主营业务不能为空")
//    @Length(max = 50, message = "不能超过50个字符")
    var mainBusi: String? = null

    /**
     * 经营范围
     */
    @NotNull(message = "经营范围不能为空")
//    @Length(max = 50, message = "不能超过50个字符")
    var busiScope: String? = null

    /**
     * 全年销量
     */
    @Column(columnDefinition = "number default 0.00")
    var yearSaleWeight: Double = 0.0

    /**
     * 全年占比
     */
    @Column(columnDefinition = "int default 0")
    var yearPercent: Int = 0

    /**
     * 主要需求物资(我司没有)
     * @author samy
     * @date 2020/06/13
     */
    @NotNull(message = "主要需求物资不能为空")
    var extraGoodsRequirement: String? = null

    /**
     * 品名(多个逗号分割)
     *
     * 主要需求物资(未在我司采购)
     * @author samy
     * @date 2020/06/13
     */
    @NotNull(message = "主要需求物资不能为空")
    var goodsNames: String? = null

    /**
     * 主要运力实现方式
     */
    @NotNull(message = "主要运力实现方式不能空")
    var mainDeliveryWay: String? = null

    /**
     * 物流名称(司机、三方物流等)
     */
//    @NotNull(message="三方物流单位或组织者，司机名称不能为空")
    @Length(max = 50, message = "不能超过50个字符")
    var deliveryName: String? = null

    /**
     * 物流偏好
     */
//    @NotNull(message = "主要物流偏好不能为空")
    var deliveryPrefer: String? = null
}