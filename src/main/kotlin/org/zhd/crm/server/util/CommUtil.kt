package org.zhd.crm.server.util

import net.coobird.thumbnailator.Thumbnails
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import org.zhd.crm.server.model.crm.ErrorLog
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.repository.crm.DictionaryRepository
import org.zhd.crm.server.repository.crm.ErrorLogRepository
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.*
import java.net.URLEncoder
import java.security.MessageDigest
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList

@Component
class CommUtil {
    private val longSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val shortSdf = SimpleDateFormat("yyyy-MM-dd")
    @Autowired
    private lateinit var dictRepo: DictionaryRepository
    @Autowired
    private lateinit var errLogRepo: ErrorLogRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository

    private val log = LoggerFactory.getLogger(CommUtil::class.java)

    fun sha1(str: String): String {
        if (str.length == 0) {
            return ""
        }
        try {
            var mdTemp = MessageDigest.getInstance("SHA-1")
            mdTemp.update(str.toByteArray(Charsets.UTF_8))
            val md = mdTemp.digest()
            var j = md.count()
            val hexStr = StringBuffer()
            for (i in 0..(j - 1)) {
                var shaHex = Integer.toHexString(md[i].toInt().and(0xFF))
                if (shaHex.count() < 2) {
                    hexStr.append(0)
                }
                hexStr.append(shaHex)
            }
            return hexStr.toString()
        } catch (e: Exception) {
            return ""
        }
    }

    fun getTempImgFile() = ClassPathResource("temp/tempImg").file

    @Throws(Exception::class)
    fun gzipSize(inputSteam: InputStream, fileType: String) {
        var file = ClassPathResource("temp/tempImg").file
        val fileOutputStream = FileOutputStream(file)
        var type = "jpg"
        if ("image/png".equals(fileType)) type = "png"
        Thumbnails.of(inputSteam).scale(1.0).outputFormat(type).outputQuality(.5).toOutputStream(fileOutputStream)
    }

    fun autoSetClass(originClass: Any, targetClass: Any, excludeFieldNames: Array<String> = arrayOf("id", "updateAt", "createAt")): Any {
        originClass.javaClass.declaredFields.map { f ->
            val methodName = f.name.substring(0, 1).toUpperCase() + f.name.substring(1)
            if (!excludeFieldNames.contains(f.name)) {
                val om = originClass.javaClass.getMethod("get$methodName")
                val m = targetClass.javaClass.getMethod("set$methodName", f.type)
                m.invoke(targetClass, om.invoke(originClass))
            }
        }
        return targetClass
    }

    fun entityToMap(currentClass: Any, excludeFieldNames: Array<String> = emptyArray()): Map<String, Any> {
        val result = HashMap<String, Any>()
        currentClass.javaClass.declaredFields.map { f ->
            val methodName = f.name.substring(0, 1).toUpperCase() + f.name.substring(1)
            if (!excludeFieldNames.contains(f.name)) {
                val cm = currentClass.javaClass.getMethod("get$methodName")
                result.put(f.name, cm.invoke(currentClass))
            }
        }
        return result
    }

