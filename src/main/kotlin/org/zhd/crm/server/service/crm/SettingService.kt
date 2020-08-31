package org.zhd.crm.server.service.crm

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.model.crm.Dictionary
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.service.ActiveMqSenderService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Service
@Transactional
class SettingService {
    @Autowired
    private lateinit var orgRepo: OrganizationRepository
    @Autowired
    private lateinit var dptRepo: DptRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var menuRepo: MenuRepository
    @Autowired
    private lateinit var roleRepo: RoleRepository
    @Autowired
    private lateinit var authRepo: AuthRepository
    @Autowired
    private lateinit var taskRepo: TaskPlanningRepository
    @Autowired
    private lateinit var gradeRepo: GradeCoefficientRepository
    @Autowired
    private lateinit var errLogRepo: ErrorLogRepository
    @Autowired
    private lateinit var mqDataRepo: MqDataRepository
    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService
    @Autowired
    private lateinit var cstmRepo: CustomerRepository
    @Autowired
    private lateinit var busiRelationRepo: BusiRelationRepository
    @Autowired
    private lateinit var linkRepo: LinkerRepository
    @Autowired
    private lateinit var customPropertyRepo: CustomPropertyRepository
    @Autowired
    private lateinit var bkIfRepo: BankInfoRepository
    @Autowired
    private lateinit var customerRepo: CustomerRepository
    @Autowired
    private lateinit var loginMsgRepository: LoginMsgRepository
    @Autowired
    private lateinit var dictRepo: DictionaryRepository
    @Autowired
    private lateinit var wxLinkerRepo: WxLinkerRepository
    @Autowired
    private lateinit var addressRepo: AddressRepository
    @Autowired
    private lateinit var menuAuthRepository: MenuAuthRepository
    @Autowired
    private lateinit var extraAuthRepository: ExtraAuthRepository

    private val log = LoggerFactory.getLogger(SettingService::class.java)

    fun orgSave(org: Organization) = orgRepo.save(org)

    fun orgAll() = orgRepo.findCommAll()

    fun orgFindByName(name: String) = orgRepo.findByName(name)

    fun orgFindById(id: Long) = orgRepo.findOne(id)

    fun orgFindList(name: String?, id: String?, simpleName: String?, remark: String?, pg: Pageable) = orgRepo.findAll(name, id, simpleName, remark, pg)

    fun orgUpdate(org: Organization): Organization {
        var targetOrg = orgRepo.findOne(org.id)
        var resultOrg = commUtil.autoSetClass(org, targetOrg) as Organization
        orgRepo.save(resultOrg)
        return resultOrg
    }

    fun orgDel(id: Long) = orgRepo.delete(id)

    fun orgBatchUpdateStatus(status: Int, ids: List<Long>) = orgRepo.batchUpdateStatus(status, ids)

    fun dptFindByName(name: String) = dptRepo.findByName(name)

    fun dptSave(dpt: Dpt, orgId: Long = 0): Dpt {
        if (orgId > 0) dpt.fkOrg = orgRepo.findOne(orgId)
        return dptRepo.save(dpt)
    }

    fun dptSave(dpt: Dpt) = dptRepo.save(dpt)

    //查询业务部门
    fun dptFindById(id: Long) = dptRepo.findOne(id)

    //查询所有业务部门
    fun dptFindAll() = dptRepo.findCommAll()

    //查询所有业务部门
    fun findAllDpt(dptName: String?, pageable: Pageable) = dptRepo.findAllDpt(dptName, pageable)

    fun dptFindList(name: String?, id: String?, orgName: String?, orgShortName: String?, remark: String?, pg: Pageable) = dptRepo.findAll(name, id, orgName, orgShortName, remark, pg)

    fun dptUpdate(dpt: Dpt, orgId: Long): Dpt {
        dpt.fkOrg = orgRepo.findOne(orgId)
        val targetOrg = dptRepo.findOne(dpt.id)
        val resultOrg = commUtil.autoSetClass(dpt, targetOrg) as Dpt
        dptRepo.save(resultOrg)
        return resultOrg
    }

    fun dptDel(id: Long) = dptRepo.delete(id)

    fun dptBatchUpdateStatus(status: Int, ids: List<Long>) = dptRepo.batchUpdateStatus(status, ids)

