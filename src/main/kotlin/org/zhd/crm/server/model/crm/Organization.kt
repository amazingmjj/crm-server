package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 机构表
@Entity
@Table(name = "t_organization")
class Organization(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true, nullable = false)
    var name: String = ""
    // 简称
    var simpleName: String = ""
    // 0 停用 1启用
    var status: Int = 1
    // 法人代表
    var legalRept: String = ""
    var remark: String = ""
}