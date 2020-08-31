package org.zhd.crm.server.model.statistic

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * 客户地区评估
 */
@Entity
@Table(name = "crm_area_record", uniqueConstraints = [UniqueConstraint(columnNames = ["acct_id", "area_name"])])
class CustomerAreaRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @NotNull(message = "acctId不能为空")
    @Column(name = "acct_id", nullable = false)
    var acctId: Long? = null
    @NotNull(message = "地区不能为空")
    @Column(name = "area_name", nullable = false)
    var areaName: String? = null
    /**
     * 年评估销量
     */
    var saleEvaluationWeight: Double? = null
    /**
     * 主要物流现状
     */
    var deliveryStatusInfo: String? = null
    /**
     * 物流对销量的影响
     */
    var effectForSale: String? = null
}