    // 获取日期范围（用于折线图)
    fun getDateRange(startDate: String, endDate: String): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val ssdf = SimpleDateFormat("MM月dd日")
        val calendar = Calendar.getInstance()
        val sDate = sdf.parse(startDate)
        calendar.time = sDate
        val calendarEnd = Calendar.getInstance()
        calendarEnd.time = sDate
        val dateList = ArrayList<String>()
        dateList.add(ssdf.format(sDate))
        val edate = sdf.parse(endDate)
        while (calendarEnd.time.before(edate)) {
            calendarEnd.add(Calendar.DAY_OF_MONTH, 1)
            val tempDate = ssdf.format(calendarEnd.time)
            dateList.add(tempDate)
        }
        return dateList
    }

    // 获取年月范围（用于折线图)
    fun getMonthRange(startDate: String, endDate: String): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM")
        val calendar = Calendar.getInstance()
        val sd = sdf.parse(startDate)
        calendar.time = sd
        val monthList = ArrayList<String>()
        monthList.add(sdf.format(sd))
        val ed = sdf.parse(endDate)
        while (calendar.time.before(ed)) {
            calendar.add(Calendar.MONTH, 1)
            val tempDate = sdf.format(calendar.time)
            monthList.add(tempDate)
        }
        return monthList
    }

    //将带有_的字符串转为驼峰形式
    fun getColumnMethodName(key: String): String {
        val keyArr = key.split("_")
        var modifyArr = arrayOf("")
        keyArr.indices.map { i ->
            val modKey = keyArr[i].substring(0, 1).toUpperCase() + keyArr[i].substring(1)
            modifyArr = modifyArr.plus(modKey)
        }
        return modifyArr.joinToString("")
    }

    //将驼峰形式的字符串转为带_的
    fun underscoreName(key: String): String {
        val strBuf = StringBuffer()
        key.indices.map { i ->
            if (i == 0) strBuf.append(key[i].toLowerCase())
            else if (Character.isUpperCase(key[i])) {
                strBuf.append("_")
                strBuf.append(key[i].toLowerCase())
            } else strBuf.append(key[i])
        }
        return strBuf.toString()
    }

    // 获取当天x天前后的时间，value负数表示x天前，正数表示x天后
    fun getDay(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, value)
        return Timestamp(cal.timeInMillis)
    }

    // 获取某天x天前后的时间，value负数表示x天前，正数表示x天后
    fun getDay(currentTime: Timestamp, value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.time = currentTime
        cal.add(Calendar.DAY_OF_MONTH, value)
        return Timestamp(cal.timeInMillis)
    }

    // 获取某个时间x分钟前后的时间，value负数表示x分钟前，正数表示x分钟后
    fun getMinute(currentTime: Timestamp, value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.time = currentTime
        cal.add(Calendar.MINUTE, value)
        return Timestamp(cal.timeInMillis)
    }

    // 获取x天前后的开始时间,value负数表示x天前，正数表示x天后
    fun getDayStart(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, value)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Timestamp(cal.timeInMillis)
    }

    // 获取x天前后的结束时间,value负数表示x天前，正数表示x天后
    fun getDayEnd(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, value)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return Timestamp(cal.timeInMillis)
    }

    // 获得当天某小时初始时间,value表示某小时
    fun getHourStart(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, value)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Timestamp(cal.timeInMillis)
    }

    // 获得当天某小时结束时间,value表示某小时
    fun getHourEnd(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, value)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return Timestamp(cal.timeInMillis)
    }

    // 获得周一时间 value: 0表示本周，1表示下周，-1表示上周
    fun getWeekStartTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        val weekday = cal.get(Calendar.DAY_OF_WEEK) - value * 7
        cal.add(Calendar.DATE, 2 - weekday)
        return Timestamp(cal.timeInMillis)
    }

    // 获得周日时间 value: 0表示本周，1表示下周，-1表示上周
    fun getWeekEndTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        val weekday = cal.get(Calendar.DAY_OF_WEEK) - value * 7
        cal.add(Calendar.DATE, 8 - weekday)
        return Timestamp(cal.timeInMillis)
    }

    // 获得月第一天时间 value: 0表示本月，1表示下月，-1表示上月
    fun getMonthStartTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, value)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        return Timestamp(cal.timeInMillis)
    }

    // 获得月最后一天时间 value: 0表示本月，1表示下月，-1表示上月
    fun getMonthEndTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, value)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return Timestamp(cal.timeInMillis)
    }

    // 获得季度第一天时间 value: 0表示本季度，1表示下季度，-1表示上季度
    fun getQuarterStartTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH) + 1 + value * 3
        //只计算到去年第四季度或明年第一季度
        val month: Int = if (currentMonth < 1) 9 else if (currentMonth in 1..3) 0 else if (currentMonth in 4..6) 3 else if (currentMonth in 7..9) 6 else if (currentMonth in 10..12) 9 else 0
        if (currentMonth < 1) cal.add(Calendar.YEAR, -1) else if (currentMonth > 12) cal.add(Calendar.YEAR, 1) else {
        }
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DATE, 1)
        return Timestamp(cal.timeInMillis)
    }

    // 获得季度最后一天时间 value: 0表示本季度，1表示下季度，-1表示上季度
    fun getQuarterEndTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH) + 1 + value * 3
        //只计算到去年第四季度或明年第一季度
        val month: Int = if (currentMonth < 1) 11 else if (currentMonth in 1..3) 2 else if (currentMonth in 4..6) 5 else if (currentMonth in 7..9) 8 else if (currentMonth in 10..12) 11 else 2
        val day: Int = if (currentMonth < 1) 31 else if (currentMonth in 1..3) 31 else if (currentMonth in 4..6) 30 else if (currentMonth in 7..9) 30 else if (currentMonth in 10..12) 31 else 31
        if (currentMonth < 1) cal.add(Calendar.YEAR, -1) else if (currentMonth > 12) cal.add(Calendar.YEAR, 1) else {
        }
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DATE, day)
        return Timestamp(cal.timeInMillis)
    }

    //本年度第一天时间 value: 0表示今年，1表示明年，-1表示去年
    fun getYearStartTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, value)
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DATE, 1)
        return Timestamp(cal.timeInMillis)
    }

    //本年度最后一天时间 value: 0表示今年，1表示明年，-1表示去年
    fun getYearEndTime(value: Int): Timestamp {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, value)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DATE, 31)
        return Timestamp(cal.timeInMillis)
    }

    //汉字获取首字母大写
    fun getFirstSpell(name: String): String {
        var str = ""
        if (name.isBlank()) return str
        val arr: CharArray = name.toCharArray()
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.UPPERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        if (codeType(arr[0]) == 1) {
            val pyArr = PinyinHelper.toHanyuPinyinStringArray(arr[0], format)
            val spell = pyArr[0].toCharArray().get(0).toString()
            if (pyArr.size == 1) {
                str = spell
            } else {//多音字查字典匹配
                if (judgeSame(pyArr)) {
                    str = spell //多音字首字母都相同就取第一个
                } else {
                    if (arr.size > 1) {
                        val firstMatch = dictRepo.findByName(name.substring(0, 2))
                        if (firstMatch == null) {
                            val secondMatch = dictRepo.findByName(name.substring(0, 1))
                            str = if (secondMatch == null) spell else secondMatch
                        } else str = firstMatch
                    } else {
                        val match = dictRepo.findByName(name.substring(0, 1))
                        str = if (match == null) spell else match
                    }
                }
            }
        } else if (codeType(arr[0]) == 3) str = arr[0].toString().toUpperCase()
        else str = arr[0].toString()
        return str
    }

    //获取姓名首字母
    fun getInitialName(name: String): String {
        var result = ""
        val arr: CharArray = name.toCharArray()
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE //小写
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        (0..(arr.size - 1)).map { idx ->
            var str = ""
            if (codeType(arr[idx]) == 1) {
                val pyArr = PinyinHelper.toHanyuPinyinStringArray(arr[idx], format)
                val spell = pyArr[0].toCharArray().get(0).toString()
                if (pyArr.size == 1) {
                    str = spell
                } else {//多音字查字典匹配
                    if (judgeSame(pyArr)) {
                        str = spell //多音字首字母都相同就取第一个
                    } else {
                        val match = dictRepo.findByName(arr[idx].toString())
                        str = if (match == null) spell else match
                    }
                }
            }
            result = "$result${str.toLowerCase()}"
        }
        return result
    }

    //判断多音字首字母是否相同
    fun judgeSame(pyArr: Array<String>): Boolean {
        val spell = pyArr[0].toCharArray().get(0)
        var tempMark = true
        (1..(pyArr.size - 1)).map { idx ->
            val temp = pyArr[idx].toCharArray().get(0)
            if (spell != temp) tempMark = false //首字母存在不同
        }
        return tempMark
    }

    //判断是哪种字符类型
    fun codeType(ch: Char): Int {
        if (ch in '\u4e00'..'\u9fa5') return 1 //中文字符
        else if (ch in '\u0030'..'\u0039') return 2 //数字字符
        else if ((ch in '\u0041'..'\u005A') or (ch in '\u0061'..'\u007A')) return 3 //英文字符
        else return 0
    }

    //处理省
    fun handleXYCompProv(prov: String): String {
        var provStr = ""
        val districtArr = arrayOf("北京", "天津", "上海", "重庆")
        val autoRegArr = arrayOf("内蒙古", "广西壮族", "广西", "西藏", "宁夏回族", "宁夏", "新疆维吾尔", "新疆")
        val specialRegArr = arrayOf("香港", "澳门")
        if (prov.isNotBlank() && prov.indexOf("省") == -1) {
            if (districtArr.contains(prov)) provStr = prov + "市"
            else if (autoRegArr.contains(prov)) provStr = if (prov == "广西") prov + "壮族自治区" else if (prov == "宁夏") prov + "回族自治区" else if (prov == "新疆") prov + "维吾尔自治区" else prov + "自治区"
            else if (specialRegArr.contains(prov)) provStr = prov + "特别行政区"
            else provStr = prov + "省"
        } else provStr = prov
        return provStr
    }

    //处理市
    fun handleXYCompCity(city: String): String {
        var cityStr = ""
        if (city.isNotBlank() && city.indexOf("市") == -1) {
            if (city.indexOf("自治州") >= 0) cityStr = city
            else if (city.indexOf("地区") >= 0) cityStr = city.substring(0, city.indexOf("地区")) + "市"
            else cityStr = city + "市"
        } else cityStr = city
        return cityStr
    }

    //处理城市名
    fun handleRegion(region: String): String {
        var regionStr = ""
        if (region.indexOf("市") > 0) {
            regionStr = region.substring(0, region.length - 1)
        } else regionStr = region
        return regionStr
    }

    // 保存异常
    @Async
    fun errLogSave(content: String) {
        val errLog = ErrorLog()
        errLog.content = content
        errLogRepo.save(errLog)
        log.info("异常保存成功")
    }

    //下载excel模板
    fun downLoadExcel(resp: HttpServletResponse, excelName: String) {
        try {
            log.info(">>>start downLoadExcel")
            val file = ResourceUtils.getFile("classpath:temp/$excelName")
            if (file.exists()) {
                val inStream = FileInputStream(file)
                resp.setContentType("multipart/form-data")//自动判断下载文件类型
                resp.setHeader("Content-Disposition", "attachment;filename=${URLEncoder.encode(excelName, "UTF-8")}")
                val out = resp.outputStream
                var b = 0
                val buffer = ByteArray(100000)
                while (b != -1) {
                    b = inStream.read(buffer);
                    if (b != -1) out.write(buffer, 0, b)
                }
                inStream.close();
                out.flush();
                out.close();
                log.info(">>>end downLoadExcel")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 分页
    fun selectPageSql(currentPage: Int, pageSize: Int, queryStr: String): String {
        val start = currentPage * pageSize
        val end = (currentPage + 1) * pageSize + 1
        return "select * from (select rst.*,rownum rn from ($queryStr) rst where rownum < $end) where rn > $start"
    }

    fun getToken(): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd").format(Date())
        return sha1("${dateStr}zhdcrm")
    }

    //获取数据权限,都为null表示公司级别
    fun getDataLevel(uid: Long): Map<String, String?> {
        val uAcct = acctRepo.findOne(uid)
        val acctId: String? = if ("业务员".equals(uAcct.dataLevel)) uAcct.id.toString() else null
        val dptId: String? = if ("部门".equals(uAcct.dataLevel)) uAcct.fkDpt.id.toString() else null
        val orgId: String? = if ("机构".equals(uAcct.dataLevel)) uAcct.fkDpt.fkOrg.id.toString() else null
        log.info(">>>acctId:$acctId,dptId:$dptId,orgId:$orgId")
        return mapOf("acctId" to acctId, "dptId" to dptId, "orgId" to orgId)
    }

    //生成短信验证码
    fun verifyCode() = (Random().nextInt(899999) + 100000).toString()
}