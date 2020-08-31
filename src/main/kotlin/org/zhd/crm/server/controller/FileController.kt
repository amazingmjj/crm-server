package org.zhd.crm.server.controller

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.google.gson.JsonObject
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.model.crm.Dictionary
import org.zhd.crm.server.model.statistic.GoodStock
import org.zhd.crm.server.repository.crm.OutLinkerRepository
import org.zhd.crm.server.repository.statistic.GoodStockRepository
import org.zhd.crm.server.service.QiniuService
import org.zhd.crm.server.service.crm.*
import org.zhd.crm.server.util.CommUtil
import org.zhd.crm.server.util.CrmConstants
import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.net.URLEncoder
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional

@RestController
@RequestMapping("file")
class FileController {
    private val log = LoggerFactory.getLogger(FileController::class.java)

    @Autowired
    private lateinit var qiniuService: QiniuService
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var settingService: SettingService
    @Autowired
    private lateinit var outLinkRepo: OutLinkerRepository
    @Autowired
    private lateinit var customerService: CustomerService
    @Autowired
    private lateinit var basicDataService: BasicDataService
    @Autowired
    protected lateinit var callCenterService: CallCenterService
    @Autowired
    private lateinit var salesManageService: SalesManageService
    @Autowired
    private lateinit var stockRepo: GoodStockRepository

    @Value("\${download.template.xls.sms_link}")
    private var smsLinkTemplate: String = ""
    private var crmStockReport: String = "crm_stockReport.xls"

    @Value("\${spring.profiles.active}")
    private var currentPro = ""

    private fun getManager(model: String = "crm") = when (model) {
        "crm" -> qiniuService.getBucketManager(QiniuService.crmConfig.accessKey, QiniuService.crmConfig.secretKey)
        else -> qiniuService.getBucketManager(QiniuService.crmConfig.accessKey, QiniuService.crmConfig.secretKey)
    }

    private fun getUploadToken(model: String = "crm") = when (model) {
        "crm" -> qiniuService.getUploadToken(QiniuService.crmConfig.accessKey, QiniuService.crmConfig.secretKey, QiniuService.crmConfig.buckeName)
        else -> qiniuService.getUploadToken(QiniuService.crmConfig.accessKey, QiniuService.crmConfig.secretKey, QiniuService.crmConfig.buckeName)
    }

    private fun getBasicUrl(model: String = "crm") = when (model) {
        "crm" -> QiniuService.crmConfig.outLink
        else -> QiniuService.crmConfig.outLink
    }

    private fun getBucketName(model: String = "crm") = when (model) {
        "crm" -> QiniuService.crmConfig.buckeName
        else -> QiniuService.crmConfig.buckeName
    }

    @PostMapping("/uedit/del")
    fun delFile(fileName: String): Map<String, Any> {
        var result = HashMap<String, Any>()
        qiniuService.delFile(fileName, getManager(), getBucketName(), getBasicUrl())
        result.put("returnCode", 0)
        return result
    }

    @PostMapping("/uedit/upload")
    fun postFile(action: String, upfile: MultipartFile, req: HttpServletRequest): Map<String, Any> {
        log.info("upload file:>>>action: $action; files length:>>${upfile.size}")
        var result = HashMap<String, Any>()
        try {
            val fileName = qiniuService.generatorFileName(action)
            log.info("fileName:>>>$fileName")
            // 压缩图片
            if (action.equals("uploadvideo")) {
                qiniuService.uploadFile(fileName, upfile.bytes, getUploadToken())
                result.put("size", upfile.size)
                result.put("type", upfile.contentType)

            } else if (action.equals("crmExcelFile")) {
                // 批量导入数据,excel统一使用文本类型
                crmExcelHandle(upfile, req)
            } else {
                commUtil.gzipSize(upfile.inputStream, upfile.contentType)
                val upZipFile = commUtil.getTempImgFile()
                log.info("zip file size:>>${upZipFile.length()}")
                if (upZipFile.length() > upfile.size) {
                    qiniuService.uploadFile(fileName, upfile.bytes, getUploadToken())
                    result.put("size", upfile.size)
                    result.put("type", upfile.contentType)
                } else {
                    qiniuService.uploadFile(fileName, upZipFile, getUploadToken())
                    result.put("size", upZipFile.length())
                    result.put("type", upfile.contentType)
                }
            }
            result.put("original", upfile.originalFilename)
            result.put("name", fileName)
            result.put("url", getBasicUrl() + "/" + fileName)
            result.put("state", "SUCCESS")
        } catch (e: Exception) {
            log.error("uedit upload error:>>", e)
            result.put("state", "上传失败:${e.message!!}")
        }
        return result
    }