    //查询业务员
    fun acctFindById(id: Long) = acctRepo.findOne(id)

    // 根据身份证查询业务员
    fun acctFindByIdCard(idcard: String) = acctRepo.findByIdCardNo(idcard)

    fun acctFindByName(name: String) = acctRepo.findByName(name)

    fun findByPlatformCode(erpCode: String) = acctRepo.findByPlatformCode(erpCode)

    //校验登录账号是否存在
    fun loginAcctCount(name: String) = acctRepo.loginAcctCount(name)

    //查询所有业务员
    fun acctFindAll(acctName: String?, pg: Pageable) = acctRepo.findAllExSuper(acctName, pg)

    /**
     * 查询所有业务员
     * @author mjj
     * @Date 2020-04-08
     */
    fun acctFindAllAuth(acctName: String?, pg: Pageable, uid: Long?): Page<Account> {
        //区分权限
        if (null != uid) {
            val account = acctRepo.findOne(uid)
            return when (account.dataLevel) {
                "业务员" -> {
                    acctRepo.findAllAccount(acctName, account.id, null, pg)
                }
                "部门" -> {
                    acctRepo.findAllAccount(acctName, null, account.fkDpt.name, pg)
                }
                else -> {
                    acctRepo.findAllAccount(acctName, null, null, pg)
                }
            }
        } else {
            return acctRepo.findAllAccount(acctName, null, null, pg)
        }

    }

    fun changeName(acctList: List<Account>): List<Account> {
        val tempList = ArrayList<Account>();
        acctList.map { item ->
            val temp = Account()
            BeanUtils.copyProperties(item, temp)
            if (temp.name.contains("x") && StringUtils.isNotEmpty(temp.remark)) {
                temp.name = temp.remark!! + "(" + temp.name + ")"
            }
            tempList.add(temp)
        }
        return tempList
    }

    //查询电商部门的业务员
    fun findAcctForXy() = acctRepo.findAcctForXy()

    fun acctFindList(name: String?, loginAcct: String?, orgName: String?, dptName: String?, position: String?, phone: String?, roleName: String?, dataLevel: String?, pg: Pageable) = acctRepo.findCommAll(name, loginAcct, orgName, dptName, position, phone, roleName, dataLevel, pg)

    fun acctSave(acct: Account) = acctRepo.save(acct)

    fun acctSave(acct: Account, dptId: Long, roleId: Long): Account {
        var obj = acct
        obj.fkDpt = dptRepo.findOne(dptId)
        obj.fkRole = roleRepo.findOne(roleId)
        if (obj.id != null) {
            obj = acctRepo.findOne(obj.id)
            if (obj.fkRole.id != acct.fkRole.id) {//角色修改需要将权限重新分配
                //原权限
                val originMap = HashMap<Long, Long>()
                obj.auths.map { s ->
                    originMap[s.fkMenu.id!!] = s.id!!
                }
                //新权限
                val newMap = HashMap<Long, Long>()
                acct.fkRole.auths.map { s ->
                    newMap[s.fkMenu.id!!] = s.id!!
                }
                val auths = HashSet<Auth>()
                originMap.keys.map { key ->
                    val originAuth = authRepo.findOne(originMap[key])
                    //新权限中没有该菜单，则取该菜单权限,有则以新权限为准
                    if (!newMap.containsKey(key)) auths.add(originAuth)
                }
                acct.fkRole.auths.map { ath ->
                    auths.add(ath)
                }
                obj.auths = auths

                //特殊权限
                if (null!=acct.fkRole.extraAuths&& acct.fkRole.extraAuths!!.isNotEmpty()){
                    //原权限
                    val originExtraMap = HashMap<Long, Long>()
                    if (null!=obj.extraAuths&& obj.extraAuths!!.isNotEmpty()){
                        obj.extraAuths!!.map { s ->
                            originExtraMap[s.fkMenuAuth.menu.id!!] = s.id!!
                        }
                    }
                    //新权限
                    val newExtraMap = HashMap<Long, Long>()
                    acct.fkRole.extraAuths!!.map { s ->
                        newExtraMap[s.fkMenuAuth.menu.id!!] = s.id!!
                    }
                    val extraAuthSet = HashSet<ExtraAuth>()
                    originExtraMap.keys.map { key ->
                        val originExtraAuth = extraAuthRepository.findOne(originExtraMap[key])
                        //新权限中没有该菜单，则取该菜单权限,有则以新权限为准
                        if (!newExtraMap.containsKey(key)) extraAuthSet.add(originExtraAuth)
                    }
                    acct.fkRole.extraAuths!!.map { ath ->
                        extraAuthSet.add(ath)
                    }
                    obj.extraAuths = extraAuthSet

                }
            }
            obj = commUtil.autoSetClass(acct, obj, arrayOf("id", "updateAt", "createAt", "customers", "auths" , "extraAuths")) as Account
        } else {
            val auths = HashSet<Auth>()
            obj.fkRole.auths.map { ath ->
                auths.add(ath)
            }
            obj.auths = auths

            if(null!=obj.fkRole.extraAuths&& obj.fkRole.extraAuths!!.isNotEmpty()){
                val extraAuths = HashSet<ExtraAuth>()
                obj.fkRole.extraAuths!!.map { ath ->
                    extraAuths.add(ath)
                }
                obj.extraAuths = extraAuths
            }

        }
        return acctRepo.save(obj)
    }

