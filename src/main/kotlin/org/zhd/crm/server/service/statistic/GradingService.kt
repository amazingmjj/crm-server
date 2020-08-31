package org.zhd.crm.server.service.statistic

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.GradeCoefficient
import org.zhd.crm.server.model.crm.GradeSummary
import org.zhd.crm.server.model.statistic.CustomerArrears
import org.zhd.crm.server.model.statistic.CustomerClassify
import org.zhd.crm.server.model.statistic.CustomerDealCount
import org.zhd.crm.server.model.statistic.CustomerExpt
import org.zhd.crm.server.repository.crm.CustomerRepository
import org.zhd.crm.server.repository.crm.GradeCoefficientRepository
import org.zhd.crm.server.repository.crm.GradeSummaryRepository
import org.zhd.crm.server.repository.statistic.*
import org.zhd.crm.server.service.crm.ProcedureService
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.script.ScriptEngineManager
import kotlin.streams.toList

// 客户分级业务
@Service
class GradingService {
    @Autowired
    private lateinit var customerClassifyRepo: CustomerClassifyRepository
    @Autowired
    private lateinit var cstmRepo: CustomerRepository
    @Autowired
    private lateinit var goodsSalesRepo: GoodsSalesRepository
    @Autowired
    private lateinit var cstmArrearsRepo: CustomerArrearsRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var cstmDealCountRepo: CustomerDealCountRepository
    @Autowired
    private lateinit var cstmExptRepo: CustomerExptRepository
    @Autowired
    private lateinit var gradeCoefficientRepo: GradeCoefficientRepository
    @Autowired
    private lateinit var cstmArrearRepo: CustomerArrearsRepository
    @Autowired
    private lateinit var salesmanHighSellRepo: SalesmanHighSellRepository
    @Autowired
    private lateinit var cstmBillingRepo: CustomerBillingRepository
    @Autowired
    private lateinit var behaviorRecordRepo: BehaviorRecordRepository
    @Autowired
    private lateinit var gradeSummaryRepo: GradeSummaryRepository
    @Autowired
    private lateinit var procedureService: ProcedureService


    private val log = LoggerFactory.getLogger(GradingService::class.java)

    private val sdfm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun platformProcedure() {
        val startTime = System.currentTimeMillis()
        log.info("调用三方平台存储过程开始:>>$startTime")
        procedureService.callPurePro("xy");
        procedureService.callPurePro("erp");
        val endTime = System.currentTimeMillis()
        log.info("调用三方平台存储过程结束;总耗时:${endTime - startTime} ms")
    }

    // 批量更新/导入客户分级明细
    fun batchSaveCstmClassify() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateStr = sdf.format(calendar.time)
        val startTime = System.currentTimeMillis()
        log.info("客户分级明细开始导入:>>>>$startTime")
        // 欠款
        saveCstmArrears(dateStr, calendar.timeInMillis)
        // 成交次数
        saveCstmDealCount(dateStr, calendar.timeInMillis)
        // 异常得分
        saveCstmExpt(dateStr, calendar.timeInMillis)

