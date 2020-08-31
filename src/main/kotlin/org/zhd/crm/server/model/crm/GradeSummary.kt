package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

// 客户分级评分
@Entity
@Table(name = "t_grade_summary")
class GradeSummary(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    // 公司抬头(会存在合并情况)
    var compName: String? = null

    // 转化时间
    var transformDate: Timestamp? = null

    // 业务部门
    var dptName: String? = null

    // 业务员名称
    var acctName: String? = null

    // 评分
    var summary: Double? = null

    // 评分等级
    var summaryLevel: String? = null

    // 评分日期
    var summaryDate: Timestamp? = null

    // 上线情况 0其他 1型云 2线下
    var xyCondition: Int = 0
}