package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_communicate")
class Communicate(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 添加人
    @ManyToOne
    lateinit var fkAcct: Account

    // 沟通类型
    var contactType: String = ""

    // 沟通日子
    var contactDate: Timestamp? = null

    // 沟通内容
    var content: String = ""

    @ManyToOne
    lateinit var fkCstm: Customer
}