        val endTime = System.currentTimeMillis()
        log.info("客户分级明细导入完毕:>>>耗时${endTime - startTime}ms")
        // 客户分级评分
        cstmGradingSummary(dateStr, calendar.timeInMillis, sdf)
    }

    // 单个客户评分(用于检测数据的正确性)
    fun singleCstmGradingSummary(dateStr: String, compName: String): Map<String, Any> {
        val report = HashMap<String, Any>()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val cstmMapList = cstmRepo.simpleNameList()
        val cstmGroupMap = cstmMapList.groupBy { m -> m.get("publicName")!! }
//        val cstmKeys = cstmGroupMap.keys.toList()
        val gradeCoefficient = gradeCoefficientRepo.findOne(1)
        val params = gradeCoefficient.equationName!!.split("||")[1].split("*")
        var equalStr = gradeCoefficient.name
        var onlineParam = gradeCoefficientRepo.findByName("线上销量").coefficient
        var offlineParam = gradeCoefficientRepo.findByName("线下销量").coefficient
        val regionList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_REGION)
        // 其他客户性质
        var typeOther = gradeCoefficientRepo.findByName("其他客户性质").coefficient
        var typeBusi = gradeCoefficientRepo.findByName("经销商").coefficient
        var typeClient = gradeCoefficientRepo.findByName("终端客户").coefficient
        // 欠款列表
        var arrearList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_DEBT)
        // 高卖参数
        var highSell = gradeCoefficientRepo.findByName("高卖金额").coefficient
        // 成交次数参数
        var dealCount = gradeCoefficientRepo.findByName("成交次数").coefficient
        // 异常违约参数
        var exptBreak = gradeCoefficientRepo.findByName("违约次数").coefficient
        // 异常恶意锁货
        var exptLock = gradeCoefficientRepo.findByName("恶意锁货次数").coefficient
        // 异常取消次数
        var exptCancel = gradeCoefficientRepo.findByName("取消次数").coefficient
        // 正式客户
        var formalCstm = gradeCoefficientRepo.findByName("正式客户").coefficient
        // 公共客户
        var publicCstm = gradeCoefficientRepo.findByName("公共客户").coefficient
        // 品类列表
        var categoryList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_CATEGORY)
        // 代开票是
        var billing = gradeCoefficientRepo.findByName("Y").coefficient
        // 代开票否
        var notBill = gradeCoefficientRepo.findByName("N").coefficient
        // 新老客户判断时间点
        var startDate = sdf.parse("2018-01-01")
        var calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.YEAR, 1)
        var endDate = calendar.time
        // 新客户
        var newParam = gradeCoefficientRepo.findByName("新客户").coefficient
        // 老客户
        var oldParam = gradeCoefficientRepo.findByName("老客户").coefficient
        // 平台活跃系数
        var activity = gradeCoefficientRepo.findByName("日均登录次数").coefficient
        val gradeArr = HashMap<String, Array<Double>>()
        var list = cstmGroupMap.get(compName)
        if (list == null) {
            report.put("returnCode", -1)
            report.put("errMsg", "用户不存在")
        } else {
            val singleMap = list.find { m -> m.get("compName").equals(compName) }!!
            val deptName = singleMap.get("dptName")!!
            val acctName = singleMap.get("acctName")!!
            val billDate = singleMap.get("billDate")!!
            try {

                // 计算分数
                var totalScore = 0.0
                var paramScores = emptyArray<Double>()
                params.map { m ->
                    paramScores = paramScores.plus(0.0)
                }
                params.indices.map { idx ->
                    val key = params.get(idx)
                    when (key) {
                        "销量得分" -> paramScores[idx] = calcSales(list, dateStr, compName, onlineParam, offlineParam)
                        "地区得分" -> paramScores[idx] = calcRegion(list, regionList, compName)
                        "类型得分" -> paramScores[idx] = calcProperty(list, typeOther, typeBusi, typeClient, compName)
                        "欠款得分" -> paramScores[idx] = calcArrear(list, compName, arrearList)
                        "高卖得分" -> paramScores[idx] = calcHighSell(list, compName, highSell)
                        "频次得分" -> paramScores[idx] = calcDealCount(list, compName, dealCount)
                        "异常得分" -> paramScores[idx] = calcExpt(list, compName, exptBreak, exptLock, exptCancel)
                        "正式/公共得分" -> paramScores[idx] = calcCstmMark(list, compName, formalCstm, publicCstm)
                        "品类得分" -> paramScores[idx] = calcCategory(list, compName, categoryList)
                        "代开票得分" -> paramScores[idx] = calcBilling(list, compName, billing, notBill)
                        "新旧客户得分" -> paramScores[idx] = calcNewCstm(list, compName, sdf, startDate, endDate, newParam, oldParam)
                        "平台活跃得分" -> paramScores[idx] = calcPlatformActivity(list, compName, startDate, dateStr, activity)
                    }
                }
                var copyEqualStr = equalStr
                params.indices.map { idx ->
                    copyEqualStr = copyEqualStr.replace("\${${params[idx]}}", paramScores[idx].toString())
                }
                log.info("equalStr>>>>>$copyEqualStr")
                val mgr = ScriptEngineManager()
                val engine = mgr.getEngineByName("JavaScript")
                val decimalFormat = DecimalFormat("#.00")
                val result = decimalFormat.format(engine.eval(copyEqualStr).toString().toDouble())
                log.info("result:>>>$result")
                totalScore = result.toDouble()
                report.put("returnCode", 0)
                report.put("equalResult", result)
                report.put("equalStr", copyEqualStr)
                report.put("compName", compName)
                report.put("dptName", deptName)
                report.put("acctName", acctName)
                report.put("billDate", billDate)
            } catch (e: Exception) {
                log.error("单个计算客户评分异常:>>>", e)
                report.put("returnCode", -1)
                report.put("errMsg", e.message!!)
            }
        }
        return report
    }

    // 客户分级评分
    fun cstmGradingSummary(dateStr: String, time: Long, sdf: SimpleDateFormat, rowUpdate: Boolean = false) {
        var calcTimeStart = System.currentTimeMillis()
        log.info("客户分级服务开始时间:>>>>$calcTimeStart")
        val cstmMapList = cstmRepo.simpleNameList()
        val cstmGroupMap = cstmMapList.groupBy { m -> m.get("publicName")!! }
        val cstmKeys = cstmGroupMap.keys.toList()
        val gradeCoefficient = gradeCoefficientRepo.findOne(1)
        val params = gradeCoefficient.equationName!!.split("||")[1].split("*")
        var equalStr = gradeCoefficient.name
        var onlineParam = gradeCoefficientRepo.findByName("线上销量").coefficient
        var offlineParam = gradeCoefficientRepo.findByName("线下销量").coefficient
        val regionList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_REGION)
        // 其他客户性质
        var typeOther = gradeCoefficientRepo.findByName("其他客户性质").coefficient
        var typeBusi = gradeCoefficientRepo.findByName("经销商").coefficient
        var typeClient = gradeCoefficientRepo.findByName("终端客户").coefficient
        // 欠款列表
        var arrearList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_DEBT)
        // 高卖参数
        var highSell = gradeCoefficientRepo.findByName("高卖金额").coefficient
        // 成交次数参数
        var dealCount = gradeCoefficientRepo.findByName("成交次数").coefficient
        // 异常违约参数
        var exptBreak = gradeCoefficientRepo.findByName("违约次数").coefficient
        // 异常恶意锁货
        var exptLock = gradeCoefficientRepo.findByName("恶意锁货次数").coefficient
        // 异常取消次数
        var exptCancel = gradeCoefficientRepo.findByName("取消次数").coefficient
        // 正式客户
        var formalCstm = gradeCoefficientRepo.findByName("正式客户").coefficient
        // 公共客户
        var publicCstm = gradeCoefficientRepo.findByName("公共客户").coefficient
        // 品类列表
        var categoryList = gradeCoefficientRepo.findByParentId(CrmConstants.GRADE_COEFFICIENT_CATEGORY)
        // 代开票是
        var billing = gradeCoefficientRepo.findByName("Y").coefficient
        // 代开票否
        var notBill = gradeCoefficientRepo.findByName("N").coefficient
        // 新老客户判断时间点
        var startDate = sdf.parse("2018-01-01")
        var calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.YEAR, 1)
        var endDate = calendar.time
        // 新客户
        var newParam = gradeCoefficientRepo.findByName("新客户").coefficient
        // 老客户
        var oldParam = gradeCoefficientRepo.findByName("老客户").coefficient
        // 平台活跃系数
        var activity = gradeCoefficientRepo.findByName("日均登录次数").coefficient
        val gradeArr = HashMap<String, Array<Double>>()
        (0..(cstmKeys.size - 1)).map { k ->
            val compName = cstmKeys.get(k)
            var gradeSummary = gradeSummaryRepo.findObj(compName, dateStr)
            if (gradeSummary != null && !rowUpdate) {
                log.info("$dateStr, $compName 记录已存在")
            } else {
                try {
                    val list = cstmGroupMap.get(compName)!!
                    // 找到与公司名称相同的对象
                    val singleMap = list.find { m -> m.get("compName").equals(compName) }!!
                    val deptName = singleMap.get("dptName")!!
                    val acctName = singleMap.get("acctName")!!
                    val billDate = singleMap.get("billDate")!!
                    val erpCode = singleMap.get("erpCode")
                    val xyCode = singleMap.get("eUserId")
                    //0其他 1型云 2线下
                    val xyCondition = if (!xyCode.isNullOrBlank() && !erpCode.isNullOrBlank()) 1 else if (xyCode.isNullOrBlank() && !erpCode.isNullOrBlank()) 2 else 0
                    // 计算分数
                    var totalScore = 0.0
                    var paramScores = emptyArray<Double>()
                    params.map { m ->
                        paramScores = paramScores.plus(0.0)
                    }
                    params.indices.map { idx ->
                        val key = params.get(idx)
                        when (key) {
                            "销量得分" -> paramScores[idx] = calcSales(list, dateStr, compName, onlineParam, offlineParam)
                            "地区得分" -> paramScores[idx] = calcRegion(list, regionList, compName)
                            "类型得分" -> paramScores[idx] = calcProperty(list, typeOther, typeBusi, typeClient, compName)
                            "欠款得分" -> paramScores[idx] = calcArrear(list, compName, arrearList)
                            "高卖得分" -> paramScores[idx] = calcHighSell(list, compName, highSell)
                            "频次得分" -> paramScores[idx] = calcDealCount(list, compName, dealCount)
                            "异常得分" -> paramScores[idx] = calcExpt(list, compName, exptBreak, exptLock, exptCancel)
                            "正式/公共得分" -> paramScores[idx] = calcCstmMark(list, compName, formalCstm, publicCstm)
                            "品类得分" -> paramScores[idx] = calcCategory(list, compName, categoryList)
                            "代开票得分" -> paramScores[idx] = calcBilling(list, compName, billing, notBill)
                            "新旧客户得分" -> paramScores[idx] = calcNewCstm(list, compName, sdf, startDate, endDate, newParam, oldParam)
                            "平台活跃得分" -> paramScores[idx] = calcPlatformActivity(list, compName, startDate, dateStr, activity)
                        }
                    }
                    var copyEqualStr = equalStr
                    params.indices.map { idx ->
                        copyEqualStr = copyEqualStr.replace("\${${params[idx]}}", paramScores[idx].toString())
                    }
                    log.info("equalStr>>>>>$copyEqualStr")
                    val mgr = ScriptEngineManager()
                    val engine = mgr.getEngineByName("JavaScript")
                    val decimalFormat = DecimalFormat("#.00")
                    val result = decimalFormat.format(engine.eval(copyEqualStr).toString().toDouble())
                    log.info("result:>>>$result")
                    totalScore = result.toDouble()
                    if (!rowUpdate) {
                        gradeSummary = GradeSummary()
                        gradeSummary.compName = compName
                        gradeSummary.acctName = acctName
                        gradeSummary.dptName = deptName
                        gradeSummary.summaryDate = Timestamp(time)
                        gradeSummary.transformDate = Timestamp(sdf.parse(billDate).time)
                        gradeSummary.xyCondition = xyCondition
                    }
                    gradeSummary!!.summary = totalScore
                    gradeSummaryRepo.save(gradeSummary)
                } catch (e: Exception) {
                    log.error("客户等级评分出现异常:>>>", e)
                    commUtil.errLogSave("客户等级评分出现异常:>>>CompName:${compName}; errorLog:>>>${e.message}")
                }
            }
        }
        // 更新等级排名
        val totalCount = gradeSummaryRepo.totalDayCount(dateStr)
        // 目前分三级
        val aEnd = totalCount * 0.3
        val bEnd = totalCount * 0.6
        // 更新任务等级
        // A等级
        gradeSummaryRepo.batchUpdateLevel("A", dateStr, 1, Math.round(aEnd).toInt())
        // B等级
        gradeSummaryRepo.batchUpdateLevel("B", dateStr, Math.round(aEnd + 1).toInt(), Math.round(bEnd).toInt())
        // C等级
        gradeSummaryRepo.batchUpdateLevel("C", dateStr, Math.round(bEnd + 1).toInt(), totalCount)
        val calcTimeEnd = System.currentTimeMillis()
        log.info("客户分级得分耗时:>>>>${calcTimeEnd - calcTimeStart} ms")

    }

    // 销售得分计算
    fun calcSales(cstmList: List<Map<String, String>>, dateStr: String, compName: String, onlineParam: Double, offlineParam: Double): Double {
        var online = 0.0
        var offline = 0.0
        cstmList.indices.map { idx ->
            val erpCode = cstmList.get(idx).get("erpCode")!!
            online += goodsSalesRepo.sumTotalWeight(erpCode, dateStr + " 23:59:59", 1)
            offline += goodsSalesRepo.sumTotalWeight(erpCode, dateStr + " 23:59:59", 0)
        }
        log.info("compName:>>>$compName; online:>>$online; offline:>>$offline")
        return online * onlineParam + offline * offlineParam
    }

    // 地区得分
    fun calcRegion(cstmList: List<Map<String, String>>, regionList: List<GradeCoefficient>, compName: String): Double {
        var regionScore = regionList.filter { gc -> gc.name.equals("其他地区") }.get(0).coefficient
        var defaultScore = 0.0
        cstmList.map { m ->
            var region = m.get("region")
            if (region != null) {
                val arr = regionList.filter { gc -> gc.name.equals(region) }
                if (arr.isNotEmpty()) defaultScore += arr.get(0).coefficient

            }
        }
        if (defaultScore > 0) regionScore = defaultScore
        log.info("compName:$compName; 地区得分:>>>$regionScore")
        return regionScore
    }

    // 客户性质得分
    fun calcProperty(cstmList: List<Map<String, String>>, typeOther: Double, typeBusi: Double, typeClient: Double, compName: String): Double {
        var property = 0.0
        cstmList.map { m ->
            val type = if (m.get("cstmProperty") == null) "其他客户性质" else m.get("cstmProperty")!!
            when (type) {
                "经销商" -> property += typeBusi
                "终端客户" -> property += typeClient
                else -> property += typeOther
            }
        }
        log.info("compName:>>$compName; 客户性质得分:>>>$property")
        return property
    }

    // 欠款得分
    fun calcArrear(cstmList: List<Map<String, String>>, compName: String, arrearList: List<GradeCoefficient>): Double {
        var arrear = 0.0
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            // 欠款金额
            var amount = cstmArrearRepo.sumAmount(erpCode)
            if (amount > 0) arrear += findCorrectCradeArrear(arrearList, amount)

        }
        log.info("compName:>>$compName; 欠款:>>$arrear")
        return arrear
    }

    private fun findCorrectCradeArrear(arrearList: List<GradeCoefficient>, amount: Double): Double {
//        log.info("amount:>>>$amount")
        var defaultIdx = -1
        (0..(arrearList.size - 1)).map { idx ->
            val gcName = arrearList.get(idx).name
            var arr = gcName.split(",")
            log.info("gcName:>>>$gcName; arr size:>>${arr.size}")
            if (arr.size == 1 && gcName.toDouble() < amount) defaultIdx = idx
            if (arr.size == 2 && (arr[0].toDouble() < amount && arr[1].toDouble() >= amount)) defaultIdx = idx
        }
        if (defaultIdx == -1) throw Exception("客户欠款基础欠款范围设置出现问题")
        return arrearList.get(defaultIdx).coefficient
    }

    // 高卖得分
    fun calcHighSell(cstmList: List<Map<String, String>>, compName: String, highParam: Double): Double {
        var highSell = 0.0
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            highSell += salesmanHighSellRepo.sumAmount(erpCode)
        }
        highSell = highSell * highParam
        log.info("compName:>>$compName; 高卖:>>>$highSell")
        return highSell
    }

    // 成交次数
    fun calcDealCount(cstmList: List<Map<String, String>>, compName: String, dealCountParam: Double): Double {
        var dealCount = 0.0
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            dealCount += cstmDealCountRepo.sumCount(erpCode)
        }
        dealCount = dealCount * dealCountParam
        log.info("compName:>>>$compName; 成交次数:>>>$dealCount")
        return dealCount
    }

    // 异常得分
    fun calcExpt(cstmList: List<Map<String, String>>, compName: String, exptBreak: Double, exptLock: Double, exptCancel: Double): Double {
        var expt = 0.0
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            var list = cstmExptRepo.findListByErpCode(erpCode)
            if (list.size > 0) {
                // 违约
                var breakList = list.filter { expt -> expt.type == 1 }
                expt += exptBreak * (breakList.map { expt -> expt.exptCount!! }.sum())
                // 恶意锁货
                var lockList = list.filter { expt -> expt.type == 2 }
                expt += exptLock * (lockList.map { expt -> expt.exptCount!! }.sum())
                // 取消
                var cancelList = list.filter { expt -> expt.type == 3 }
                expt += exptCancel * (cancelList.map { expt -> expt.exptCount!! }.sum())
            }
        }
        log.info("compName:>>$compName; 异常得分:>>>$expt")
        return expt
    }

    // 正式/公共客户得分
    fun calcCstmMark(cstmList: List<Map<String, String>>, compName: String, formalParam: Double, pubParam: Double): Double {
        var mark = 0.0
        cstmList.map { m ->
            var markStr = m.get("mark")!!
            if (markStr.equals("2")) mark += formalParam else mark += pubParam
        }
        log.info("compName:>>$compName; 正式/公共客户得分:>>>$mark")
        return mark
    }

    // 品类得分
    fun calcCategory(cstmList: List<Map<String, String>>, compName: String, gradeCategoryList: List<GradeCoefficient>): Double {
        var category = 0.0
        var defaultScore = gradeCategoryList.filter { gc -> gc.name.equals("其他") }.get(0).coefficient
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            var goodslist = goodsSalesRepo.distinctGoods(erpCode)
            goodslist.map { s ->
                var gc = gradeCategoryList.find { gc -> gc.name.equals(s) }
                if (gc == null) category += defaultScore else category += gc.coefficient
            }
        }
        log.info("compName:>>>$compName; 品类得分:>>>$category")
        return category
    }

    // 代开票得分
    fun calcBilling(cstmList: List<Map<String, String>>, compName: String, billing: Double, notBill: Double): Double {
        var bill = 0.0
        cstmList.map { m ->
            var erpCode = m.get("erpCode")!!
            if (cstmBillingRepo.billingCount(erpCode) > 0) bill += billing else bill += notBill
        }
        log.info("compName:>>>$compName; 代开票得分:>>>$bill")
        return bill
    }

    // 新旧客户得分
    fun calcNewCstm(cstmList: List<Map<String, String>>, compName: String, sdf: SimpleDateFormat, startDate: Date, endDate: Date, newParam: Double, oldParam: Double): Double {
        var newCstm = 0.0
        cstmList.map { m ->
            var cstmDate = sdf.parse(m.get("createAt"))
            if (cstmDate.before(endDate) && cstmDate.after(startDate)) newCstm += newParam else newCstm += oldParam
        }
        log.info("compName:>>>$compName; 新旧客户得分:>>>$newCstm")
        return newCstm
    }

    // 平台活跃得分
    fun calcPlatformActivity(cstmList: List<Map<String, String>>, compName: String, startDate: Date, dateStr: String, activityParam: Double): Double {
        var activity = 0.0
        cstmList.map { m ->
            val ecode = m.get("eUserId")
            if (ecode != null) {
                val avgDay = (sdfm.parse("$dateStr 23:59:59").time - startDate.time) / (1000 * 3600 * 24)
                val userIdStr = ecode.substring(2, 8).toInt().toString()
                val count = behaviorRecordRepo.loginCountByXy(userIdStr)
                log.info("avgDay:>>$avgDay,userIdStr:$userIdStr,count:$count")
                activity += (count * activityParam) / avgDay
            }
        }
        log.info("compName:>>>$compName; 平台活跃得分:>>>$activity")
        return activity
    }

    // 欠款
    private fun saveCstmArrears(dateStr: String, time: Long) {
        val arrearList = cstmArrearsRepo.findAll().toList().groupBy { ar -> ar.erpCode!! }
        val arrearKeys = arrearList.keys.toList()
        if (arrearList.size > 0) recycleArrearSave(arrearKeys, dateStr, arrearList, time, 0)
    }

    private fun recycleArrearSave(arrearKeys: List<String>, dateStr: String, arrearList: Map<String, List<CustomerArrears>>, time: Long, startIndex: Int) {
        (startIndex..(arrearKeys.size - 1)).map { idx ->
            var erpCode = arrearKeys.get(idx)
            var erpName = arrearList.get(erpCode)!!.get(0).erpName!!
            try {
                saveCstmClassify(dateStr, erpCode, erpName, time, "欠款金额", "arrear", arrearList.get(erpCode)!!.stream().map { ar -> ar.arrearAmount!! }.toList().sum().toString())
            } catch (e: Exception) {
                log.error("保存/更新客户欠款异常", e)
                commUtil.errLogSave("保存/更新客户欠款异常|idx=$idx,$erpCode, $erpName|errMsg:>>${e.message}")
            }
        }
    }

    @Throws(Exception::class)
    private fun saveCstmClassify(dateStr: String, erpCode: String, erpName: String, time: Long, name: String, type: String, value: String) {
        var obj = customerClassifyRepo.findObject(name, dateStr, type, erpCode)
        if (obj == null) {
            obj = CustomerClassify()
            obj.dealDate = Timestamp(time)
            obj.name = name
            obj.erpCode = erpCode
            obj.erpName = erpName
            obj.type = type
            obj.value = value
        }
        if (obj.id == null) customerClassifyRepo.save(obj)
    }

    // 成交次数
    private fun saveCstmDealCount(dateStr: String, time: Long) {
        val dealCountList = cstmDealCountRepo.findAll().toList()
        if (dealCountList.size > 0) recycleDealCountSave(dealCountList, dateStr, time, 0)
    }

    private fun recycleDealCountSave(dealCountList: List<CustomerDealCount>, dateStr: String, time: Long, startIndex: Int) {
        (startIndex..(dealCountList.size - 1)).map { idx ->
            val tempDealCount = dealCountList.get(idx)
            try {
                saveCstmClassify(dateStr, tempDealCount.erpCode!!, tempDealCount.erpName!!, time, "成交次数", "dealCount", tempDealCount.dealCount!!.toString())
            } catch (e: Exception) {
                log.error("保存/更新客户成交次数异常", e)
                commUtil.errLogSave("保存/更新客户成交次数异常|idx=$idx,${tempDealCount.erpCode},${tempDealCount.erpName}|errMsg:>>${e.message}")
            }
        }
    }

    // 异常得分
    private fun saveCstmExpt(dateStr: String, time: Long) {
        val exptList = cstmExptRepo.findAll().toList()
        // type 1 违约次数
        val exptfirst = exptList.filter { exp -> exp.type!!.equals(1) }
        if (exptfirst.size > 0) recycleExptSave(exptfirst, dateStr, time, 0, "违约次数")
        // type 2 恶意锁货
        val exptSec = exptList.filter { exp -> exp.type!!.equals(2) }
        if (exptSec.size > 0) recycleExptSave(exptSec, dateStr, time, 0, "恶意锁货")
        // type 3 取消次数
        val exptThird = exptList.filter { exp -> exp.type!!.equals(3) }
        if (exptThird.size > 0) recycleExptSave(exptThird, dateStr, time, 0, "取消次数")
    }

    private fun recycleExptSave(exptList: List<CustomerExpt>, dateStr: String, time: Long, startIndex: Int, exptType: String) {
        log.info("exptList.size:>>>${exptList.size - 1}")
        (startIndex..(exptList.size - 1)).map { idx ->
            log.info("idx:>>$idx; type:>>>$exptType")
            val tempExpt = exptList.get(idx)
            try {
                saveCstmClassify(dateStr, tempExpt.erpCode!!, tempExpt.erpName!!, time, "异常-$exptType", "expt", tempExpt.exptCount!!.toString())
            } catch (e: Exception) {
                log.error("保存/更新客户异常次数-$exptType|idx:>>$idx", e)
                commUtil.errLogSave("保存/更新客户异常次数-$exptType|idx=$idx,${tempExpt.erpCode},${tempExpt.erpName}|errMsg:>>${e.message}")
            }
        }
    }
}

//fun main(args: Array<String>) {
//    val a = "\${销量得分}+\${地区得分}"
//    println(a.replace("\${销量得分}", "334"))
//    var x = arrayOf(0.0, 0.0, 0.0)
//    x[1]= 1.0
//    x.map { j ->
//        println(j)
//    }
//}


