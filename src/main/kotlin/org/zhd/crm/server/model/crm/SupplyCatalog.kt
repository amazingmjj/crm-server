package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_supply_catalog")//物资品类表
class SupplyCatalog(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true, nullable = false)
    var name: String = ""
    // 1 启用 0 停用
    var status: Int = 1
}