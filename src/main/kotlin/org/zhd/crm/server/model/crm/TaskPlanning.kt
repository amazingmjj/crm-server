package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_task_planning")
class TaskPlanning(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //年度
    var year: Int = 0
    //月份
    var month: Int = 0
    //类型 0公司 1机构 2部门 3个人
    var type: Int = 0
    //公司
    var compName: String? = null
    //机构
    var orgName: String? = null
    //部门
    var dptName: String? = null
    //部门id
    var dptCode: String? = null
    //业务员
    var acctName: String? = null
    //业务员erp编码
    var acctCode: String? = null
    //线上重量
    var onlineTask: Double = 0.0
    //线下重量
    var offlineTask: Double = 0.0
    //板材重量
    var boardTask: Double = 0.0
    //总销量
    var amountTask: Double = 0.0
    //高卖任务金额
    var highValueTask: Double = 0.0
    //新客户数量
    var firstCustNum: Int = 0
    //二次开发数量
    var secondCustNum: Int = 0
}