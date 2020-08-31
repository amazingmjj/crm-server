package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 分级系数
@Entity
@Table(name = "t_grade_coefficient")
class GradeCoefficient(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    var name: String = ""

    var parentId: Long? = null

    // 系数
    var coefficient: Double = 0.0

    // 等式名称
    // $销售等分|| + ...
    var equationName: String? = null

    @Column(columnDefinition = "int default 0")
    var nameOrder: Int = 0

}