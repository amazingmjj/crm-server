package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_dpt")
class Dpt(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true, nullable = false)
    var name: String = ""
    var leader: String = ""
    // 1 启用 0停用
    var status: Int = 1
    var remark: String = ""
    @ManyToOne
    lateinit var fkOrg: Organization

}