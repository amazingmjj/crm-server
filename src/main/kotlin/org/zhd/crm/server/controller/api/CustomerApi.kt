package org.zhd.crm.server.controller.api

import io.swagger.annotations.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.query.Param
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.Customer
import org.zhd.crm.server.model.crm.Linker
import org.zhd.crm.server.model.crm.WxLinker
import org.zhd.crm.server.model.projection.LinkProjection
import org.zhd.crm.server.util.CommUtil
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("api/v1/customer")
class CustomerApi() : CrmBaseController() {

    @ApiOperation(value = "型云获取客户联系人列表", notes = "")
    @ApiImplicitParam(name = "codes", value = "型云电商编号（多个值用逗号分割）", required = true)
    @GetMapping("linkers")
    fun cstmLinkers(codes: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        val codeArr = codes.split(",")
        map["list"] = customerService.findCstmLinkInBusiCode(codeArr)
        return map
    }

    @ApiOperation(value = "Erp获取客户联系人列表", notes = "")
    @ApiImplicitParam(name = "erpCodes", value = "ERP编号（多个值用逗号分割）", required = true)
    @GetMapping("erpLinkers")
    fun cstmLinkersErp(erpCodes: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        val codeArr = erpCodes.split(",")
        map["list"] = customerService.findCstmLinkInErpCode(codeArr)
        return map
    }

    @ApiOperation(value = "型云获取单位性质客户")
    @ApiImplicitParam(name = "codes", value = "型云电商编号（多个值用逗号分割）", required = true)
    @GetMapping("cstmUnitProperty")
    fun cstmUnitPropertyList(codes: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        val codeArr = codes.split(",")
        map["list"] = customerService.findUnitPropertyInEbusiMemberCode(codeArr)
        return map
    }

    @ApiOperation(value = "型云所有白条客户")
    @GetMapping("cstmIous")
    fun esCstmIousList(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["list"] = customerService.findEsIousCstms()
        return map
    }


    @PostMapping("linker")
    @ApiOperation(value = "联系人新增或修改")
    @ApiImplicitParam(name = "linker", value = "联系人对象", required = true, dataTypeClass = LinkProjection::class)
    fun saveOrUpdateLinkers(linker: LinkProjection): Map<String, Any> {
        val result = HashMap<String, Any>()
        if (linker.getLinkPhone() == null) {
            result["returnCode"] = -1
            result["errMsg"] = "联系人电话不能为空"
        } else if (linker.getCstmId() == null) {
            result["returnCode"] = -1
            result["errMsg"] = "客户id不能为空"
        } else if (linker.getLinkName() == null) {
            result["returnCode"] = -1
            result["errMsg"] = "联系人姓名不能为空"
        } else {
            val link = Linker()
            if (linker.getLinkId() != null) link.id = linker.getLinkId()
            link.name = linker.getLinkName()
            link.phone = linker.getLinkPhone()
            // 同一个客户联系人手机号不能重复
            val cstmLinkProjectList = customerService.findCstmLinkRepeat(linker.getLinkPhone(), linker.getCstmId())
            if (cstmLinkProjectList.count() == 0 || cstmLinkProjectList.first().getLinkId() == linker.getLinkId()) {
                var resp = true
                ///修改所属客户时,需要检查
                if (customerService.checkMainLinker(link, linker.getCstmId())) {
                    resp = false
                    result["errMsg"] = "主联系人个数不能超过1个"
                } else if (link.id != null) {
                    if (customerService.checkModifyPhone(link, linker.getCstmId())) {
                        resp = false
                        result["errMsg"] = "型云客户不能修改主联系人号码"
                    }
                }
                if (resp) {
                    customerService.linkerSave(1, link, linker.getCstmId(), null, "xy")
//                    result["obj"] = obj
                }
                result["returnCode"] = if (resp) 0 else -1
            } else {
                result["returnCode"] = -1
                result["errMsg"] = "同一个客户下联系人电话不能重复"
            }
        }
        return result
    }

    @PostMapping("updateInvoiceInfo")
    @ApiOperation("更新客户开票信息")
    @ApiImplicitParams(ApiImplicitParam(name = "erpCode", value = "Erp编号", required = true),
            ApiImplicitParam(name = "invoiceAddr", value = "开票地址", required = true),
            ApiImplicitParam(name = "openBank", value = "开户行", required = true),
            ApiImplicitParam(name = "tfn", value = "税号", required = true))
    fun updateCstmInvoiceInfo(erpCode: String, invoiceAddr: String, openBank: String, tfn: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val cstm = customerService.findCstmByErpCode(erpCode)
        if (cstm == null) {
            result["returnCode"] = -1
            result["errMsg"] = "客户不存在"
        } else {
            cstm.billAddr = invoiceAddr
            cstm.tfn = tfn
            cstm.openBank = openBank
            customerService.customerSave(cstm)
            customerService.crmSync2XyOrErp(cstm, false)
            result["returnCode"] = 0
        }
        return result
    }

