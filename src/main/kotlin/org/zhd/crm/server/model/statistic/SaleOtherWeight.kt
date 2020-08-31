package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "sale_other_weight") //
class SaleOtherWeight (@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    // 业务员编号
    var empId: Long? = null
    // 业务员名称
    var empName: String? = null
    // erp编号
    var platformCode: String? = null
    // 年月
    var yearMonth: String? = null
    // 额外销量
    var weight: Double? = null
}