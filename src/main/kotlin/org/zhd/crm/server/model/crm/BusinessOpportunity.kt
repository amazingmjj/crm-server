package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_business_opportunity")
class BusinessOpportunity(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 商机名称
    @Column(nullable = false)
    var opptyName: String = ""
    // 客户名称
    var cstmName: String? = null
    // 客户id,如果已录入客户就取id
    var cstmCode: String? = null
    // 联系人,如果已录入客户就取主联系人
    var linkerName: String? = null
    // 联系方式,如果已录入客户就取主联系人
    var linkerPhone: String? = null
    // 提交人
    @ManyToOne
    lateinit var creator: Account
    // 商机地址
    var opptyAddr: String? = null
    // 商机描述
    @Column(columnDefinition = "varchar(255) default ''")
    var remark: String=""
}