package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "crm_goods_sales")
class GoodsSales(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //对应客户表erpCode
    var memberCode: String = ""
    //品类:0 全部 1 H型钢 2 槽钢 3 角钢 4 圆钢 5 扁钢 6 开平板 7 其他
    @Column(columnDefinition = "varchar2(255) default '其他'")
    var category: String = ""
    // 重量  (实提-退货)
    var weight: Double? = null
    // 金额
    var amount: Double? = null
    //成交日期
    var dealDate: Timestamp? = null

    // 1 线上 0 线下
    @Column(columnDefinition = "int default 0")
    var type: Int = 0
}