package org.zhd.crm.server

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner
import org.zhd.crm.server.model.crm.Dictionary
import org.zhd.crm.server.model.crm.Linker
import org.zhd.crm.server.repository.crm.*
import org.zhd.crm.server.repository.statistic.SalesmanGoodsRepository
import org.zhd.crm.server.repository.statistic.SalesmanHighSellRepository
import org.zhd.crm.server.service.ActiveMqRecieverService
import org.zhd.crm.server.service.ActiveMqSenderService
import org.zhd.crm.server.service.ScheduleService
import org.zhd.crm.server.service.crm.CustomerService
import org.zhd.crm.server.service.crm.SettingService
import org.zhd.crm.server.util.CommUtil
import javax.jms.Queue

@RunWith(SpringRunner::class)
@SpringBootTest
class CstmServiceTest {
    @Autowired
    private lateinit var customerService: CustomerService
    @Autowired
    private lateinit var settingService: SettingService
    @Autowired
    private lateinit var scheduleService: ScheduleService
    @Autowired
    private lateinit var highSaleRepo: SalesmanHighSellRepository
    @Autowired
    private lateinit var cstmRepo: CustomerRepository
    @Autowired
    private lateinit var cstmRedPepo: CustomerRecordRepository
    @Autowired
    private lateinit var saleGoodsRepo: SalesmanGoodsRepository
    @Autowired
    private lateinit var bankInfoRepository: BankInfoRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var mqSenderService: ActiveMqSenderService
    @Autowired
    private lateinit var mqRcvService: ActiveMqRecieverService
    @Autowired
    private lateinit var dictRepo: DictionaryRepository
    @Autowired
    private lateinit var linkerRepository: LinkerRepository
    @Autowired
    private lateinit var customerRepo: CustomerRepository
    @Autowired
    private lateinit var xyCustomChangeQueue: Queue

    private val log = LoggerFactory.getLogger(CstmServiceTest::class.java)
    private val uid: Long = 1
    private val prefix = "Dev"

    @Test
    fun cstmRegionList(){ //分页查询地区
//        val pg = PageRequest(0, 8)
//
//        val page = customerService.findRegionList(null,pg)
//        log.info(">>>total:${page.totalElements}")
//        log.info(">>>list:"+toJsonString("region",page.content))
        val resp = linkerRepository.findLinkAndCstmRelation()
        log.info(">>>${JSON.toJSONString(linkerRepository)}")
    }

    @Test
    fun testBehavior(){
        //json转map
        val jsonStr= "{\"standard\":\"10#\",\"goods_name\":\"槽钢\",\"weight_range\":\"\",\"user_name\":\"常州兴铁金属材料有限公司\",\"ip\":\"222.185.186.180\",\"length\":\"6.0\",\"tolerance_range\":\"\",\"source\":\"电脑端\",\"warehouse\":\"6号门\",\"supply\":\"马钢\",\"ip_location\":\"中国 江苏省 常州市\",\"material\":\"Q235B\",\"mq_type\":\"1\",\"time\":\"1550128542000\",\"event\":\"从购物车删除\",\"order_id\":\"88355\",\"user\":\"C0004198\",\"event_en\":\"del_cart\"}"
        val jsonMap = JSON.parseObject(jsonStr, Map::class.java) as Map<String, String>
        customerService.behaviorRecordSave(jsonMap)
    }

    @Test
    fun customerList(){ //分页查询客户
        val mark: Int = 1 //1潜在客户，2 正式客户，3 公共客户
        val pg = PageRequest(0, 8, Sort.Direction.DESC, "update_at")

        //compName?, linkName?, linkPhone?, startTime?, endTime?, dptName?, acctName?, mark: Int, uid: Long, pg: Pageable
//        val page = customerService.findCustomerList(null,null,null,null,null,null,null,mark,uid,pg)
//        log.info(">>>total:${page.totalElements}")
//        log.info(">>>list:"+toJsonString("customer",page.content))
    }

    @Test
    fun linkerCreate(){ //联系人创建和更新
        val newCstmId: Long = 6057
        val originCstmId: String? = null
        val link = Linker()
        link.name = "陈帅帅"
        link.phone = "123"
        link.id = 76319 //测试更新
        link.creator = settingService.acctFindById(uid)

        customerService.linkerSave(uid,link,newCstmId,originCstmId)
        log.info(">>>save linker successed")
    }

    @Test
    fun customerTransform(){ //潜在客户转为正式客户
        val cstmId: Long = 162427 //潜在客户

        val msg = customerService.customerTransform(cstmId, uid)
        log.info(">>>msg:$msg")
    }

