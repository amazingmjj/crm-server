package org.zhd.crm.server.model.statistic

import org.hibernate.annotations.Table
import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "crm_salesman_goods")
@Table(appliesTo = "crm_salesman_goods", comment = "业务员相关物资品类表")//不包括总经办,江苏智恒达供应部,外贸销售
class SalesmanGoods(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    // 业务员code
    var employeeCode: String? = null
    // 业务员名称
    var employeeName: String? = null
    // 交易日期
    var dealDate: Timestamp? = null
    // 类型 1 线上 0 线下
    @Column(columnDefinition = "int default 0")
    var type: Int = 1
    // 金额
    var amount: Double? = null
    // 吨位
    var weight: Double? = null
    // 品类
    var category: String? = null

}