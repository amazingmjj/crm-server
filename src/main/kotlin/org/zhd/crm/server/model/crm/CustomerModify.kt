package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_customer_modify")
class CustomerModify(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @ManyToOne
    lateinit var customer: Customer

    // 原值
    @Column(nullable = false)
    var originVal: String = ""

    // 修改值
    @Column(nullable = false)
    var modifyVal: String = ""

    // 操作员
    @ManyToOne
    lateinit var creator: Account

    // 字段名称 (公司名称，业务关系， 客户性质，业务部门， 业务员， 工商证照编码， 公司地址， 主要联系人， 联系电话， 税号， 开户银行， 开户名称， 开户账号， 开票地址)
    @Column(nullable = false)
    var columnName: String = ""

    @Column(columnDefinition = "varchar(255) default ''")
    var remark: String = ""
}