package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_mobile_info")
class MobileInfo(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 账户id
    var acctId: Long? = null
    // 设备号(一个账户下可能有多个)
    var deviceNum: String? = null
    // 设备类型 0 Ios 1 And
    @Column(columnDefinition = "int default 0")
    var type: Int = 0
}

