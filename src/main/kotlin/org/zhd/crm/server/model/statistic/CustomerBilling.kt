package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 客户是否代开票
@Entity
@Table(name = "crm_customer_billing")
class CustomerBilling(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 客户编号
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null
}