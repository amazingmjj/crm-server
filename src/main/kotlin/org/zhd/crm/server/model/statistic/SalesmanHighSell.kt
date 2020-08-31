package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "crm_salesman_high_sell")
class SalesmanHighSell(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 业务员code
    var employeeCode: String? = null
    // 业务员名称
    var employeeName: String? = null
    // 客户code
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null
    // 交易日期
    var dealDate: Timestamp? = null
    // 高卖金额
    var highAmount: Double? = null
}