package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_sms")//发送表
class Sms(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {
    // 收信人手机号
    @Column(nullable = false, columnDefinition = "varchar2(11)")
    var phone: String = ""
    // 收信人姓名
    var name: String = "未知"
    // 不超过255字符
    @Column(nullable = false, columnDefinition = "varchar2(4000)")
    var content: String = ""
    // 发送成功 0 发送中 1 发送失败 2 定时发送 3 已取消 4
    var status: Int = 1

    // 1 为内部联系人 2为外部联系人 3验证码
    var sendType: Int = 1

    // 如果为内部联系人 必填
    var refId: Long? = null

    // 短信id 必填
    var msgId: Long? = null

    // 创建人
    @ManyToOne
    lateinit var creator: Account

    @ManyToOne
    lateinit var parent: SmsStatistic

}