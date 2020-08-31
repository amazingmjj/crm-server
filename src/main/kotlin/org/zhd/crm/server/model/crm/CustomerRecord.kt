package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_customer_record")
class CustomerRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {

    @ManyToOne
    lateinit var fkCustom: Customer
    // 记录类型 1 删除 2 潜在客户转化正式客户 3流失 4 公共客户转化正式客户 5 正式客户转化公共客户
    var type: Int = 1

    // 删除人 或者 转化人
    @ManyToOne
    lateinit var fkAcct: Account

    // 删除理由
    var reason: String = ""
}