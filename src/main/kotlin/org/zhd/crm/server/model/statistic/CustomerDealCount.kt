package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 客户成交次数
@Entity
@Table(name = "crm_customer_deal_count")
class CustomerDealCount(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 客户编号
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null
    // 成交次数
    var dealCount: Int? = null
}