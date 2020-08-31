package org.zhd.crm.server.model.crm

import com.fasterxml.jackson.annotation.JsonIgnore
import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_customer")
class Customer(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    // 公司名称
    @Column(nullable = false)
    var compName: String = ""
    // 公司简称
    var compNameAb: String = ""
    // 助记码
    var memberCode: String = ""
    // 客户来源
    var customerSource: String = ""
    // 客户渠道
    var customerChannel: String = "其他"
    // ERP编号
    var erpCode: String? = null
    // 电商会员编号
    var ebusiMemberCode: String? = null
    // 电商管理员账号
    var ebusiAdminAcctNo: String? = null
    // 电商编号(统计库）
    var xyCode: String? = null
    // 客户类型 1 公司客户 2 个人客户
    var customerType = 1
    // 客户类型 0:新客户 1:老客户
    @Column(columnDefinition = "int default 0")
    var cstmType: Int = 0
    // 起始日期
    var startTime: Timestamp? = null

    //首次有效开单日期
    @JsonIgnore
    var firstBillDate: Timestamp? = null

    //首次有效提货日期
    @JsonIgnore
    var firstDeliveryDate: Timestamp? = null

    //业务关系
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ref_cstm_busi_relation")
    lateinit var busiRelation: Set<BusiRelation>

    // 客户性质
    @OneToOne
    lateinit var fkCustomProperty: CustomProperty

    // 业务部门
    @OneToOne
    lateinit var fkDpt: Dpt

    // 业务员
    @ManyToOne
    lateinit var fkAcct: Account

    // 工商证照编码
    var busiLicenseCode: String? = null
    // 注册资本
    var registerCapital: String? = null
    // 法人代表
    var legalRept: String? = null
    // 公司logo
    var compLogoUrl: String? = null
    // 公司地址区
    var compArea: String? = null
    // 公司地址市
    var compCity: String? = null
    // 公司地址省
    var compProv: String? = null
    // 公司地址
    var compAddr: String? = null
    // 传真号码
    var faxNum: String? = null
    // 公司规模
    var compSize: String? = null
    // 公司类型
    var compType: String? = null
    // 地区
    var region: String? = null
    // 成立日期
    var setUpDate: Timestamp? = null
    // 实际控制人
    var factController: String? = null
    // 实际控制人身份证
    var factControllerIdno: String? = null

    // 联系人
    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "ref_cstm_linker")
    lateinit var linkers: Set<Linker>

    // 税号
    var tfn: String? = null
    // 开户名称
    var openAcctName: String? = null
    // 开户行
    var openBank: String? = null
    // 开户账号
    var openAcct: String? = null
    // 开票省
    var billProv: String? = null
    // 开票市
    var billCity: String? = null
    // 开票区
    var billArea: String? = null
    // 开票地址
    var billAddr: String? = null
    // 所在行业
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_industry")
    lateinit var industry: Set<Industry>

    // 经营范围
    var busiScope: String? = null
    // 采购周期
    var purchaseCycle: String? = null
    // 月采吨位
    var weightPerMonth: Double? = null
    // 高卖情况 0 无 1 有
    @Column(columnDefinition = "int default 0")
    var sellHighStatus: Int = 0
    // 信用情况
    var creditStatus: String? = null
    // 年销售额
    var annualSales: Double? = null
    // 纳税额
    var taxPay: Double? = null

    // 采购物资品类
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_procumt_goods")
    lateinit var procurementGoods: Set<SupplyCatalog>

    // 采购用途
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_procumt_purpose")
    lateinit var procurementPurpose: Set<Purpose>

    // 希望增加采购物资品类
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_hope_add_goods")
    lateinit var hopeAddGoods: Set<SupplyCatalog>

    // 经营物资品类
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_deal_goods")
    lateinit var dealGoods: Set<SupplyCatalog>

    // 经营用途
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_deal_purpose")
    lateinit var dealPurpose: Set<Purpose>

    /**
     * 订金需求、比例、周期暂时影藏
     * @date 2019/10/14
     */
    // 订金需求
    var depositRequirement: String? = null
    // 订金比例(目前用于客户单位性质)
    @Column(columnDefinition = "varchar2(500) default '20'")
    var depositRate: String? = null
    // 订金周期
    var depositCycle: String? = null

    //加工请求
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ref_cstm_process_req")
    lateinit var processRequirement: Set<ProcessRequirement>

    // 开平尺寸
    var kaipingSize: String? = null
    // 其他合作模式
    var otherCooperateModel: String? = null
    var remark: String? = null
    // 营业执照图片
    var busiLicenseUrl: String? = null
    // 税务登记证图片
    var taxRegisterUrl: String? = null
    // 组织机构代码证图片
    var orgCertificateUrl: String? = null
    // 开票资料图片
    var invoiceInfoUrl: String? = null
    // 状态 1 启用 0 停用 2删除
    var status: Int = 1
    // 客户标识 1 潜在客户 2 正式客户 3 公共客户
    @Column(columnDefinition = "int default 1")
    var mark: Int = 1

    // 是否上锁 如果加锁的客户不变成公共客户 1 加锁 0 未锁
    @Column(columnDefinition = "int default 0")
    var lockStatus = 0

    // 开单日期
    var billDate: Timestamp? = null

    // 转化正式客户的方式(0未转化 1开单自动转化 2手动转化)
    @Column(columnDefinition = "int default 0")
    var transType: Int = 0

    // 创建人
    @ManyToOne
    lateinit var createAcct: Account

    // 公共公司名称(用于客户合并统计用,与抬头合并功能无关)
    var publicCompName: String? = null

    // 公司名称首字母
    var compNameInitial: String? = null

    //流失或转化日期
    var convertDate: Timestamp? = null

    //工作组 (固定：板材组、国标组、协标组)
    var workgroupName: String? = null

    // 是否是后结算单位 0 不是 1 是
    @Column(columnDefinition = "int default 0")
    var settleDelay: Int = 0
    // 客户单位性质 1 全款 2 订金 3 白条
    @Column(columnDefinition = "varchar2(500) default '1,2'")
    var unitProperty: String? = null

    /**
     * 关联主体抬头
     * @date 2020/01/08
     * @author samy
     * @content 方便业务部门绩效统计
     */
    @OneToOne
    @JsonIgnore
    var mainCstm: Customer? = null
}