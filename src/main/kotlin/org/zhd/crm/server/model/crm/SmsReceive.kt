package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_sms_receive")
class SmsReceive(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 回复人手机号
    @Column(nullable = false, columnDefinition = "varchar2(11)")
    var phone: String = ""
    // 回复人姓名
    var name: String? = null
    // 不超过4000字符 回复内容j
    @Column(nullable = false, columnDefinition = "varchar2(4000)")
    var content: String = ""
    // 客户回复短信时间
    var replyTime: Timestamp? = null
    // 短信id 必填
    var msgId: Long? = null
    // 客户名称
    var cstmName: String? = null
    // 部门名称
    var dptName: String? = null
    // 业务员名称(客户对应的)
    var acctName: String? = null
    // 短信类型 1内部联系人回复  2外部联系人回复 3陌生人回复
    @Column(columnDefinition = "int default 1")
    var type: Int = 1
}