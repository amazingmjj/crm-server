package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 客户欠款
@Entity
@Table(name = "crm_customer_arrears")
class CustomerArrears(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 客户编号
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null
    // 业务员编号
    var employeeCode: String? = null
    // 业务员名称
    var employeeName: String? = null
    // 欠款金额
    var arrearAmount: Double? = null
}