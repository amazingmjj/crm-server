package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_mq_data")
class MqData(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //消息类型 0接收 1发送
    var msgType: Int = 0
    //处理状态 0成功 1失败
    var status: Int = 0
    //处理次数
    var dealNum: Int = 0
    //队列名
    var destination: String = ""
    //队列类型 0其他 1 xy用户行为同步 2 xy用户订单同步 3 xy审核用户同步 4 xy下单同步 5 erp部门同步 6 erp机构同步 7 erp业务员同步 8 erp客户同步 9 erpCode同步 10 erp下单同步 11单个拜访延时同步 12所有拜访延时同步 13短信延时同步 14客户短信回复 15短信消息回执 16 客户合并
    var mqType: Int = 0
    //队列名称
    var mqName: String = ""
    //内容
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    var content: String = ""
    //备注
    var remark: String = ""
}