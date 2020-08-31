package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_linker_record")
class LinkerRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var name: String = ""
    @Column(nullable = false)
    var phone: String = ""
    // 1男 2女 3未知
    @Column(columnDefinition = "int default 1")
    var sex: Int = 1
    var age: Int = 0
    // 学历
    var edu: String? = null
    // 籍贯
    var nativePlace: String? = null
    // 微信号
    var wxNo: String? = null
    // QQ号
    var qqNo: String? = null
    // 微博名称
    var wbName: String? = null
    // 其他联系方式
    var otherLinkWay: String? = null
    // 是否是主联系人 0 不是 1是
    @Column(columnDefinition = "int default 0")
    var mainStatus: Int = 0
    // 职位
    var position: String? = null
    // 备注
    var remark: String? = null
    // 删除人姓名
    var delName: String = ""
    // 删除原因
    var delReason: String = ""
    // 删除日期
    var delDate: Timestamp? = null
    // 客户名称
    @Column(nullable = false)
    var cstmName: String = ""
}