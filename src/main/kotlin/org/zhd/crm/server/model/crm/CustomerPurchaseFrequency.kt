package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

/**
 * 客户购买超频跟踪表
 */
@Entity
@Table(name = "t_customer_purchase_freq")
class CustomerPurchaseFrequency(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var dptName: String? = null
    var employeeName: String? = null
    var erpCode: String? = null
    var compName: String? = null
    var phone: String? = null
    /**
     * 未购买天数
     */
    var unDealDays: Integer? = null
    /**
     * 周期
     */
    var period: Integer? = null
    /**
     * 超期天数
     */
    var overDays: Integer? = null

    /**
     * 联系反馈
     */
    var feedBack: String? = null
    var dptId: Long? = null
    var acctId: Long? = null
}