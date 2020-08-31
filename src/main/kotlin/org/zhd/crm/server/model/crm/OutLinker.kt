package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_out_linker")
class OutLinker(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {
    @Column(nullable = false, unique = true)
    var phone: String = ""

    var name: String? = null

    // 多个标签用逗号分割
    var label: String? = null

    // 备注
    var remark: String? = null

    @ManyToOne
    lateinit var creator: Account
}