    fun acctBatchUpdateStatus(status: Int, ids: List<Long>) = acctRepo.batchUpdateStatus(status, ids)

    fun acctDel(id: Long) {
        val obj = acctRepo.findOne(id)
        obj.auths = HashSet<Auth>()
        acctRepo.delete(id)
    }

    fun acctUpdatePwd(newPwd: String, id: Long) {
        val userObj = acctRepo.findOne(id)
        userObj.pwd = newPwd
        acctRepo.save(userObj)
    }


    fun acctCount(phone: String?, email: String?) = acctRepo.acctCount(phone, email)

    /**
     * 员工绑定微信用户
     * @author samy
     * @date 2020/01/03
     */
    fun acctWxLinker(openId: String, type: Int, acct: Account) = wxLinkerRepo.findByOpenIdAndTypeAndAcct(openId, type, acct)

    /**
     * 微信用户保存
     * @author samy
     * @date 2020/01/03
     */
    fun wxLinkerSave(wxLinker: WxLinker) = wxLinkerRepo.save(wxLinker)

    /**
     * 删除账户相关微信用户
     * @author samy
     * @date 2020/01/03
     */
    fun batchAcctLinkerDel(acctId: Long) = wxLinkerRepo.batchAcctLinkerDel(acctId)

    /**
     * 单个账户下微信客户
     * @author samy
     * @date 2020/01/03
     */
    fun acctWxLinkers(acctId: Long) = wxLinkerRepo.acctWxLinkers(acctId)

    fun subMenuList() = menuRepo.subMenuList()

    fun roleFindCommonList(name: String?, id: String?, pg: Pageable) = roleRepo.findCommAll(name, id, pg)

    fun roleFindById(id: Long) = roleRepo.findOne(id)

    fun updateAcctAuth(acctId: Long, ids: List<String>, authArray: Array<String>) {
        val tempAcct = acctRepo.findOne(acctId)
        val auths = HashSet<Auth>()
        val extraAuthSet = HashSet<ExtraAuth>()
        authArray.indices.map { i ->
            val temp = Auth()
            temp.fkMenu = menuRepo.findOne(ids[i].toLong())
            val content = authArray[i]
            if ("" != content) {
                content.split("|").map { s ->
                    when (s) {
                        "新增" -> temp.hasCreate = 1
                        "修改" -> temp.hasUpdate = 1
                        "删除" -> temp.hasDelete = 1
                        else -> {}
                    }
                }
            } else {
                temp.hasCreate = 0
                temp.hasDelete = 0
                temp.hasUpdate = 0
            }
            auths.add(temp)

            //特殊权限
            val menuAuthList = menuAuthRepository.findByMenuId(ids[i].toLong())
            for (menuAuth in menuAuthList){
                var extraTemp = ExtraAuth()
                extraTemp.fkMenuAuth=menuAuth
                if ("" != content&&content.contains(menuAuth.authName)) {
                    extraTemp.hasCheck=1
                } else {
                    extraTemp.hasCheck=0
                }
                extraAuthSet.add(extraTemp)
            }
        }
        tempAcct.auths = auths
        tempAcct.extraAuths = extraAuthSet
        acctRepo.save(tempAcct)
    }

