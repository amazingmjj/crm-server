package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_bank_info")
class BankInfo(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 开户名称
    var name: String = ""

    // 开户银行
    var openBank: String = ""

    // 银行账号
    var bankAcct: String = ""

    // 备注
    var remark: String = ""

    // 是否是客户的主账号  1 是 0 否
    var mainAcct: Int = 0

    @ManyToOne
    lateinit var fkCstm: Customer

}