    @Test
    fun mobInfoCreate(){
        val acctId = 1L
        val deviceNum = "wqedqwdsad"
        val obj = customerService.mobInfoCreate(acctId, deviceNum, 0)
        if (obj.id!! > 0) log.info(">>>create successed")
    }

    @Test
    fun logOut(){
        val deviceNum = "wqedqwdsad"
        val msg = customerService.mobLogOut(deviceNum, 0)
        if (msg == "") log.info(">>>logOut successed")
    }

    @Test
    fun testSchedule1(){
        scheduleService.handleMobileMessage()
    }

    @Test
    fun testSchedule2(){
        scheduleService.cstmLost()
    }

    @Test
    fun testSchedule3(){
        scheduleService.deleteMobileInfo()
    }

    @Test
    fun testSchedule4(){
        scheduleService.batchUpdateCstmInitial()
    }

    @Test
    fun testSchedule5(){
        scheduleService.cstmGradePro()
    }

    @Test
    fun testSchedule6(){
        scheduleService.cstmCallTimeout()
    }

    private fun toJsonString(name: String, list: List<Any>): String{
        val json = JSONArray()
        list.map { s->
            val jo = JSONObject()
            jo.put(name,s)
            json.add(jo)
        }
        return json.toString()
    }

    @Test
    fun getSpell(){
        val initial = commUtil.getFirstSpell("长兴鸿鹄")
        val initial1 = commUtil.getFirstSpell("长沙")
        log.info(">>>>$initial,$initial1")
    }

    private fun saveDict(name: String,value:String,remark:String?){
        val dict = Dictionary()
        dict.name = name
        dict.value = value
        if (remark != null) dict.remark = remark
        dictRepo.save(dict)
    }
    @Test
    fun getAddr(){
        val prov = commUtil.handleXYCompProv("广西壮族")
        val prov1 = commUtil.handleXYCompProv("广西")
        val prov2 = commUtil.handleXYCompProv("香港")
        val prov3 = commUtil.handleXYCompProv("上海")
        val prov4 = commUtil.handleXYCompProv("江苏")
        val prov5 = commUtil.handleXYCompProv("")
        val city = commUtil.handleXYCompCity("常州")
        val city1 = commUtil.handleXYCompCity("常州自治州")
        val city2 = commUtil.handleXYCompCity("")
        log.info(">>>>$prov,$prov1,$prov2,$prov3,$prov4,$prov5")
        log.info(">>>>$city,$city1,$city2")
        val region = commUtil.handleRegion("常州市")
        val region1 = commUtil.handleRegion("常州")
        val region2 = commUtil.handleRegion(" ")
        log.info(">>>>$region,$region1,$region2")
    }

    private fun spell(name: String){
        log.info(">>>$name")
        val arr: CharArray = name.toCharArray()
        var str = ""
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.UPPERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        if (commUtil.codeType(arr[0]) == 1) {
            val pyArr = PinyinHelper.toHanyuPinyinStringArray(arr[0], format)
            if (pyArr.size == 1) {
                str = pyArr[0].toCharArray().get(0).toString()
            } else {//多音字查字典匹配
                if (commUtil.judgeSame(pyArr)){
                    log.info("多音字相同首字母>>>${name.substring(0, 1)}")
                } else {
                    log.info("多音字不同首字母>>>${name.substring(0, 1)}")
                }
            }
        }
    }

    @Test
    fun cstmSpell(){
        val list = cstmRepo.findAll()
        list.map { s ->
            spell(s.compName)
        }
    }

    @Test
    fun testFind(){
        val name = "大帅比"
        val queue = xyCustomChangeQueue.queueName
        log.info("名称：$queue")
        val tempCstm = customerRepo.findByCompName(name)
        if (tempCstm != null) {
            log.info("存在线下客户:>>>$name>>>开始更新")
        } else {
            log.info("不存在客户:>>>$name>>>>开始新增")
        }
        val list = ArrayList<String>()
        list.add("ada")
//        list.add("d")
//        list.add("gg")
        val busiRelation = list.joinToString(separator = ",")
        log.info("业务关系：$busiRelation")
    }

    @Test
    fun testMq(){
        val resp = HashMap<String, String>()
        val destination = "${prefix}_xy_2_crm_custm_info"
        val mqType = "2"
        val type = if (mqType == "1") 1 else if (mqType == "2") 4 else if (mqType == "3") 2 else if (mqType == "4") 3 else 0
//        mqRcvService.saveMqData(resp,destination,type)
    }
}