    fun saveRoleAuth(role: Role, ids: List<String>, authArray: Array<String>): Boolean {
        var newObj = role
        var originAuth: List<Auth>? = null
        var originExtraAuth : List<ExtraAuth>? = null
        if (newObj.id != null) {
            newObj = roleRepo.findOne(role.id)
            newObj.name = role.name
            originAuth = newObj.auths.toList()
            originExtraAuth = newObj.extraAuths?.toList()
        }
        val auths = HashSet<Auth>()
        val extraAuthSet = HashSet<ExtraAuth>()
        authArray.indices.map { i ->
            var temp = Auth()
            if (originAuth != null) {
                val tempList = originAuth.filter { ath -> ath.fkMenu.id == ids[i].toLong() }
                if (tempList.isNotEmpty()) temp = tempList[0]
            }
            //特殊权限
            var extraTempList = ArrayList<ExtraAuth>()
            if (originExtraAuth != null&& originExtraAuth.isNotEmpty()) {
                val tempList = originExtraAuth.filter { ath -> ath.fkMenuAuth.menu.id == ids[i].toLong() }
                if (tempList.isNotEmpty()) extraTempList = tempList as ArrayList<ExtraAuth>
            }
            if (temp.id == null) temp.fkMenu = menuRepo.findOne(ids[i].toLong())
            val content = authArray[i]
            if ("" != content) {
                content.split("|").map { s ->
                    when (s) {
                        "新增" -> temp.hasCreate = 1
                        "修改" -> temp.hasUpdate = 1
                        "删除" -> temp.hasDelete = 1
                        else -> {}
                    }
                }
            } else {
                temp.hasCreate = 0
                temp.hasDelete = 0
                temp.hasUpdate = 0
            }
            auths.add(temp)

            //特殊权限
            val menuAuthList = menuAuthRepository.findByMenuId(ids[i].toLong())
            for (menuAuth in menuAuthList){
                var extraTemp = ExtraAuth()
                if (extraTempList.isNotEmpty()){
                    val tempList = extraTempList.filter { ath -> ath.fkMenuAuth.authName == menuAuth.authName }
                    if (tempList.isNotEmpty()) extraTemp = tempList[0]
                }
                if (extraTemp.id == null) {
                    extraTemp.fkMenuAuth=menuAuth
                }
                if ("" != content&&content.contains(menuAuth.authName)) {
                    extraTemp.hasCheck=1
                } else {
                    extraTemp.hasCheck=0
                }
                extraAuthSet.add(extraTemp)
            }
        }
        newObj.auths = auths
        newObj.extraAuths = extraAuthSet
        roleRepo.save(newObj)
//        if (originAuth != null) {
//            delAuth(originAuth)
//        }
        //同步该角色下的账号
        val acctList = acctRepo.findByRole(newObj.id!!)
        if (acctList.isNotEmpty()){
            acctList.map { thisAcct->{
                updateAcctAuth(thisAcct.id!!,ids,authArray)
            } }
        }

        return newObj.id!! > 0
    }

    fun roleDelte(id: Long) = roleRepo.delete(id)

    fun roleBatchUpdateStatus(status: Int, ids: List<Long>) = roleRepo.batchUpdateStatus(status, ids)

    fun roleFindAllExSuper() = roleRepo.findAllExSuper()

//    @Async
//    private fun delAuth(ids: List<Long?>?) {
//        ids!!.map { l ->
//            authRepo.delete(l)
//        }
//    }

