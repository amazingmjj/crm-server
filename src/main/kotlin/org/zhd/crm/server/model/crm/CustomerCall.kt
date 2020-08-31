package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_customer_call")
class CustomerCall(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @ManyToOne
    lateinit var customer: Customer

    // 添加人
    @ManyToOne
    lateinit var creator: Account

    // 计划拜访日期
    @Column(nullable = false, columnDefinition = "TIMESTAMP (6) default sysdate")
    var planVisitTime: Timestamp = Timestamp(System.currentTimeMillis())

    // 计划完成日期
    @Column(nullable = false)
    var planDate: Timestamp = Timestamp(System.currentTimeMillis())

    // 打卡时间
    var clockTime: Timestamp? = null

    // 拜访状态 0 拜访中 1 拜访成功 2 拜访失败(超时)
    @Column(columnDefinition = "int default 0")
    var status: Int = 0

    // 电脑端： PC 移动端：And, Ios
    @Column(columnDefinition = "varchar2(255) default 'pc'")
    var callType: String = "PC"

    // 经度
    var longitude: String? = null

    // 维度
    var latitude: String? = null

    // 定位地址
    var locateAddr: String? = null

    // 访问备注
    @Column(columnDefinition = "varchar(255) default ''")
    var remark: String? = null
}
