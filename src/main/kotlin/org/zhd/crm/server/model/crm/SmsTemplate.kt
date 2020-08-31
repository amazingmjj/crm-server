package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_sms_template")
class SmsTemplate(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var groupName: String? = null
    var name: String? = null
    @Column(nullable = false, columnDefinition = "varchar2(4000)")
    var content: String = ""
}