    //erp同步部门信息
    fun erpDptSave(record: Map<String, String>) {
        val action = record["action_mark"]
        log.info(">>>erpDptSave action:$action")
        when (action) {
            "1" -> {
                val temp = dptRepo.findByName(record["name"]!!)
                if (temp == null) {
                    val dpt = Dpt()//新增
                    dpt.fkOrg = orgRepo.findOne(CrmConstants.DEFAULT_ORG_ID)//默认型云
                    handleDpt(record, dpt)
                } else {//更新
                    log.info(">>>[erp部门新增]存在该部门：${record["name"]},重新启用")
                    handleDpt(record, temp)
                }
            }
            "2" -> {
                val dpt = dptRepo.findByName(record["origin_name"]!!)
                if (dpt != null) {
                    //部门名称修改需要同步到TaskPlanning表
                    val newDptName = record["name"]
                    if (dpt.name != newDptName) {
                        val taskList = taskRepo.findByDptCode(dpt.id.toString())
                        for (task in taskList) {
                            task.dptName = newDptName
                            taskRepo.save(task)
                        }
                    }
                    handleDpt(record, dpt)//更新
                } else commUtil.errLogSave("[erp同步]当前crm没有该部门:>>>${record["origin_name"]}>>>无法更新部门")
            }
            "3" -> {
                val dpt = dptRepo.findByName(record["name"]!!)
                if (dpt != null) {
                    dpt.status = 0//删除设置为停用
                    dptRepo.save(dpt)
                    //同时需要将关联的客户/业务员赋予默认值
                    log.info("erp部门删除：${dpt.id}")
                    val cstmList = cstmRepo.findListByDptId(dpt.id!!)
                    cstmList.map { cstm ->
                        cstm.fkDpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                        cstmRepo.save(cstm)
                    }
                    val acctList = acctRepo.findListByDptId(dpt.id!!)
                    acctList.map { acct ->
                        acct.fkDpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
                        acctRepo.save(acct)
                    }
                } else commUtil.errLogSave("[erp同步]当前crm没有该部门:>>>${record["name"]}>>>无法删除部门")
            }
        }
    }

    fun handleDpt(record: Map<String, String>, dpt: Dpt) {
        dpt.name = if (record["name"] != "") record["name"]!! else ""
        dpt.leader = if (record["leader"] != "") record["leader"]!! else ""
        dpt.remark = if (record["remark"] != "") record["remark"]!! else ""
        dpt.status = if (record["status"] == "1") 0 else 1
        dptRepo.save(dpt)
        log.info(">>>save dpt successed>>>")
    }

    //erp同步机构信息
    fun erpOrgSave(record: Map<String, String>) {
        val action = record["action_mark"]
        log.info(">>>erpOrgSave action:$action")
        when (action) {
            "1" -> {
                val temp = orgRepo.findByName(record["name"]!!)
                if (temp == null) {
                    val newOrg = Organization()
                    newOrg.status = 1
                    handleOrg(record, newOrg)//新增
                } else {
                    log.info(">>>[erp机构新增]存在该机构：${record["name"]},重新启用")
                    temp.status = 1//启用
                    handleOrg(record, temp)//更新
                }
                //同时处理机构对应的往来单位
                saveCstmForOrg(record)
            }
            "2" -> {
                val org = orgRepo.findByName(record["origin_name"]!!)
                if (org != null) {
                    handleOrg(record, org)//更新
                    //同时更新机构对应的往来单位
                    val obj = customerRepo.findByCompName(record["origin_name"]!!)
                    if (obj != null) {
                        obj.compName = if (record["name"] != "") record["name"]!! else ""
                        obj.legalRept = if (record["legal_rept"] != "") record["legal_rept"]!! else ""
                        obj.compNameAb = if (record["simple_name"] != "") record["simple_name"]!! else ""
                        customerRepo.save(obj)
                    } else commUtil.errLogSave("[erp同步机构]当前crm没有该客户:>>>${record["origin_name"]}>>>无法更新机构对应的客户")
                } else commUtil.errLogSave("[erp同步]当前crm没有该机构:>>>${record["origin_name"]}>>>无法更新机构")
            }
            "3" -> {
                val org = orgRepo.findByName(record["name"]!!)
                if (org != null) {
                    org.status = 0//删除设置为停用
                    orgRepo.save(org)
                    // 同时需要将关联的部门赋予默认值
                    log.info("erp机构删除：${org.id}")
                    val list = dptRepo.findListByOrgId(org.id!!)
                    list.map { dpt ->
                        dpt.fkOrg = orgRepo.findOne(CrmConstants.DEFAULT_ORG_ID)
                        dptRepo.save(dpt)
                    }
                    //同时机构对应的往来单位需要删除
                    val obj = customerRepo.findByCompName(record["name"]!!)
                    if (obj != null) {
                        obj.status = 2
                        obj.erpCode = ""
                        customerRepo.save(obj)
                    } else commUtil.errLogSave("[erp同步机构]当前crm没有该客户:>>>${record["origin_name"]}>>>无法删除机构对应的客户")
                } else commUtil.errLogSave("[erp同步]当前crm没有该机构:>>>${record["name"]}>>>无法删除机构")
            }
        }
    }

