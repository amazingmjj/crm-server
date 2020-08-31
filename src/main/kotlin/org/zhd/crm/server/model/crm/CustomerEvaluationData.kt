package org.zhd.crm.server.model.crm

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.validator.constraints.Length
import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

/**
 * 客户评估数据
 * 其他供应商
 * 其他供应商优势
 */
@Entity
@Table(name = "t_customer_eval_data")
class CustomerEvaluationData(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

    @JsonIgnore
    var parentId: Long? = null

    @Length(max = 50, message = "不能超过50个字符")
    var supplyName: String? = null

    @Length(max = 50, message = "不能超过50个字符")
    var supplyPrefer: String? = null
}