package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_auth")
class Auth(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @ManyToOne
    lateinit var fkMenu: Menu
    @Column(columnDefinition = "int default 0")
    var hasCreate: Int = 0
    @Column(columnDefinition = "int default 0")
    var hasUpdate: Int = 0
    @Column(columnDefinition = "int default 0")
    var hasDelete: Int = 0
}