    fun handleOrg(record: Map<String, String>, org: Organization) {
        org.name = if (record["name"] != "") record["name"]!! else ""
        org.legalRept = if (record["legal_rept"] != "") record["legal_rept"]!! else ""
        org.remark = if (record["remark"] != "") record["remark"]!! else ""
        org.simpleName = if (record["simple_name"] != "") record["simple_name"]!! else ""
        orgRepo.save(org)
        log.info(">>>save org successed>>>")
    }

    //erp同步账户
    fun erpAcctSave(record: Map<String, String>) {
        val action = record["action_mark"]
        log.info(">>>erpAcctSave action:$action")
        if (action != "3") {
            val temp = acctRepo.findByPlatformCode(record["employee_code"]!!)
            if (temp == null) {
                log.info("当前crm没有该业务员编码:>>>${record["employee_code"]}>>>新增业务员")
                val acct = Account()
                acct.platformCode = record["employee_code"]//erp自动生成
                acct.loginAcct = record["name"]!!//erp必填
                acct.customers = HashSet<Customer>()
                var auths = HashSet<Auth>()
                val role = roleRepo.findOne(CrmConstants.DEFAULT_ROLE_ID)//默认业务员
                role.auths.map { ah ->
                    auths.add(ah)
                }
                acct.fkRole = role
                acct.auths = auths
                handleAcct(record, acct)
            } else {
                log.info("当前crm存在该业务员编码:>>>${record["employee_code"]}>>>更新业务员")
                //业务员名称修改需要同步到TaskPlanning表
                val newAcctName = record["name"]
                if (temp.name != newAcctName) {
                    val taskList = taskRepo.findByAcctCode(record["employee_code"]!!)
                    for (task in taskList) {
                        task.acctName = newAcctName
                        taskRepo.save(task)
                    }
                }
                handleAcct(record, temp) //更新
            }
        } else {
            val acct = acctRepo.findByPlatformCode(record["employee_code"]!!)
            if (acct != null) {
                acct.status = 0//停用
                acct.platformCode = ""
                acctRepo.save(acct)
                //同时需要将关联的客户赋予默认值
                log.info("erp业务员删除：${acct.id}")
                val list = cstmRepo.findListByAcctId(acct.id!!)
                list.map { cstm ->
                    cstm.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
                    cstmRepo.save(cstm)
                }
            } else commUtil.errLogSave("[erp同步]当前crm没有该业务员编码:>>>${record["employee_code"]}>>>无法删除业务员")
        }
    }

    fun handleAcct(record: Map<String, String>, acct: Account) {
        val excludeArr = arrayOf("mq_type", "action_mark", "origin_name", "sex", "status", "dpt_name", "employee_code")
        record.keys.map { key ->
            if ("sex" == key) acct.sex = if (record[key] == "") 1 else (if (record[key] == "男") 1 else 2)
            else if ("status" == key) acct.status = if (record[key] == "") 1 else (if (record[key] == "1") 0 else 1)
            else if ("dpt_name" == key) acct.fkDpt = dptRepo.findByName(record[key]!!)!!//erp必填
            else if ("in_time" == key) acct.inTime = if (record[key].isNullOrBlank()) "" else record[key]
            else if (!excludeArr.contains(key)) {
                val methodName = commUtil.getColumnMethodName(key)
                val m = acct.javaClass.getMethod("set$methodName", String::class.java)
                m.invoke(acct, if (record[key] == "") " " else record[key])
            }
        }
        acctRepo.save(acct)
        log.info(">>>save acct successed>>>")
    }

    //任务计划保存
    fun taskSave(task: TaskPlanning) = taskRepo.save(task)

    //分页查询任务计划
    fun taskList(year: String, month: String, type: String, orgName: String?, dptName: String?, acctName: String?, pg: Pageable) = taskRepo.findAll(year.toInt(), month.toInt(), type.toInt(), orgName, dptName, acctName, pg)

