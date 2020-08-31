package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_menu")
class Menu(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(nullable = false)
    var name: String = ""
    // 真实顺序
    @Column(columnDefinition = "int default 1")
    var factOrder: Int = 1
    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "parent_id")
    var parent: Menu? = null
    var iconClass: String? = null
    var pageUrl: String? = null
}