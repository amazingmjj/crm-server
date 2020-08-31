package org.zhd.crm.server.model.crm

import com.fasterxml.jackson.annotation.JsonIgnore
import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_account")
class Account(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(nullable = false)
    var name: String = ""

    // 所属部门
    @ManyToOne
    lateinit var fkDpt: Dpt

    // 登录账号
    @Column(unique = true, nullable = false)
    var loginAcct: String = ""

    // 密码sha1加密
    @Column(nullable = false, columnDefinition = "varchar2(255) default '1f82c942befda29b6ed487a51da199f78fce7f05'")
    var pwd: String = "1f82c942befda29b6ed487a51da199f78fce7f05"

    @Column(nullable = false)
    var phone: String = ""

    // 状态 1启用 0停用
    var status: Int = 1

    @JsonIgnore
    @OneToMany(mappedBy = "fkAcct")
    lateinit var customers: Set<Customer>

    @ManyToOne
    lateinit var fkRole: Role

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "ref_acct_auth")
    lateinit var auths: Set<Auth>

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "ref_acct_extra_auth")
    var extraAuths: Set<ExtraAuth>? = null

    // 数据等级
    @Column(columnDefinition = "varchar2(255) default '业务员'")
    var dataLevel: String = "业务员"

    // 性别 1 男 2 女
    @Column(columnDefinition = "int default 1")
    var sex: Int = 1

    // 民族
    var national: String? = null
    // 职位
    var position: String? = null
    // 学历
    var edu: String? = null
    // 专业
    var professional: String? = null
    // 邮箱
    var email: String? = null
    // 职称
    var jobTitle: String? = null
    // 电话
    var telephone: String? = null
    // 地址
    var addr: String? = null
    // 籍贯
    var nativePlace: String? = null
    // 婚姻状况
    var maritalStatus: String? = null
    // 政治面貌
    var politicalLandscape: String? = null
    // 生日
    var birthday: String? = null
    // 入职时间
    var inTime: String? = null
    // 工作组
    var workGroup: String? = null
    // 备注
    var remark: String? = null
    // 头像
    var avatar: String? = null
    // 三方平台集成账号的标识(ERP，XY等)
    var platformCode: String? = null
    // 离职情况 0 在职 1 离职
    @Column(columnDefinition = "int default 0")
    var demission: Int = 0
    // 密码等级 0非常弱 1弱（Weak） 2一般（Average） 3强（Strong） 4非常强 5安全（Secure） 6非常安全
    var pwdLevel: Int = 0
    /**
     * 身份证
     * @date 2019/09/18
     * @author samy
     */
    var idCardNo: String? = null
}