    //任务计划新增和更新
    fun taskCreateOrUpdate(task: TaskPlanning): TaskPlanning {
        var obj = task
        //有id就更新，没有新增
        if (obj.id != null) {
            val originObj = taskRepo.findOne(obj.id)
            val newObj = commUtil.autoSetClass(task, originObj, arrayOf("id", "createAt", "updateAt")) as TaskPlanning
            obj = taskRepo.save(newObj)
        } else {
            obj = taskRepo.save(task)
        }
        return obj
    }

    //查重
    fun taskCountRepeats(year: Int, month: Int, type: Int, compName: String?, orgName: String?, dptName: String?, acctCode: String?) = taskRepo.taskCountRepeats(year, month, type, compName, orgName, dptName, acctCode)

    //查询单个任务计划
    fun findTask(id: Long) = taskRepo.findOne(id)

    //删除任务计划
    fun taskDel(id: Long) = taskRepo.delete(id)

    //查询父分级
    fun findParentGrade(parentId: Long) = gradeRepo.findOne(parentId)

    //查询父下所有分级
    fun findGradeList(parentId: Long): List<GradeCoefficient> {
        val sort = Sort(Sort.Direction.ASC, "nameOrder")
        return gradeRepo.findAll(parentId, sort)
    }

    //分级新增和修改
    fun gradeCreateOrUpdate(type: String, parentId: Long, grades: Array<String>, name: String?) {
        log.info("start create grade>>>grades.size:${grades.size}>>>type:$type>>>parenrId:$parentId")
        if (parentId == CrmConstants.GRADE_COEFFICIENT_FORMULA) { //系数公式单独处理
            val obj = gradeRepo.findOne(parentId)
            obj.equationName = grades[0]
            obj.name = name!!
            gradeRepo.save(obj)
        } else if (type == "1") { //新增
            if (parentId == CrmConstants.GRADE_COEFFICIENT_DEBT) { //欠款得分需要先删除
                gradeRepo.deleteDebtGrade(parentId)
            }
            grades.map { str ->
                val arr = str.split("|")//name|coefficient|order
                log.info("gradeName:>>>${arr[0]}>>>gradeCoefficient:>>>${arr[1]}>>>nameOrder:>>>${arr[2]}")
                var grade = GradeCoefficient()
                grade.name = arr[0]
                grade.coefficient = arr[1].toDouble()
                grade.parentId = parentId
                grade.nameOrder = arr[2].toInt()
                gradeRepo.save(grade)
            }
        } else { //修改
            grades.map { str ->
                val arr = str.split("|")//id|name|coefficient
                log.info("gradeId:>>>${arr[0]}>>>gradeName:>>>${arr[1]}>>>gradeCoefficient:>>>${arr[2]}")
                val obj = gradeRepo.findOne(arr[0].toLong())
                if (obj != null) {
                    obj.name = arr[1]
                    obj.coefficient = arr[2].toDouble()
                    gradeRepo.save(obj)
                }
            }
        }
        log.info("create grade successed>>>")
    }

    //删除任务计划
    fun gradeDel(id: Long) = gradeRepo.delete(id)

    fun findParentList() = gradeRepo.findParentList()

    fun queryErrLog(content: String?, startTime: String?, endTime: String?, pg: Pageable) = errLogRepo.findAll(content, startTime, endTime, pg)

    //查询消息队列
    fun queryMqMsg(content: String?, startTime: String?, endTime: String?, msgType: String?, pg: Pageable) = mqDataRepo.findAll(content, startTime, endTime, msgType, pg)

    //重新发送
    fun mqResend(id: Long): String {
        var msg = ""
        val obj = mqDataRepo.findOne(id)
        val map = JSON.parseObject(obj.content, HashMap::class.java) as HashMap<String, String>
        if (obj.msgType == 1) mqSenderService.reSendMsg(map, obj.mqType, id, obj.destination)//发送erp或xy
        else msg = "编号为${id}的消息不能重发"
        return msg
    }

    //保存登录信息
    fun loginMsgSave(loginMsg: LoginMsg) = loginMsgRepository.save(loginMsg)

    //分页查询登录信息
    fun loginMsgList(acctName: String?, startTime: String?, endTime: String?, pageable: Pageable) = loginMsgRepository.findLoginMsgList(acctName, startTime, endTime, pageable)

