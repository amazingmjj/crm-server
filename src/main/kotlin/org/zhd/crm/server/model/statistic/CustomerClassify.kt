package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

// 客户分级明细表
@Entity
@Table(name = "crm_customer_classify")
class CustomerClassify(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 类型
    var type: String? = null
    // 名称
    var name: String? = null
    // 值
    var value: String? = null
    // 日期
    var dealDate: Timestamp? = null
    // 客户编码
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null

}