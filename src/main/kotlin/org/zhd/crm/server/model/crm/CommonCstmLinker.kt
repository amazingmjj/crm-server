package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

/**
 * 客户常用联系人
 * @author samy
 * @date 2019/08/19
 */
@Entity
@Table(name = "ref_cstm_comm_link")
class CommonCstmLinker(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 客户ID
    var cstmId: Long? = null
    // 联系人ID
    var linkerId: Long? = null
    // 是否是常用联系人 0 非常用 1 常用
    @Column(columnDefinition = "int default 0")
    var status: Int = 0
}