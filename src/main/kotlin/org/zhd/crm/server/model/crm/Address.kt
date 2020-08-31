package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

/**
 * 2019年最新的省市区
 * @author samy
 * @date 2020/06/06
 */
@Entity
@Table(name = "t_address")
class Address(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true)
    var code: String = ""

    @Column(nullable = false)
    var name: String = ""

    var parentCode: String? = null

    /**
     * 类型 1 省 2 市 3 区 4 直辖市
     */
    @Column(columnDefinition = "int default 3")
    var type: Int = 1

}