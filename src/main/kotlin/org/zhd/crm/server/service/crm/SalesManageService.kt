package org.zhd.crm.server.service.crm

import com.alibaba.fastjson.JSON
import com.xyscm.erp.crm.api.dto.GoodStockDto
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.zhd.crm.server.dubbo.GoodStockDubboService
import org.zhd.crm.server.model.crm.SinaFutures
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.repository.crm.SinaFuturesRepository
import org.zhd.crm.server.repository.statistic.GoodStockRepository
import org.zhd.crm.server.repository.statistic.GoodsSalesRepository
import org.zhd.crm.server.service.HttpService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
@Transactional
class SalesManageService {
    @Autowired
    private lateinit var goodsSalesRepo: GoodsSalesRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @PersistenceContext
    private lateinit var entityManager: EntityManager //实体管理对象
    @Autowired
    private lateinit var stockRepo: GoodStockRepository
    @Autowired(required = false)
    private lateinit var goodStockDubboService: GoodStockDubboService
    @Autowired
    private lateinit var httpService: HttpService
    @Autowired
    private lateinit var sinaRepo: SinaFuturesRepository

    @Value("\${spring.profiles.active}")
    private var currentPro = ""
    private val log = LoggerFactory.getLogger(SalesManageService::class.java)

    fun findDemissionStatList(compName: String?, acctName: String?, dptName: String?, uid: String, pageable: Pageable, sort: Int): Page<Any> {
        val uAcct = acctRepo.findOne(uid.toLong())
        val id: String? = if ("业务员" == uAcct.dataLevel) uAcct.id.toString() else null
        val dptId: String? = if ("部门" == uAcct.dataLevel) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构" == uAcct.dataLevel) uAcct.fkDpt.fkOrg.id.toString() else null
        if (sort == 0) return goodsSalesRepo.findFirstSortStat(compName, acctName, dptName, id, dptId, orgId, pageable)
        else if (sort == 1) return goodsSalesRepo.findSecondSortStat(compName, acctName, dptName, id, dptId, orgId, pageable)
        else if (sort == 2) return goodsSalesRepo.findThirdSortStat(compName, acctName, dptName, id, dptId, orgId, pageable)
        else return goodsSalesRepo.findFourthSortStat(compName, acctName, dptName, id, dptId, orgId, pageable)
    }

    fun findDemissionList(startTime: String?, endTime: String?, compName: String, acctName: String, dptName: String, pageable: Pageable, sort: Int): Page<Any> {
        log.info(">>>$startTime,$endTime")
        if (sort == 0) return goodsSalesRepo.findFirstSortDetails(startTime, endTime, compName, acctName, dptName, pageable)
        else if (sort == 1) return goodsSalesRepo.findSecondSortDetails(startTime, endTime, compName, acctName, dptName, pageable)
        else if (sort == 2) return goodsSalesRepo.findThirdSortDetails(startTime, endTime, compName, acctName, dptName, pageable)
        else if (sort == 3) return goodsSalesRepo.findFourthSortDetails(startTime, endTime, compName, acctName, dptName, pageable)
        else return goodsSalesRepo.findFifthSortDetails(startTime, endTime, compName, acctName, dptName, pageable)
    }

    fun handleLineGraph(list: List<Any>, result: HashMap<String, Any>): HashMap<String, Any> {
        val salesList = ArrayList<String>()
        val gmList = ArrayList<String>()
        var dateList = ArrayList<String>()
        val dataMap = HashMap<String, Array<Any>>()
        for (data in list) {
            val arr = data as Array<Any>
            dataMap[arr[0].toString()] = arr
        }
        log.info("size:${dataMap.size}")
        if (dataMap.size != 0){
            val sorted = dataMap.toSortedMap()
            val keys = sorted.keys
            val startTime = keys.first()
            val endTime = keys.last()
            log.info("首位时间为：$startTime,$endTime")
            dateList = commUtil.getMonthRange(startTime, endTime) as ArrayList<String>
            dateList.map { date ->
                val key = keys.filter { str -> str == date }
                log.info("年月为：${key.toString()}")
                if (key.isEmpty()) {
                    salesList.add("0")
                    gmList.add("0")
                } else {
                    val arr = dataMap[key[0].toString()] as Array<Any>
                    salesList.add(arr[4].toString())
                    gmList.add(arr[5].toString())
                }
            }
        }
        result["dateList"] = dateList
        result["salesList"] = salesList
        result["gmList"] = gmList
        return result
    }

