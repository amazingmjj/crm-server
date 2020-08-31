package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_project")//项目信息表
class Project(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true, nullable = false)
    var name: String = ""
    //项目组长
    @ManyToOne
    lateinit var leader: Account
    //1 启用 0 停用
    var status: Int = 1
    //备注
    var remark: String? = null
}