    /**
     * 批量更新或新增地址
     * @author samy
     * @date 2020/06/06
     */
    @PostMapping("address")
    fun saveOrUpdateAddress(fileName: String) {
        val result = StringBuffer()
        val filePrefix = "/Users/juny/Downloads/"
        val f = FileReader(filePrefix + fileName)
        val br = BufferedReader(f)
        br.use {
            r ->
            val temp = r.readLine()
            if (r != null) result.append(temp)
        }
        log.info("result:>>${result.toString()}")
        br.close()
        f.close()
//        val jsonArr = JSONArray.parseArray(result.toString())
//        val obj = JSONObject.parseObject(jsonArr[0].toString())
        saveAddress(result.toString())
    }

    private fun saveAddress(jsonStr: String) {
        val jsonArr = JSONArray.parseArray(jsonStr)
        jsonArr.map { s ->
            val obj =JSONObject.parseObject(s.toString())
            if (obj["code"].toString() != null) {
                var addr = settingService.addressFindByCode(obj["code"].toString())
                if (addr == null) {
                    addr = Address()
                    addr!!.code = obj["code"].toString()
                }
                addr.name = obj["name"].toString()
                addr.parentCode = obj["parent"].toString()
                addr.type = obj["type"].toString().toInt()
                settingService.addressSaveOrUpdate(addr)
                if (obj["children"] != null) saveAddress(obj["children"].toString())
            } else {
                log.info("address obj error:>>$s")
            }
        }
    }

    //返回download url
    @GetMapping("address/{id}")
    fun address(@PathVariable("id") id: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        var url = ""
        val addr = if (currentPro == "prod") "${CrmConstants.CRM_BACKGROUND_ADDR}${req.contextPath}" else "${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"
        when (id) {
            1 -> url = "$addr/file/download/1"
            2 -> url = "$addr/file/download/2"
        }
        log.info(">>>$url")
        val encodeUrl = URLEncoder.encode(url, "utf-8")
        result["returnCode"] = 0
        result["addr"] = encodeUrl
        return result
    }

    //返回download url
    @PostMapping("export/excel")
    fun exportFileForPost(mark: Int, req: HttpServletRequest): String {
        when (mark) {
            1 -> return salesManageService.exportStockReport(req, crmStockReport)
            else -> return ""
        }
    }

    //下载模板
    @GetMapping("download/{id}")
    fun downloadExcel(@PathVariable("id") id: Int, resp: HttpServletResponse): String {
        when (id) {
            1 -> commUtil.downLoadExcel(resp, smsLinkTemplate)
            2 -> callCenterService.exportOutLinker(resp, "outLinker.xls")
            3 -> commUtil.downLoadExcel(resp, crmStockReport)
            else -> {
            }
        }
        return "success"
    }

    @GetMapping("/currentProfile")
    fun getCurrentProfile(): String {
        return currentPro
    }

    @PostMapping("/uploadFile") //前台调用正式环境的接口
    fun uploadTaskFile(upfile: MultipartFile, currentProfile: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        //获取时间
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val dateStr = sdf.format(Date())
        val ym = dateStr.substring(0, 6)
        val dhms = dateStr.substring(6)
        //处理文件路径
        val basePath = "/app/download/$currentProfile/"
        val addr = "${CrmConstants.CRM_FOREGROUND_ADDR}/$currentProfile/" //TODO 测试地址使用哪一个
        val path = "$basePath$ym/"
        val targetFile = File(path)
        if (!targetFile.exists()) {
            log.info(">>>${path}路径不存在,新建文件夹")
            targetFile.mkdirs()
        }
        val fileName = upfile.originalFilename
        val uniqueName = "${dhms}_$fileName"
        val url = "$addr$ym/$uniqueName" //图片地址
        log.info(">>>path:$path,fileName:$fileName,uniqueName:$uniqueName,url:$url")
        try {
            val out = FileOutputStream(path + uniqueName)
            out.write(upfile.bytes)
            out.flush()
            out.close()
            result["fileName"] = fileName
            result["uniqueName"] = uniqueName
            result["url"] = url
            result["returnCode"] = 0
        } catch (e: IOException) {
            e.printStackTrace()
            result["returnCode"] = -1
            result["errMsg"] = "IO异常"
        }
        return result
    }

