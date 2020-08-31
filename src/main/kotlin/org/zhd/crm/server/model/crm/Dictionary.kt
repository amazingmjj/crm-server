package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_dictionary")
class Dictionary(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 字典类型 0多音字 1新浪期货资源代码
    @Column(columnDefinition = "int default 0")
    var type = 0
    // 名称
    var name: String = ""
    // 值
    var value: String = ""
    // 备注
    var remark: String = ""
}