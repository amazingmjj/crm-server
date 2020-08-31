package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

/**
 * 属于客户微信联系人
 *
 * @author samy
 * @date 2019/12/28
 */
@Entity
@Table(name = "t_wx_linker")
class WxLinker(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    /**
     * 微信昵称
     */
    var name: String? = null

    /**
     * 公众号名称
     */
    var appName: String? = null
    /**
     * 公众号key
     */
    var appKey: String? = null

    /**
     * 头像
     */
    var avatar: String? = null

    /**
     * OPENID
     */
    var openId: String? = null

    /**
     * 是否关注
     */
    var subscribe: String? = null

    /**
     * 类型(1 客户 2 员工)
     */
    var type: Int? = null

    @ManyToOne
    var fkCstm: Customer? = null

    @ManyToOne
    var acct: Account? = null
}