    @PostMapping("settleDelayByCstmName")
    @ApiOperation("单位名称后结算状态")
    @ApiImplicitParams(ApiImplicitParam(name = "name", value = "客户单位名称(多个名称用逗号分割)", required = true))
    fun settleDelayByCstmName(name: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val nameList = name.split(",").toList()
        val count = customerService.settleCountByCompName(nameList)
        result["returnCode"] = 0
        result["settleStatus"] = count
        return result
    }

    @PostMapping("idCardNameValidate")
    @ApiOperation("身份名称验证")
    @ApiImplicitParams(ApiImplicitParam(name = "name", value = "业务员名称", required = true),
            ApiImplicitParam(name = "idCard", value = "业务员身份证号", required = true))
    fun idCardNameValidate(name: String, idCard: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val acct = settingService.acctFindByIdCard(idCard)
        if (acct == null) {
            result["returnCode"] = -1
            result["errMsg"] = "业务员不存在"
        } else if (acct.name != name) {
            result["returnCode"] = -1
            result["errMsg"] = "姓名与身份不匹配"
        } else {
            result["returnCode"] = 0
        }
        return result
    }

    /**
     * ERP或者型云的微信用户列表
     * @author samy
     * @date 2020/01/17
     */
    @GetMapping("wxUsers")
    @ApiOperation("ERP或者型云的微信用户列表")
    @ApiImplicitParams(
            ApiImplicitParam(name = "code", value = "唯一编号", required = true, type = "String"),
            ApiImplicitParam(name = "type", value = "code类型(1 erp 2 型云)", required = true, type = "int")
    )
    fun wxUserList(code: String, type: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        var cstm: Customer? = null
        when (type) {
            1 -> {
                cstm = customerService.findCstmByErpCode(code)
            }
            2 -> {
                cstm = customerService.findCstmByEbusiMemberCode(code)
            }
            else -> {
                result["returnCode"] = -1
                result["errMsg"] = "invalid type"
            }
        }
        if (cstm == null) {
            result["returnCode"] = -1
            result["errMsg"] = "invalid customer"
        } else {
            result["list"] = customerService.cstmWxLinkers(cstm.id!!, cstm.fkAcct.id!!)
        }
        return result
    }


    /**
     * 绑定微信授权客户联系人
     * @author samy
     * @date 2020/03/27
     */

    @ApiIgnore
    @PostMapping("bindWxAuthXyLinker")
    fun bindWxAuthXyLinker(wxLinker: WxLinker, xyNo: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        val resultStr = customerService.bindWxAuthXyLinker(wxLinker, xyNo)
        if (resultStr.isNullOrEmpty()) {
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = resultStr!!
        }
        return result
    }

    /**
     * 获取部门名称
     * @author samy
     * @date 2020/03/28
     */
    @GetMapping("linker/dptName")
    @ApiOperation("联系人对应公司所属部门名称")
    @ApiImplicitParams(
            ApiImplicitParam(name = "value", value = "对应类型的值(中文encode下)", required = true, type = "string"),
            ApiImplicitParam(name = "type", value = "类型(1 手机号 2 公司名称)", required = true, type = "int")
    )
    fun getDptName(type: Int, value: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        when (type) {
            1 -> {
                // 根据手机号返回对应客户所在部门的名称
                val dptNameList = customerService.linkerDptNameByPhone(value)
                result["value"] = dptNameList.joinToString(",")
            }
            2 -> {
                // 根据用户名返回对应客户所在部门的名称
                val cstm = customerService.findCustomerByCompName(value)
                result["value"] = cstm?.fkDpt?.name ?: ""
            }
            else -> {
                result["returnCode"] = -1
                result["errMsg"] = "无效类型"
            }
        }
        return result
    }

    /**
     * 修改区域为县级市的客户，把地区改成县级市名
     * 用完注销
     */
//    @ApiIgnore
//    @PostMapping("correctCustomerArea")
//    fun correctCustomerArea(currentPage: Int,pageSize: Int):HashMap<String, Any>{
//        val result = HashMap<String, Any>()
//        var page = PageRequest(currentPage,pageSize)
//        val resultStr = customerService.updateCstCityToArea(page)
//        if (resultStr.isNullOrEmpty()) {
//            result["returnCode"] = 0
//        } else {
//            result["returnCode"] = -1
//            result["errMsg"] = resultStr!!
//        }
//        return result
//    }


}