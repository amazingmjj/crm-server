package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*
import org.hibernate.type.MaterializedClobType

@Entity
@Table(name = "t_error_log")
class ErrorLog(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {

//    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "varchar2(4000) default ''")
    var content: String = ""
}