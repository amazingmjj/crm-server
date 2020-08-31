package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_message")
class Message(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 消息类型 0拜访通知 1...
    @Column(columnDefinition = "int default 0")
    var type: Int = 0
    // 内容
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "varchar2(4000) default ''")
    var content: String = ""
    // 内容类型 0JSON 1 String
    @Column(columnDefinition = "int default 0")
    var contentType: Int = 0
    // 消息状态 0未读 1已读
    @Column(columnDefinition = "int default 0")
    var status: Int = 0
    // 业务员id
    var acctCode: String = ""
    // 提示信息
    var msgInfo: String = ""
}