    private fun saveCstmForOrg(record: Map<String, String>) {
        val newCstm = Customer()
        //不处理的字段
        val excludeArr = arrayOf("mq_type", "action_mark", "name", "origin_name", "remark", "simple_name", "busi_relation", "dpt_name", "acct_erp_code")
        record.keys.map { key ->
            if ("busi_relation" == key) {
                log.info("colName is busi_relation, value is ${record[key]}")
                //业务关系
                val arr = record[key].toString().split(",")
                val busiRelation = HashSet<BusiRelation>()
                arr.map { name ->
                    busiRelation.add(busiRelationRepo.findByName(name))
                }
                newCstm.busiRelation = busiRelation
            } else if ("dpt_name" == key) {
                log.info("colName is dpt_name, value is ${record[key]}")
                //业务部门
                if (record[key] != "") {
                    val dpt = dptRepo.findByName(record[key]!!)
                    newCstm.fkDpt = dpt!!
                } else newCstm.fkDpt = dptRepo.findOne(CrmConstants.DEFAULT_DPT_ID)
            } else if ("acct_erp_code" == key) {
                log.info("colName is acct, value is ${record[key]}")
                //业务员
                if (record[key].isNullOrBlank()) {
                    newCstm.fkAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
                } else {
                    val acct = acctRepo.findByPlatformCode(record[key]!!)
                    newCstm.fkAcct = acct!!
                }
            } else if (!excludeArr.contains(key)) {
                val methodName = commUtil.getColumnMethodName(key)
                val m = newCstm.javaClass.getMethod("set$methodName", String::class.java)
                m.invoke(newCstm, if (record[key].isNullOrBlank()) "" else record[key])
                log.info("colName is $key, value is ${record[key]}")
            }
        }
        //保存
        val linkerSet = HashSet<Linker>()
        linkerSet.add(linkRepo.findOne(CrmConstants.DEFAULT_LINKER_ID)!!)
        newCstm.linkers = linkerSet
        newCstm.fkCustomProperty = customPropertyRepo.findByName(CrmConstants.DEFAULT_CUSTOM_PROPERTY)
        newCstm.createAcct = acctRepo.findOne(CrmConstants.DEFAULT_ACCT_ID)
        newCstm.procurementGoods = HashSet<SupplyCatalog>()
        newCstm.procurementPurpose = HashSet<Purpose>()
        newCstm.hopeAddGoods = HashSet<SupplyCatalog>()
        newCstm.dealGoods = HashSet<SupplyCatalog>()
        newCstm.dealPurpose = HashSet<Purpose>()
        newCstm.processRequirement = HashSet<ProcessRequirement>()
        newCstm.industry = HashSet<Industry>()
        newCstm.customerSource = "ERP"
        newCstm.registerCapital = "0"
        //erp过来的就是正式客户
        newCstm.mark = 2
        newCstm.publicCompName = newCstm.compName //默认和客户名称一致
        newCstm.convertDate = Timestamp(Date().time) //转化时间
        newCstm.billDate = Timestamp(Date().time) //开单日期
        customerRepo.save(newCstm)
        //保存银行信息
        val bankInfo = BankInfo()
        bankInfo.fkCstm = newCstm
        bkIfRepo.save(bankInfo)
        log.info(">>>>save Cstm for Org successed>>>>")
    }

    // 新增字典
    fun saveDict(dict: Dictionary) = dictRepo.save(dict)

    // 查询期货字典
    fun findSinaDict() = dictRepo.findSinaDict()

    /**
     * 地址的增删改
     * @author samy
     * @date 2020/06/06
     */
    fun addressSaveOrUpdate(address: Address) {
        addressRepo.save(address)
    }

    fun addressFindByCode(code: String) = addressRepo.findByCode(code)

    fun addressFindAllPg(type: String?, name: String?, pageable: Pageable) = addressRepo.findAllPg(type, name, pageable)

    fun addressFindByCityAllPg(name: String?, pageable: Pageable) = addressRepo.findByCityAllPg(name, pageable)

    fun findExtraAuthByMenuId(menuId: Long) = extraAuthRepository.findByMenuId(menuId)
}