    // 根据物资批号查询唯一物资
    fun findUniqueGoods(sumGoodsBatch: String) = stockRepo.findBySumGoodsBatch(sumGoodsBatch)

    //poi生成库存报价单excel到项目resources/temp中,返回下载链接
    fun exportStockReport(req: HttpServletRequest, excelName: String): String {
        val content = req.getParameter("content")
        val addr = if (currentPro == "prod") "${CrmConstants.CRM_BACKGROUND_ADDR}${req.contextPath}" else "${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"
        log.info(">>>$addr")
        val url = "$addr/file/download/3"
        val encodeUrl = URLEncoder.encode(url, "utf-8")
        log.info(">>>start export")
        //处理JSON数据 "[{\"partsnameName\": \"花纹卷\",\"goodsMaterial\": \"Q235\",\"goodsSpec\": \"4.5*1250\",\"goodsProperty1\": \"350\",\"productareaName\": \"本钢\",\"warehouseCode\": \"000045\",\"warehouseName\": \"开平厂\",\"sortMark\": null,\"goodsProperty4\": null,\"goodsProperty5\": null,\"sumgoodsBatch\": \"GS201701010090\",\"ajuPricesetMakeprice\": 0,\"pricesetMakeprice\": 0,\"ajuPricesetPrefprice\": 0,\"pricesetPrefprice\": 0,\"goodsMetering\": \"磅计\",\"goodsAssistnum\": 1,\"goodsNum\": 1,\"goodsWeight\": 19.64,\"goodsAssistweight\": 16.902,\"goodsSupplynum\": 0,\"goodsSupplyweight\": 0,\"goodsSupplyassistweight\": 0,\"datasDetailStr1\": \"-0.1620\"},{\"partsnameName\": \"花纹卷\",\"goodsMaterial\": \"Q235\",\"goodsSpec\": \"4.5*1250\",\"goodsProperty1\": \"350\",\"productareaName\": \"本钢\",\"warehouseCode\": \"000055\",\"warehouseName\": \"卷板库\",\"sortMark\": null,\"goodsProperty4\": null,\"goodsProperty5\": null,\"sumgoodsBatch\": \"GS201708250036\",\"ajuPricesetMakeprice\": 4060,\"pricesetMakeprice\": 4060,\"ajuPricesetPrefprice\": 20,\"pricesetPrefprice\": 10,\"goodsMetering\": \"磅计\",\"goodsAssistnum\": 1,\"goodsNum\": 2,\"goodsWeight\": 39.38,\"goodsAssistweight\": 33.804,\"goodsSupplynum\": 0,\"goodsSupplyweight\": 0,\"goodsSupplyassistweight\": 0,\"datasDetailStr1\": \"-0.1626\"}]"
        val stockList = JSON.parseArray(content, GoodStockDto::class.java)
        //创建
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("现货物资报价单")
        log.info(">>>stockList size:${stockList.size}")
        var rowNum = 1
        val headers = arrayOf("磅计销售价格", "理计销售价格", "计量方式", "品名", "材质", "规格", "长度", "产地", "仓库", "公差范围", "重量范围", "支件数")
        val stockMap: Map<String, String> = mapOf("磅计销售价格" to "PricesetMakeprice", "理计销售价格" to "AjuPricesetMakeprice", "计量方式" to "GoodsMetering", "品名" to "PartsnameName", "材质" to "GoodsMaterial", "规格" to "GoodsSpec", "长度" to "GoodsProperty1", "产地" to "ProductareaName", "仓库" to "WarehouseName", "公差范围" to "GoodsProperty5", "重量范围" to "GoodsProperty4", "支件数" to "GoodsAssistnum")
        val headerRow = sheet.createRow(0)
        headers.indices.map { i ->
            val cell = headerRow.createCell(i)
            val text = HSSFRichTextString(headers[i])
            cell.setCellValue(text)
        }
        stockList.map { stock ->
            val stockRow = sheet.createRow(rowNum)
            log.info(">>>创建excel第${rowNum}行数据")
            (0..(headers.size - 1)).map { idx ->
                val fieldName = stockMap[headers[idx]]
                val m = stock.javaClass.getMethod("get$fieldName")
                val value = if (m.invoke(stock) == null) "" else m.invoke(stock)
                log.info(">>>value:$value")
                stockRow.createCell(idx).setCellValue(value.toString())
            }
            rowNum++
        }
        try {
            val file = ResourceUtils.getFile("classpath:temp/$excelName")
            if (!file.exists()) {
                log.info(">>>$excelName,不存在")
            }
            val fos = FileOutputStream(file)
            workbook.write(fos)
            fos.close()
            log.info(">>>end export")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        log.info(">>>encodeUrl:$encodeUrl")
        return encodeUrl
    }

    //获取库存销售汇总
    fun findStockSalesList(result: HashMap<String, Any>, partsName: String?, material: String?, goodsSpec: String?, length: String?, toleranceRange: String?, weightRange: String?, uid: String, sort: String, currentPage: Int, pageSize: Int): HashMap<String, Any> {
        val pg = PageRequest(currentPage, pageSize)
        val uAcct = acctRepo.findOne(uid.toLong())
        log.info(">>>数据权限:${uAcct.dataLevel}")
        val id: String? = if ("业务员" == uAcct.dataLevel) uAcct.id.toString() else null
        val dptId: String? = if ("部门" == uAcct.dataLevel) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构" == uAcct.dataLevel) uAcct.fkDpt.fkOrg.id.toString() else null
        val page = if (sort == "0") goodsSalesRepo.findFirstSortSales(id, dptId, orgId, partsName, material, goodsSpec, length, toleranceRange, weightRange, pg) else goodsSalesRepo.findSecondSortSales(id, dptId, orgId, partsName, material, goodsSpec, length, toleranceRange, weightRange, pg)
        handleStockList(result, page, currentPage, pageSize)
        return result
    }

    //获取并组合可卖库存量，可卖数量
    fun handleStockList(result: HashMap<String, Any>, page: Page<Any>, currentPage: Int, pageSize: Int) {
        val saleList = page.content
        val total = page.totalElements
        val resultList = ArrayList<Array<String>>()
        if (total == 0L){
            log.info(">>>return")
            result["list"] = resultList
            result["total"] = total
            result["returnCode"] = 0
            return
        }
        val batchList = ArrayList<String>()
        saleList.map { sl ->
            val arr = sl as Array<Any>
            batchList.add(sl[0].toString())
        }
        val batchStr = batchList.joinToString(separator = ",")
        log.info(">>>total:$total,batchStr:$batchStr")
        val goodStockDto = GoodStockDto()
        goodStockDto.sumgoodsBatchStr = batchStr
        val goodStockList = goodStockDubboService.queryGoodStock(currentPage, pageSize, goodStockDto)
        val supplyWeightMap = HashMap<String, GoodStockDto>()
        goodStockList.map { goodStock ->
            supplyWeightMap[goodStock.sumgoodsBatch] = goodStock
        }
        log.info(">>>size:${saleList.size},${goodStockList.size},${supplyWeightMap.size}")
        (0..(saleList.size -1)).map { idx ->
            val resultArr = Array(11, { "" })
            val array = saleList[idx] as Array<Any>
            (0..8).map { ix ->
                val item = array[ix]
                resultArr[ix] = if (item == null) "" else item.toString()
            }
            val goodsBatch = batchList[idx]
            //可卖库存量，可卖数量
            resultArr[9] = if (supplyWeightMap[goodsBatch] != null) (supplyWeightMap[goodsBatch] as GoodStockDto).goodsSupplyweight.toString() else "0.0"
            resultArr[10] = if (supplyWeightMap[goodsBatch] != null) (supplyWeightMap[goodsBatch] as GoodStockDto).goodsSupplynum.toString() else "0"
            log.info(">>>current goodsBatch:$goodsBatch,supplyWeight:${resultArr[9]},supplyNum:${resultArr[10]}")
            resultList.add(resultArr)
        }
        result["total"] = total
        result["list"] = resultList
    }

    //entityManager的原生查询统一写在最后
    //获取销售预警信息 使用entityManager处理复杂（多条件）查询
    fun findSalesAlertByPage(currentPage: Int, pageSize: Int, compName: String?, acctName: String?, dptName: String?, uid: String, sort: Int, percent: String?, warningType: String?, result: HashMap<String, Any>): HashMap<String, Any> {
        //预处理
        val uAcct = acctRepo.findOne(uid.toLong())
        log.info(">>>数据权限:${uAcct.dataLevel}")
        val id: String? = if ("业务员" == uAcct.dataLevel) uAcct.id.toString() else null
        val dptId: String? = if ("部门" == uAcct.dataLevel) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构" == uAcct.dataLevel) uAcct.fkDpt.fkOrg.id.toString() else null
        val firstTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getMonthStartTime(0))
        val secondTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getMonthEndTime(0))
        val thirdTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getMonthStartTime(-1))
        val fourthTime = SimpleDateFormat("yyyy-MM-dd").format(commUtil.getMonthEndTime(-1))
        //主体
        val compNameStr = if (compName == null) "" else " and a.comp_name like '%$compName%'"
        val acctNameStr = if (acctName == null) "" else " and d.name like '%$acctName%'"
        val dptNameStr = if (dptName == null) "" else " and e.name like '%$dptName%'"
        val dataLevelStr = if (id != null) " and to_char(d.id) = '$id'" else if (dptId != null) " and to_char(e.id) = '$dptId'" else if (orgId != null) " and to_char(f.id) = '$orgId'" else ""
        val warningStr = if (warningType == null) "" else if (warningType == "0") " and decode(COALESCE(bb.sum1, 0.0),0,0,COALESCE(bb.sum1, 0.0))/decode(COALESCE(cc.sum2, 0.0),0,1,COALESCE(cc.sum2, 0.0)) >= ${percent!!.toDouble()}" else " and decode(COALESCE(bb.sum1, 0.0),0,0,COALESCE(bb.sum1, 0.0))/decode(COALESCE(cc.sum2, 0.0),0,1,COALESCE(cc.sum2, 0.0)) < ${percent!!.toDouble()}"
        val mainStr = " from t_customer a left join (select b.member_code as code1, sum(b.weight) as sum1 from crm_goods_sales@CRM_CRMSTAT b where to_char(b.deal_date, 'yyyy-MM-dd') >= '$firstTime' and to_char(b.deal_date, 'yyyy-MM-dd') <= '$secondTime' group by b.member_code) bb on bb.code1 = a.erp_code left join (select c.member_code as code2, sum(c.weight) as sum2 from crm_goods_sales@CRM_CRMSTAT c where to_char(c.deal_date, 'yyyy-MM-dd') >= '$thirdTime' and to_char(c.deal_date, 'yyyy-MM-dd') <= '$fourthTime' group by c.member_code) cc on cc.code2 = a.erp_code left join t_account d on a.fk_acct_id = d.id left join t_dpt e on a.fk_dpt_id = e.id left join t_organization f on e.fk_org_id = f.id where a.mark = 2 and a.status = 1 and a.erp_code is not null$compNameStr$acctNameStr$dptNameStr$dataLevelStr$warningStr"
        val sortStr = if (sort == 0) " order by last_month_weight desc" else if (sort == 1) " order by last_month_weight asc" else if (sort == 2) " order by this_month_weight desc" else " order by this_month_weight asc"
        //计数
        val countSql = "select count(*)$mainStr"
        log.info("countSql>>>$countSql")
        //分页
        val queryStr = "select a.comp_name,d.name as acct_name,e.name as dpt_name,a.erp_code,COALESCE(bb.sum1, 0.0) as this_month_weight,COALESCE(cc.sum2, 0.0) as last_month_weight$mainStr$sortStr"
        val querySql = commUtil.selectPageSql(currentPage, pageSize, queryStr)
        log.info("querySql>>>$querySql")
        //原生查询
        val countQuery = this.entityManager.createNativeQuery(countSql)
        val count = countQuery.resultList
        val total = count[0].toString().toInt()
        val query = this.entityManager.createNativeQuery(querySql)
        val queryList = query.resultList as List<Any>
        result["list"] = queryList
        result["total"] = total
        return result
    }

    //单条商品客户购买汇总
    fun findCstmSaleList(result: HashMap<String, Any>, sort: String, uid: String, sumgoodsBatch: String, compName: String?, dptName: String?, acctName: String?, currentPage: Int, pageSize: Int): HashMap<String, Any> {
        //预处理
        val uAcct = acctRepo.findOne(uid.toLong())
        log.info(">>>数据权限:${uAcct.dataLevel}")
        val id: String? = if ("业务员" == uAcct.dataLevel) uAcct.id.toString() else null
        val dptId: String? = if ("部门" == uAcct.dataLevel) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构" == uAcct.dataLevel) uAcct.fkDpt.fkOrg.id.toString() else null
        //主体
        val dataLevelStr = if (id != null) " and to_char(b.acctId) = '$id'" else if (dptId != null) " and to_char(b.dptId) = '$dptId'" else if (orgId != null) " and to_char(b.orgId) = '$orgId'" else ""
        val compNameStr = if (compName == null) "" else " and b.comp_name like '%$compName%'"
        val dptNameStr = if (dptName == null) "" else " and b.dptName like '%$dptName%'"
        val acctNameStr = if (acctName == null) "" else " and b.acctName like '%$acctName%'"
        val selectStr = "select a.sumgoods_batch,b.comp_name,b.dptName,b.acctName,b.linkName,b.phone,COALESCE(sum(a.data_bweight),0.0)"
        val groupStr = " group by a.sumgoods_batch,b.comp_name,b.dptName,b.acctName,b.linkName,b.phone"
        val mainStr = "$selectStr from erp_forbi_xs@CRM_CRMSTAT a,v_erpcstm_basicinfo_list b where a.customer_code = b.erp_code and a.sumgoods_batch = '$sumgoodsBatch'$dataLevelStr$compNameStr$dptNameStr$acctNameStr$groupStr"
        val sortStr = if (sort == "0") " order by COALESCE(sum(a.data_bweight),0.0) desc" else " order by COALESCE(sum(a.data_bweight),0.0) asc"
        //计数
        val countSql = "select count(*) from($mainStr)"
        log.info("countSql>>>$countSql")
        //分页
        val queryStr = "$mainStr$sortStr"
        val querySql = commUtil.selectPageSql(currentPage, pageSize, queryStr)
        log.info("querySql>>>$querySql")
        //原生查询
        val countQuery = this.entityManager.createNativeQuery(countSql)
        val count = countQuery.resultList
        val total = count[0].toString().toInt()
        val query = this.entityManager.createNativeQuery(querySql)
        val queryList = query.resultList as List<Any>
        result["list"] = queryList
        result["total"] = total
        return result
    }

    // 保存新浪期货数据
    @Async
    fun saveSinaData(goodsCode: String){
        val res = httpService.sendGetRequest("http://hq.sinajs.cn/list=$goodsCode", "gbk")
        val p = Pattern.compile("\"(.*?)\"")
        val m = p.matcher(res.toString())
        if (m.find()) {
            val tempStr = m.group(0)
            val dataArr = tempStr.replace("\"", "").split(",")
            if (dataArr.size > 25) {
                val futureFields = arrayOf("futureName", "datetime", "openPrice", "highestPrice", "lowestPrice",
                        "", "purchasePrice", "sellingPrice", "latestPrice", "settlementPrice",
                        "ydaySettlementPrice", "purchaseWeight", "sellingWeight", "inventory", "turnover")
                val sina = SinaFutures()
                (0..14).map { idx ->
                    if (idx != 5) {
                        val value = dataArr[idx]
                        val fieldName = futureFields[idx]
                        val method = sina.javaClass.getMethod("set${fieldName.substring(0, 1).toUpperCase()}${fieldName.substring(1)}", String::class.java)
                        method.invoke(sina, value)
                    }
                }
                sina.futureCode = goodsCode
                // 查重校验
                val count = sinaRepo.repeatCount(sina.futureCode, sina.datetime!!)
                if (count > 0) {
                    log.info(">>>>${goodsCode}数据存在")
                } else {
                    sinaRepo.save(sina)
                    log.info(">>>>save $goodsCode Data success")
                }
            } else {
                log.info(">>>>${goodsCode}期货数据不符合要求")
            }
        } else {
            log.info(">>>>匹配不到符合的${goodsCode}数据")
        }
    }

    // 查询期货数据
    fun findSinaData(type: Int) : List<SinaFutures>{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateStr = sdf.format(Date())
        when (type) {//螺纹钢 热卷 铁矿 焦炭
            0 -> return sinaRepo.findSinaData(dateStr, CrmConstants.FUTURE_CODE_LWG)
            1 -> return sinaRepo.findSinaData(dateStr, CrmConstants.FUTURE_CODE_RZJB)
            2 -> return sinaRepo.findSinaData(dateStr, CrmConstants.FUTURE_CODE_TKS)
            3 -> return sinaRepo.findSinaData(dateStr, CrmConstants.FUTURE_CODE_JT)
            else -> return sinaRepo.findSinaData(dateStr, CrmConstants.FUTURE_CODE_LWG) //默认螺纹钢
        }
    }
}