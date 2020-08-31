package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_combine_record")
class CombineRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {
    //原客户id
    var oldCustId: Long? = null
    //原客户erpCode
    var oldCustErpCode: String? = null
    //原客户电商编号
    var oldCustEbusiCode: String? = null
    //原客户名称
    var oldCustName: String? = null
    //合并客户id
    var newCustId: Long? = null
    //合并客户erpCode
    var newCustErpCode: String? = null
    //合并客户电商编号
    var newCustEbusiCode: String? = null
    //合并客户名称
    var newCustName: String? = null
    //操作员
    @ManyToOne
    lateinit var operator: Account
    //操作状态 0成功 1失败
    @Column(columnDefinition = "int default 0")
    var status: Int = 0
}