    @Transactional(rollbackOn = arrayOf(Exception::class, RuntimeException::class))
    private fun crmExcelHandle(upfile: MultipartFile, req: HttpServletRequest) {
        val dataType = req.getParameter("dataType")
        val sheetIdx = if (req.getParameter("sheetIdx") == null) 0 else Integer.parseInt(req.getParameter("sheetIdx"))
        log.info("dataType:>>> $dataType")
        val workbook = HSSFWorkbook(POIFSFileSystem(upfile.inputStream))
        val sheet = workbook.getSheetAt(sheetIdx)
        log.info("sheet rows: ${sheet.physicalNumberOfRows}")
        val originFormat = SimpleDateFormat("yyyy/MM/dd")
        val transFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val xyFormat = SimpleDateFormat("dd-MM月-yy")
        (1..sheet.lastRowNum).map { rIdx ->
            log.info("row index:>>>$rIdx")
            val row = sheet.getRow(rIdx)
            log.info("column count: ${row.physicalNumberOfCells}")
            log.info("row lastCellNum:>>${row.lastCellNum}")
            when (dataType) {
                "dpt" -> {
                    var dept = Dpt()
                    (0..(row.lastCellNum)).map { cIdx ->
                        log.info("column index:>>>$cIdx")
                        val cell = row.getCell(cIdx)
                        if (cIdx == 0) {
                            val temp = settingService.dptFindByName(cell.stringCellValue)
                            dept = if (temp != null) temp else dept
                            if (temp == null) dept.name = cell.stringCellValue
                        }
                        if (cIdx == 1 && cell != null) {
                            dept.leader = cell.stringCellValue
                        }
                        if (cIdx == 2) {
                            log.info(cell.stringCellValue)
                            dept.fkOrg = settingService.orgFindByName(cell.stringCellValue)!!
                        }
                    }
                    settingService.dptSave(dept)
                }
                "acct" -> {
                    try {
                        val acctFileds = arrayOf("name", "fkDpt", "phone", "status", "dataLevel", "sex", "national", "position", "edu", "professional", "email", "jobTitle", "telephone", "addr", "nativePlace", "maritalStatus", "politicalLandscape", "birthday", "inTime", "workGroup", "remark", "avatar", "platformCode")
                        var acct: Account? = null
                        (0..(row.lastCellNum)).map { cIdx ->
                            val cell = row.getCell(cIdx)
                            log.info("cidx:>>>$cIdx")
                            if (cIdx == 0) {
                                val name = cell.stringCellValue
                                val erpCode = row.getCell(22).stringCellValue
                                acct = settingService.findByPlatformCode(erpCode)
//                                acct = acctService.findByLoginAcct(name)
                                if (acct == null) acct = Account()
                                acct!!.loginAcct = "crm$rIdx"
                                acct!!.name = name
                            } else if (cIdx == 1) {
                                acct!!.fkDpt = settingService.dptFindByName(cell.stringCellValue)!!
                            } else if (cIdx == 2) {
                                acct!!.phone = if (cell != null && !cell.stringCellValue.isNullOrBlank()) cell.stringCellValue else " "
                            } else if (cIdx == 3) {
                                val status = if (cell.stringCellValue == "1") 0 else 1
                                acct!!.status = status
                            } else if (cIdx == 4) {
                                var devel = "业务员"
                                if (cell != null) devel = when (cell.stringCellValue) {
                                    "0" -> "业务员"
                                    "1" -> "部门"
                                    "2" -> "公司"
                                    else -> "业务员"
                                }
                                acct!!.dataLevel = devel
                            } else if (cIdx == 5) {
                                acct!!.sex = if (cell.stringCellValue.equals("男")) 1 else 2
                            } else if (cIdx == 15) {
                                if (cell != null) {
                                    val mstatus = when (cell.stringCellValue) {
                                        "1" -> "未婚"
                                        "2" -> "已婚"
                                        "3" -> "离婚"
                                        "4" -> "丧偶"
                                        else -> "保密"
                                    }
                                    acct!!.maritalStatus = mstatus
                                }
                            } else if (cIdx == 17) {
                                val birthday = ""
                                if (cell != null && cell.stringCellValue.isNotEmpty()) transFormat.format(originFormat.parse(cell.stringCellValue))
                                acct!!.birthday = birthday
                            } else if (cIdx == 18) {
                                val inTime = ""
                                if (cell != null && cell.stringCellValue.isNotEmpty()) transFormat.format(originFormat.parse(cell.stringCellValue))
                                acct!!.inTime = inTime
                            } else if (cIdx == 21) {
                                //avatar 不处理
                            } else {
                                if (acct != null && cIdx < acctFileds.size) {
                                    log.info("set field name:>>${acctFileds[cIdx]}; column index:>>$cIdx")
                                    val m = acct!!.javaClass.getMethod("set${acctFileds[cIdx].substring(0, 1).toUpperCase()}${acctFileds[cIdx].substring(1)}", String::class.java)
                                    if (cell != null) m.invoke(acct, if (cell.stringCellValue.equals("大专")) "专科" else cell.stringCellValue) else m.invoke(acct, " ")
                                }
                            }
                        }
                        if (acct != null) {
                            //配置权限
                            val role = if (acct!!.fkDpt.name == "行政人资") settingService.roleFindById(CrmConstants.ROLE_HR_ID) else settingService.roleFindById(CrmConstants.DEFAULT_ROLE_ID)//角色初始化数据,7为业务员
                            val auths = HashSet<Auth>()
                            role.auths.map { ah ->
                                auths.add(ah)
                            }

                            acct!!.customers = HashSet<Customer>()
                            acct!!.fkRole = role
                            acct!!.auths = auths
                            settingService.acctSave(acct!!)
                        } else {
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "custm" -> {
                    try {
                        val custmFields = arrayOf("status", "compName", "compNameAb", "memberCode", "createAt", "erpCode", "ebusiMemberCode", "ebusiAdminAcctNo", "busiRelation", "fkCustomProperty", "fkDpt", "fkAcct", "busiLicenseCode", "registerCapital", "legalRept", "compLogoUrl", "compAddr", "faxNum", "compSize", "compType", "region", "setUpDate", "factController", "factControllerIdno", "mainlink", "mainPhone", "tfn", "openAcctName", "openBank", "openAcct", "billAddr")
                        val busiArr = arrayOf("供应商", "客户", "内部单位", "货主", "费用单位", "质押单位")
                        var custm: Customer? = null
                        val acct = settingService.acctFindById(CrmConstants.DEFAULT_ACCT_ID)
                        val cproperty = basicDataService.findCustomPropertyByName(CrmConstants.DEFAULT_CUSTOM_PROPERTY)
                        val dpt = settingService.dptFindById(CrmConstants.DEFAULT_DPT_ID)
                        (0..(row.lastCellNum)).map { cIdx ->
                            val cell = row.getCell(cIdx)
                            if (cIdx == 0 || cIdx == 24 || cIdx == 25) {
                            } else if (cIdx == 1) {
                                val erpCode = row.getCell(5).stringCellValue
                                if (erpCode.isBlank()) custm = customerService.findCustomerByCompName(cell.stringCellValue)
                                else custm = customerService.findCstmByErpCode(erpCode)
                                if (custm == null) {
                                    custm = Customer()
                                    // 主联系人
                                    val linkers = HashSet<Linker>()
                                    val link = Linker()
                                    link.name = if (row.getCell(24) == null) CrmConstants.DEFAULT_LINKER_NAME else row.getCell(24).stringCellValue
                                    link.phone = if (row.getCell(25) == null) CrmConstants.DEFAULT_LINKER_PHONR else row.getCell(25).stringCellValue
                                    link.creator = acct
                                    link.mainStatus = 1
                                    linkers.add(link)
                                    custm!!.linkers = linkers
                                }
                                custm!!.compName = cell.stringCellValue
                                val status = row.getCell(0).stringCellValue.toInt()
                                custm!!.status = if (status == 0) 1 else 0
                            } else if (cIdx == 2) {
                                if (cell != null && cell.stringCellValue.isNotEmpty()) custm!!.compNameAb = cell.stringCellValue else custm!!.compNameAb = ""
                            } else if (cIdx == 3) {
                                if (cell != null && cell.stringCellValue.isNotEmpty()) custm!!.memberCode = cell.stringCellValue else custm!!.memberCode = ""
                            } else if (cIdx == 4) {
                                custm!!.createAt = Timestamp(timeFormat.parse(cell.stringCellValue).time)
                            } else if (cIdx == 8) {
                                // 业务关系
                                val busiRelation = cell.stringCellValue
                                val busiSet = HashSet<BusiRelation>()
                                busiRelation.indices.map { i ->
                                    val value = busiRelation[i]
                                    if (value.equals('1')) {
                                        val tempName = busiArr[i]
                                        busiSet.add(basicDataService.findBusiRelationByName(tempName))
                                    }
                                }
                                custm!!.busiRelation = busiSet
                            } else if (cIdx == 9) {
                                // 客户性质
                                if (cell != null && cell.stringCellValue.isNotEmpty()) {
                                    custm!!.fkCustomProperty = basicDataService.findCustomPropertyByName(cell.stringCellValue)
                                } else {
                                    custm!!.fkCustomProperty = cproperty
                                }
                            } else if (cIdx == 10) {
                                // 部门
                                if (cell != null && cell.stringCellValue.isNotEmpty()) {
                                    custm!!.fkDpt = settingService.dptFindByName(cell.stringCellValue)!!
                                } else {
                                    custm!!.fkDpt = dpt
                                }
                            } else if (cIdx == 11) {
                                // 账号
                                if (cell != null && cell.stringCellValue.isNotEmpty()) {
                                    val account = settingService.findByPlatformCode(cell.stringCellValue)
                                    custm!!.fkAcct = if (account == null) acct else account
                                } else {
                                    custm!!.fkAcct = acct
                                }
                            } else {
                                if (custm != null && cIdx < custmFields.size) {
                                    val fieldName = custmFields[cIdx]
                                    val excludeNames = arrayOf("registerCapital", "setUpDate")
                                    if (!excludeNames.contains(fieldName)) {
                                        val m = custm!!.javaClass.getMethod("set${fieldName.substring(0, 1).toUpperCase()}${fieldName.substring(1)}", String::class.java)
                                        m.invoke(custm, if (cell != null && cell.stringCellValue.isNotEmpty()) cell.stringCellValue else null)
                                    }
                                }
                            }
                        }
                        if (custm != null) {
                            custm!!.createAcct = acct
                            custm!!.procurementGoods = HashSet<SupplyCatalog>()
                            custm!!.procurementPurpose = HashSet<Purpose>()
                            custm!!.hopeAddGoods = HashSet<SupplyCatalog>()
                            custm!!.dealGoods = HashSet<SupplyCatalog>()
                            custm!!.dealPurpose = HashSet<Purpose>()
                            custm!!.processRequirement = HashSet<ProcessRequirement>()
                            custm!!.mark = 2
                            custm!!.customerSource = "ERP"
                            custm!!.convertDate = custm!!.createAt
                            custm!!.billDate = custm!!.createAt
                            custm!!.publicCompName = custm!!.compName
                            customerService.customerSave(custm!!)
                            if (custm!!.id!! > 0) {
                                var bankInfo = customerService.findMainBankInfo(custm!!.id)
                                if (bankInfo == null) {
                                    bankInfo = BankInfo()
                                }
                                bankInfo.name = if (custm?.openAcctName == null) "" else custm!!.openAcctName!!
                                bankInfo.openBank = if (custm?.openBank == null) "" else custm!!.openBank!!
                                bankInfo.bankAcct = if (custm?.openAcct == null) "" else custm!!.openAcct!!
                                bankInfo.remark = ""
                                bankInfo.mainAcct = 1
                                customerService.bankInfoSave(bankInfo, custm!!.id!!, 1)
                            } else {
                            }
                        } else {
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "xycustm" -> {
                    try {
                        val custmFields = arrayOf("status", "compName", "compNameAb", "memberCode", "createAt", "erpCode", "ebusiMemberCode", "ebusiAdminAcctNo", "fkCustomProperty", "fkDpt", "fkAcct", "busiLicenseCode", "registerCapital", "legalRept", "compAddr", "compProv", "compCity", "compArea", "faxNum", "compSize", "compType", "region", "setUpDate", "factController", "factControllerIdno", "tfn", "openAcctName", "openBank", "openAcct", "billAddr", "billDate")
                        var custm: Customer? = null
                        val acct = settingService.acctFindById(CrmConstants.DEFAULT_ACCT_ID)
                        val cproperty = basicDataService.findCustomPropertyByName(CrmConstants.DEFAULT_CUSTOM_PROPERTY)
                        val excludeIdx = arrayOf(0, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        val dpt = settingService.dptFindById(CrmConstants.DEFAULT_DPT_ID)
                        val link = customerService.linkFindById(CrmConstants.DEFAULT_LINKER_ID)!!
                        link.mainStatus = 1
                        link.creator = acct
                        var breakMark = false
                        for (cIdx in 0..(row.lastCellNum)) {
                            val cell = row.getCell(cIdx)
                            if (excludeIdx.contains(cIdx)) {
                                log.info("exclude index:>>${cIdx}")
                            } else if (cIdx == 1) {
                                if (cell == null || cell.stringCellValue.isNullOrBlank()) {
                                    log.info(">>>客户名称为空")
                                    breakMark = true
                                } else {
                                    val acctNoCell = row.getCell(7)
                                    if (acctNoCell != null) {
                                        custm = customerService.findCustomerByEbusiAdminAcctNo(acctNoCell.stringCellValue)
                                        if (custm == null) {
                                            log.info(">>>不存在对应客户")
                                            breakMark = true
                                        } else if (custm!!.erpCode.isNullOrBlank()) {
                                            log.info(">>>不存在对应erpCode")
                                            breakMark = true
                                        } else {
                                        }
                                    } else {
                                        log.info(">>>xy编号为空")
                                        breakMark = true
                                    }
                                }
                                if (breakMark) {
                                    break
                                }
                                if (custm != null) {
                                    if (row.getCell(6) != null) custm!!.ebusiMemberCode = row.getCell(6).stringCellValue
                                    if (row.getCell(7) != null) custm!!.ebusiAdminAcctNo = row.getCell(7).stringCellValue
                                    val custmStatus = row.getCell(0)
                                    if (custmStatus != null) {
                                        when (custmStatus.stringCellValue.toInt()) {
                                            1 -> custm!!.mark = 2
                                            else -> custm!!.mark = 1
                                        }
                                    }
                                }
                            } else if (cIdx == 12) {
                                if (cell != null && cell.stringCellValue != "") custm!!.registerCapital = cell.stringCellValue
                            } else if (cIdx == 15) {
                                if (cell != null) custm!!.compProv = commUtil.handleXYCompProv(cell.stringCellValue)
                            } else if (cIdx == 16) {
                                if (cell != null) custm!!.compCity = commUtil.handleXYCompCity(cell.stringCellValue)
                            } else if (cIdx == 22) {
                                if (cell != null && cell.stringCellValue != "") custm!!.setUpDate = Timestamp(transFormat.parse(cell.stringCellValue).time)
                            } else if (cIdx == 30) {
                                if (cell != null && cell.stringCellValue != "") {
                                    val billDateStr = (cell.stringCellValue.split("-").map { s -> s.trim() }).joinToString("-")
                                    custm!!.billDate = Timestamp(timeFormat.parse(billDateStr).time)
                                }
                            } else {
                                if (custm != null && cIdx < custmFields.size) {
                                    val fieldName = custmFields[cIdx]
                                    val m = custm!!.javaClass.getMethod("set${fieldName.substring(0, 1).toUpperCase()}${fieldName.substring(1)}", String::class.java)
                                    m.invoke(custm, if (cell != null) cell.stringCellValue else null)
                                }
                            }
                        }
                        if (!breakMark && custm != null && custm!!.mark == 2) {
                            if (custm?.id != null) {
                                var bankInfo = customerService.findMainBankInfo(custm!!.id)
                                if (bankInfo == null) {
                                    bankInfo = BankInfo()
                                }
                                bankInfo.name = if (custm?.openAcctName == null) " " else custm!!.openAcctName!!
                                bankInfo.openBank = if (custm?.openBank == null) " " else custm!!.openBank!!
                                bankInfo.bankAcct = if (custm?.openAcct == null) " " else custm!!.openAcct!!
                                if (bankInfo.remark == null) bankInfo.remark = " "
                                if (bankInfo.id == null) bankInfo.mainAcct = 1
                                log.info("custm id:>>${custm!!.id!!}")
                                customerService.bankInfoSave(bankInfo, custm!!.id!!, 1)
                                customerService.customerSave(custm!!)
                            } else {
                                val busiRelation = HashSet<BusiRelation>()
                                busiRelation.add(basicDataService.findBusiRelationById(CrmConstants.DEFAULT_BUSI_RELATION_ID))//默认客户
                                custm!!.busiRelation = busiRelation
                                custm!!.createAcct = acct
                                custm!!.procurementGoods = HashSet<SupplyCatalog>()
                                custm!!.procurementPurpose = HashSet<Purpose>()
                                custm!!.hopeAddGoods = HashSet<SupplyCatalog>()
                                custm!!.dealGoods = HashSet<SupplyCatalog>()
                                custm!!.dealPurpose = HashSet<Purpose>()
                                custm!!.processRequirement = HashSet<ProcessRequirement>()
                                custm!!.industry = HashSet<Industry>()
                                custm!!.fkDpt = dpt
                                custm!!.fkAcct = acct
                                custm!!.fkCustomProperty = cproperty
                                custm!!.convertDate = custm!!.createAt //转化日期取创建日期
                                if (custm!!.ebusiMemberCode != null) custm!!.xyCode = custm!!.ebusiMemberCode!!.toInt().toString()
                                custm!!.publicCompName = custm!!.compName
                                val linkSet = HashSet<Linker>()
                                linkSet.add(link)
                                custm!!.linkers = linkSet
                                customerService.customerSave(custm!!)
                                if (custm!!.id!! > 0) {
                                    val binfo = BankInfo()
                                    binfo.name = if (custm?.openAcctName == null) " " else custm!!.openAcctName!!
                                    binfo.openBank = if (custm?.openBank == null) " " else custm!!.openBank!!
                                    binfo.bankAcct = if (custm?.openAcct == null) " " else custm!!.openAcct!!
                                    binfo.mainAcct = 1
                                    customerService.bankInfoSave(binfo, custm!!.id!!, 1)
                                } else {
                                }
                            }
                        } else {
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "taskPlanning" -> {
                    try {
                        val taskFields = arrayOf("Year", "Month", "Type", "OrgName", "DptName", "AcctName", "OnlineTask", "OfflineTask", "BoardTask", "AmountTask", "HighValueTask", "FirstCustNum", "SecondCustNum")
                        val doubleIdx = arrayOf(6, 7, 8, 9, 10)
                        val intIdx = arrayOf(0, 1, 11, 12)
                        val tempCell = row.getCell(2)//第三格
                        val tempType = tempCell.stringCellValue
                        if (!tempType.isNullOrBlank()) { //整行没有值不需要处理
                            val task = TaskPlanning()
                            (0..(row.lastCellNum)).map { cIdx ->
                                log.info("column index:>>>$cIdx")
                                val cell = row.getCell(cIdx)
                                log.info("cell:>>>$cell")
                                if (cell != null && cIdx < taskFields.size) {
                                    val fieldName = taskFields[cIdx]
                                    log.info("fieldName:>>>$fieldName")
                                    if (cIdx == 2) {
                                        val type = cell.stringCellValue
                                        if (type == "公司") task.type = 0 else if (type == "机构") task.type = 1 else if (type == "部门") task.type = 2 else task.type = 3
                                    } else if (intIdx.contains(cIdx)) {
                                        val m = task.javaClass.getMethod("set$fieldName", Int::class.java)
                                        m.invoke(task, if (cell.stringCellValue.isNullOrBlank()) 0 else cell.stringCellValue.toInt())
                                    } else if (doubleIdx.contains(cIdx)) {
                                        val m = task.javaClass.getMethod("set$fieldName", Double::class.java)
                                        m.invoke(task, if (cell.stringCellValue.isNullOrBlank()) 0 else cell.stringCellValue.toDouble())
                                    } else {
                                        val m = task.javaClass.getMethod("set$fieldName", String::class.java)
                                        m.invoke(task, cell.stringCellValue)
                                    }
                                }
                            }
                            var status = true
                            task.compName = "江苏智恒达投资集团"
                            if (!task.dptName.isNullOrBlank()) {//表格中存在该值
                                val dpt = settingService.dptFindByName(task.dptName!!)
                                task.dptCode = if (dpt != null) dpt.id.toString() else null
                            }
                            if (!task.acctName.isNullOrBlank()) {//表格中存在该值
                                val acct = settingService.acctFindByName(task.acctName!!)
                                //数据库不存在该业务员则不保存
                                if (acct != null) task.acctCode = acct.platformCode
                                else {
                                    status = false
                                    log.info(">>>业务员[${task.acctName}]不存在")
                                }
                            }
                            //新增时查重
                            val compName = if (task.type == 0) task.compName else null
                            val orgName = if (task.type == 1) task.orgName else null
                            val dptName = if (task.type == 2) task.dptName else null
                            val acctCode = if (task.type == 3) task.acctCode else null
                            val count = settingService.taskCountRepeats(task.year, task.month, task.type, compName, orgName, dptName, acctCode)
                            if (count > 0) status = false
                            //保存
                            if (status) settingService.taskSave(task) else {
                            }
                        } else {
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "outLinker" -> {
                    var ol: OutLinker? = null
                    (0..(row.lastCellNum)).map { cIdx ->
                        val cell = row.getCell(cIdx)
                        if (cIdx == 1 && cell != null && !cell.stringCellValue.isNullOrBlank()) {
                            log.info("outLinker phone:>>>${cell.stringCellValue}")
                            val temp = outLinkRepo.findByPhone(cell.stringCellValue)
                            val name = if (row.getCell(0) == null) "" else row.getCell(0).stringCellValue
                            val label = if (row.getCell(2) == null) "" else row.getCell(2).stringCellValue
                            val remark = if (row.getCell(3) == null) "" else row.getCell(3).stringCellValue
                            if (temp != null) ol = temp else ol = OutLinker()
                            ol!!.phone = cell.stringCellValue
                            ol!!.name = name
                            ol!!.label = label
                            ol!!.remark = remark
                            ol!!.creator = settingService.acctFindById(CrmConstants.DEFAULT_ACCT_ID)
                        }
                    }
                    if (ol != null) {
                        outLinkRepo.save(ol)
                        log.info(">>>save outLinker successed")
                    } else {

                    }

                }
                "goodsStock" -> {//保留批量导入功能和crm_goods_stock表,目前由dubbo接口代替
                    try {
                        log.info(">>>goodStock 开始处理excel第${rIdx}行数据")
                        var goodStock: GoodStock? = null
                        val floatArr = arrayOf(11, 12, 16, 17, 19, 20)
                        val doubleArr = arrayOf(14, 15)
                        val intArr = arrayOf(9, 13, 18)
                        val stockFields = arrayOf("PartsName", "Material", "GoodsSpec", "ProductArea", "Tolerance", "Length", "WeightRange", "ToleranceRange", "WarehouseName", "Count", "CalcWay", "PoundPrice", "CalcPrice", "GoodsSupplyNum", "GoodsSupplyWeight", "GoodsSupplyAssistWeight", "NegoPoundPrice", "NegoCalcPrice", "GoodsNum", "GoodsWeight", "GoodsCalcWeight", "SumGoodsBatch")
                        (0..(row.lastCellNum)).map { cIdx ->
                            log.info(">>>goodStock column index:$cIdx")
                            if (cIdx == 0) {
                                val sumGoodsBatch = row.getCell(21).stringCellValue
                                val goodsCheck = salesManageService.findUniqueGoods(sumGoodsBatch)
                                if (goodsCheck != null) {
                                    log.info(">>>goodStock 存在物资,更新")
                                    goodStock = goodsCheck
                                } else {
                                    log.info(">>>goodStock 不存在物资,新增")
                                    goodStock = GoodStock()
                                }
                            }
                            val cell = row.getCell(cIdx)
                            if (cell != null && cIdx < stockFields.size) {
                                val fieldName = stockFields[cIdx]
                                val valueStr = cell.stringCellValue
                                if (cIdx in floatArr) {
                                    // 在定义时Float? = null识别为包装类的Float,而kotlin的Float对应的是基本类型float,所以这边class要使用java.lang中的包装类
                                    val m = goodStock!!.javaClass.getMethod("set$fieldName", java.lang.Float::class.java)
                                    m.invoke(goodStock, if (valueStr.isNullOrEmpty()) null else valueStr.toFloat())
                                } else if (cIdx in doubleArr) {
                                    val m = goodStock!!.javaClass.getMethod("set$fieldName", java.lang.Double::class.java)
                                    m.invoke(goodStock, if (valueStr.isNullOrEmpty()) null else valueStr.toDouble())
                                } else if (cIdx in intArr) {
                                    val m = goodStock!!.javaClass.getMethod("set$fieldName", java.lang.Integer::class.java)
                                    m.invoke(goodStock, if (valueStr.isNullOrEmpty()) null else valueStr.toInt())
                                } else {//0,1,2,3,4,5,6,7,8,10
                                    val m = goodStock!!.javaClass.getMethod("set$fieldName", String::class.java)
                                    m.invoke(goodStock, valueStr)
                                }
                            }
                        }
                        if (goodStock != null) {
                            stockRepo.save(goodStock)
                            log.info(">>>goodStock 保存成功")
                        } else {
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "initialName" -> {
                    try {
                        log.info(">>>initialName 开始处理excel第${rIdx}行数据")
                        (0..(row.lastCellNum)).map { cIdx ->
                            if (cIdx == 2) {
                                val cell = row.getCell(2)
                                if (cell != null) {
                                    val name = cell.stringCellValue
                                    val acct = settingService.acctFindByName(name)
                                    if (acct == null) {
                                        log.info(">>>${name}没有该用户名")
                                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${name}没有该用户名")
                                    } else {
                                        val spell = commUtil.getInitialName(name)
                                        val loginAcctCount = settingService.loginAcctCount(spell)
                                        if (loginAcctCount > 0) {
                                            log.info(">>>登录名:${spell}已存在")
                                            commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：登录名:${spell}已存在")
                                        } else {
                                            acct.loginAcct = spell
                                            settingService.acctSave(acct)
                                            log.info(">>>用户名:$name,登录名:${spell}修改成功")
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        log.error("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.printStackTrace()}")
                        commUtil.errLogSave("批量导入${dataType}数据失败,行数：$rIdx,异常：${e.message}")
                    }
                }
                "erpBillDate" -> {
                    log.info(">>>erpBillDate 开始处理excel第${rIdx}行数据")
                    (0..(row.lastCellNum)).map { cIdx ->
                        if (cIdx == 1) {
                            val cell = row.getCell(1)
                            if (cell != null) {
                                val erpCode = cell.stringCellValue
                                val cstm = customerService.findCstmByErpCode(erpCode)
                                if (cstm != null) {
                                    val billDateStr = row.getCell(2).stringCellValue
                                    cstm.billDate = Timestamp(timeFormat.parse(billDateStr).time)
                                    if (cstm.mark == 3) cstm.mark = 2
//                                    cstm.billDate = cstm.createAt
                                    customerService.customerSave(cstm)
                                }
                            }
                        }
                    }
                }
                "xyLinker" -> {//修复xy和crm主联系人不一致数据
                    log.info(">>>xyLinker 开始处理excel第${rIdx}行数据")
                    (0..(row.lastCellNum)).map { cIdx ->
                        if (cIdx == 1){
                            val linkId = row.getCell(1).stringCellValue.toLong()
                            val link = customerService.linkFindById(linkId)
                            if (link != null && link.mainStatus == 1){
                                val cell = row.getCell(4)
                                val linkName = if (cell == null) "" else cell.stringCellValue
                                val linkPhone = row.getCell(5).stringCellValue
                                link.name = linkName
                                link.phone = linkPhone
                                customerService.linkerSave(link)
                            } else {
                                log.info(">>>失败id:$linkId")
                            }
                        }
                    }
                }
                "sinaGoods" -> {//批量导入新浪期货物资类型
                    val dict = Dictionary()
                    dict.type = 1
                    (0..(row.lastCellNum)).map { cIdx ->
                        log.info("column index:>>>$cIdx")
                        val cell = row.getCell(cIdx)
                        if (cIdx == 0) {
                            dict.name = cell.stringCellValue
                        } else if (cIdx == 1) {
                            dict.value = cell.stringCellValue
                        }
                    }
                    settingService.saveDict(dict)
                }
                "workgroup" -> {//批量修改工作组
                    (0..(row.lastCellNum)).map { cIdx ->
                        log.info("column index:>>>$cIdx")
                        if (cIdx == 0) {
                            val erpCode = row.getCell(0).stringCellValue
                            val cstm = customerService.findCstmByErpCode(erpCode)
                            if (cstm == null) {
                                log.info("找不到code为[$erpCode]的客户信息...")
                            } else {
                                val workgroup = row.getCell(7).stringCellValue
                                cstm.workgroupName = workgroup
                                customerService.customerSave(cstm)
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }
}

//fun main(args: Array<String>) {
//    var result = StringBuffer()
//    val f = FileReader("/Users/juny/Downloads/province_1.json")
//    val br = BufferedReader(f)
//    br.use {
//        r ->
//        val temp = r.readLine()
//        if (r != null) result.append(temp)
//    }
//    println("result:>>${result.toString()}")
//    val jsonArr = JSONArray.parseArray(result.toString())
//    val obj = JSONObject.parseObject(jsonArr[0].toString())
//    println("-------------------------------")
//    println("name:>>>${obj["name"]}")
//    println("children:>>${obj["children"]}")
//   var d1 =  "21-6月 -17"
//   var str = d1.split("-").map { s -> s.trim() }.joinToString ("-")
//    println(str)
//    val a = "1+2+(3-2.8)"
//    val mgr = ScriptEngineManager()
//    val engine = mgr.getEngineByName("JavaScript")
//    println(engine.eval(a))
//}