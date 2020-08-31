package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_linker_modify")
class LinkerModify(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @ManyToOne
    lateinit var linker: Linker

    // 原值
    @Column(nullable = false)
    var originVal: String = ""

    // 修改值
    @Column(nullable = false)
    var modifyVal: String = ""

    // 操作员
    @ManyToOne
    lateinit var creator: Account

    // 字段名称 (客户名称、联系人姓名、联系方式)
    @Column(nullable = false)
    var columnName: String = ""

    @Column(columnDefinition = "varchar(255) default ''")
    var remark: String? = null
}