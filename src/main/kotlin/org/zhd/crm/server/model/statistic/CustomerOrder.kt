package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "crm_customer_order")
class CustomerOrder(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    // 电商编号
    @Column(nullable = false)
    var acctNo: String = ""
    // 订单编号
    @Column(nullable = false)
    var orderNo: String = ""
    // 订单状态
    var status: String = ""
    @Column(columnDefinition = "varchar(255) default 'pc'")
    var type: String = ""
    //订单日期
    @Column(nullable = false)
    var orderTime: Timestamp = Timestamp(System.currentTimeMillis())

    var dptId: Long? = null

    var dptName: String? = null

    var orgId: Long? = null

    var orgName: String? = null

    var employeeName: String? = null

    var crmAcctId: Long? = null

}