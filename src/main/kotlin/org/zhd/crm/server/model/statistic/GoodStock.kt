package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity //保留批量导入功能和crm_goods_stock表,目前由dubbo接口代替
@Table(name = "crm_goods_stock") //物资库存与报价表
class GoodStock(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //汇总批号 用于确定唯一库存物资
    @Column(nullable = false)
    var sumGoodsBatch: String = ""
    // 品名
    var partsName: String? = null
    // 材质
    var material: String? = null
    // 规格
    var goodsSpec: String? = null
    // 产地
    var productArea: String? = null
    // 公差
    var tolerance: String? = null
    // 长度
    var length: String? = null
    // 重量范围
    var weightRange: String? = null
    // 公差范围
    var toleranceRange: String? = null
    // 仓库
    var warehouseName: String? = null
    // 支数
    var count: Int? = null
    // 计量方式
    var calcWay: String? = null
    // 磅计销售价格
    var poundPrice: Float? = null
    // 理计销售价格
    var calcPrice: Float? = null
    // 可卖数量
    var goodsSupplyNum: Int? = null
    // 可卖重量
    var goodsSupplyWeight: Double? = null
    // 可卖理重
    var goodsSupplyAssistWeight: Double? = null
    // 可让磅价
    var negoPoundPrice: Float? = null
    // 可让理价
    var negoCalcPrice: Float? = null
    // 实物数量
    var goodsNum: Int? = null
    // 实物重量
    var goodsWeight: Float? = null
    // 实物理重
    var goodsCalcWeight: Float? = null
}