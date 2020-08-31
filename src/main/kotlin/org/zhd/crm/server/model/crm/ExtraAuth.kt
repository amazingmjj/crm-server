package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_extra_auth")
class ExtraAuth (@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long?=null):BaseModel(){
    @ManyToOne
    lateinit var fkMenuAuth: MenuAuth
    @Column(columnDefinition = "int default 0")
    var hasCheck: Int = 0
}