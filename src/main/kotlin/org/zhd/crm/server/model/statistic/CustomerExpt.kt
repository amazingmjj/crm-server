package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

// 客户异常次数
@Entity
@Table(name = "crm_customer_expt")
class CustomerExpt(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: String? = null) : BaseModel() {
    // 类型 1 违约次数 2 恶意锁货 3 取消次数
    var type: Int? = null
    // 客户编码
    var erpCode: String? = null
    // 客户名称
    var erpName: String? = null
    // 异常次数
    var exptCount: Int? = null
}