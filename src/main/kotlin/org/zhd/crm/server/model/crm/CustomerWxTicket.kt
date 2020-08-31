package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_customer_wx_ticket")
class CustomerWxTicket(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var appKey: String? = null
    var appName: String? = null
    var ticket: String? = null
    @ManyToOne
    lateinit var fkCstm: Customer
}