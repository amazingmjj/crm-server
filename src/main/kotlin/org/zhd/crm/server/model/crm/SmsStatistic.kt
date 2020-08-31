package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_sms_statistic")
class SmsStatistic(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {

    @Column(nullable = false, columnDefinition = "varchar2(4000)")
    var content: String = ""

    @ManyToOne
    lateinit var creator: Account

    // 手机号用逗号分割
    @Column(nullable = false, columnDefinition = "varchar2(4000)")
    var mobileArray: String = ""

    // 发送类型 及时 1 定时 2
    var type: Int = 1

    // 发送状态：发送中 1 全部成功 2 发送失败 3,定时发送 4,已取消 5
    var status: Int = 1

    // 失败的个数
    var failureNum: Int = 0

    // 1 为内部联系人 2为外部联系人 3验证码
    var sendType: Int = 1

    // 延迟日期
    var delayTime: Timestamp? =null

    // 失效时间
    var deadTime: Timestamp? =null

    // 发送人数量
    @Column(columnDefinition = "int default 1")
    var sendCount: Int = 1
}