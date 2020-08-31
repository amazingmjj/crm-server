package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "crm_behavior_record")
class BehaviorRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    // 行为--event_str
    var event: String? = null
    // 行为英文
    var eventEn: String? = null
    // ip
    var ip: String? = null
    // ip地址解析结果
    var ipLocation: String? = null
    // 主键唯一键
    var rowkey: String? = null
    // 来源--source_
    var source: String? = null
    // 时间戳--time_
    var time: Timestamp? = null
    // 用户ID--userId.substring(2, 8).toInt().toString() = user_
    var userId: String? = null
    // 公司名称
    var userName: String? = null
    // 业务员
    var emp: String? = null
    // 品名--goods_name
    var goodsName: String? = null
    // 产地--supply
    var supply: String? = null
    // 材质--material
    var material: String? = null
    // 规格--standard_
    var standard: String? = null
    // 搜索内容--search
    var search: String? = null
    // 仓库--warehouse
    var warehouse: String? = null
    // 开始公差--tolerance_start
    var toleranceStart: String? = null
    // 结束公差--tolerance_end
    var toleranceEnd: String? = null
    // 商品ID--order_id
    var orderId: String? = null
    // 公差范围--tolerance_range
    var toleranceRange: String? = null
    // 重量范围--weight_range
    var weightRange: String? = null
    // 支数
    var amount: String? = null
    // 重量（搜索内容)
    var weight: String? = null
    // 订单号--order_no
    var orderNo: String? = null
    // 价差
    @Column(columnDefinition = "number(19,2) default 0.0")
    var diff: Double = 0.0
    // 订单来源
    var dealFrom: String? = null
    // 类型
    var type: String? = null
    // 延迟时间
    var deploy: String? = null
    //   提单号
    var carryNo: String? = null
    // 金额
    @Column(columnDefinition = "number(19,2) default 0.0")
    var money: Double = 0.0
    // 优先级 (是/否)
    var priority: String? = null
    // 合同号
    var contractNo: String? = null
    // 手机号
    var mphone: String? = null
    // 昵称
    var nickName: String? = null
    // 邀请渠道
    var ditchWay: String? = null
    // 装货地
    var startArea: String? = null
    // 卸货地
    var endArea: String? = null
    // 数量
    @Column(columnDefinition = "number(19,2) default 0.0")
    var num: Double = 0.0
    // 是否特价
    var isSpecialOffer: String? = null
    // 是否新品
    var isNewProduct: String? = null
    // 长度--length_
    var length: String? = null
    // 理计价格
    @Column(columnDefinition = "number(19,2) default 0.0")
    var ljPrice: Double = 0.0
    // 磅计价格
    @Column(columnDefinition = "number(19,2) default 0.0")
    var bjPrice: Double = 0.0
    // 最小限价
    @Column(columnDefinition = "number(19,2) default 0.0")
    var minPrice: Double = 0.0
    // 销售方式
    var sellType: String? = null
    // 品种
    var categoryName: String? = null
    // 最小成交重量
    @Column(columnDefinition = "number(19,6) default 0.0")
    var minWeight: Double = 0.0
    // 挂牌比例
    var percentage: String? = null
    // 地区名
    var region: String? = null
    // 公差搜若内容
    var tolerance: String? = null
    // 日期范围
    var dateRange: String? = null
    // 状态
    var status: String? = null
    // 计量方式--measure
    var measure: String? = null
    // 时长
    var delay: String? = null
}