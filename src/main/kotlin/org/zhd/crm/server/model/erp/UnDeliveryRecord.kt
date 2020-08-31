package org.zhd.crm.server.model.erp

import java.util.*
import javax.persistence.*

/**
 * 超期未提处理表
 */
@Entity
@Table(name = "undelivery_record")
class UnDeliveryRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var undeliveryRecordId: Long? = null) {

    /**
     * 来源 0:ERP 1:型云
     */
    var dataSource:String = ""

    /**
     * 单据号
     */
    var billcode:String =""

    /**
     * 客户
     */
    var datasCustomername :String? = null

    /**
     * 联系人
     */
    var linkman :String? = null

    /**
     * 联系人电话
     */
    var linkmobile :String? = null

    /**
     * 提货状态 0:已完成 1:未完成
     */
    var deleveryState : String = ""

    /**
     *  超期未提
     */
    var overdueUndelivery :Double =0.00

    /**
     * 0未提 1已提
     */
    var isckflag : Int ? = 0

    /**
     * 开始时间
     */
    var starttime :Date?= null

    /**
     * 提货截止时间
     */
    var lifttime :Date ? = null

    /**
     * 超期天数
     */
    var overdueDays : Int? = 0

    /**
     * 超期金额
     */
    var overdueMoney :Double? = 0.00

    /**
     * 实际超期金额
     */
    var realOverdueMoney :Double? = 0.00

    /**
     * 业务员
     */
    var employeeCode :String? = null

    /**
     * 业务员
     */
    var employeeName :String? = null
    /**
     * 部门
     */
    var deptName :String? = null
    /**
     * 操作类型 0:免收 1：收款 2：删除
     */
    var dealType : String? = null
    /**
     * 操作时间
     */
    var dealDate :Date = Date()

    /**
     *  操作人
     */
    var operatorCode :String? = null
    /**
     * 操作人名字
     */
    var operatorName :String? = null
    /**
     * 备注
     */
    var remark :String? = null

}