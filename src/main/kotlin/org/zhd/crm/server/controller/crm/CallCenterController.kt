package org.zhd.crm.server.controller.crm

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.OutLinker
import org.zhd.crm.server.model.crm.SmsTemplate
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("callCenter")
class CallCenterController : CrmBaseController() {
    private val log = LoggerFactory.getLogger(CallCenterController::class.java)

    @PostMapping("linker")
    fun linker(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.findCstmLinker(1, req.getParameter("xyMark"), req.getParameter("uid"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("mainStatus"), req.getParameter("compName"), req.getParameter("region"), req.getParameter("dptName"), req.getParameter("acctName"), req.getParameter("busiRelation"), req.getParameter("CustomProperty"), req.getParameter("level"), req.getParameter("billDate"), req.getParameter("mark"), result, currentPage.toString(), pageSize.toString())
        result["returnCode"] = 0
        return result
    }

    @PostMapping("linkerAll")
    fun linkerAll(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.findCstmLinker(0, req.getParameter("xyMark"), req.getParameter("uid"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("mainStatus"), req.getParameter("compName"), req.getParameter("region"), req.getParameter("dptName"), req.getParameter("acctName"), req.getParameter("busiRelation"), req.getParameter("CustomProperty"), req.getParameter("level"), req.getParameter("billDate"), req.getParameter("mark"), result, null, null)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("template/createOrUpdate")
    fun templateCreate(smsTemplate: SmsTemplate): Map<String, Any> {
        val result = HashMap<String, Any>()
        //查重 分组加模板名 唯一
        val msg = callCenterService.checkTemplate(smsTemplate)
        if (msg == "") {
            val obj = callCenterService.templateCreate(smsTemplate)
            result["returnCode"] = 0
            result["newObj"] = obj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = msg
        }
        return result
    }

    @DeleteMapping("template/{id}")
    fun templateDel(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.templateDelete(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("template")
    fun template(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = callCenterService.findTemplateAll(req.getParameter("groupName"), req.getParameter("name"), req.getParameter("content"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @GetMapping("template/queryGroupName")
    fun groupNameList(): Map<String, Any> {
        val result = HashMap<String, Any>()
        val list = callCenterService.queryGroupName()
        result["returnCode"] = 0
        result["list"] = list
        result["total"] = list.size
        return result
    }

    @PostMapping("smsCreate")
    fun smsCreate(mobile: String, content: String, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        log.info(">>>content length:${content.length}")
        val list = callCenterService.smsCreate(mobile, content, req.getParameter("delayTime"), req.getParameter("uid"), req.getParameter("mark"))
        if (list[0] == ""){
            result["returnCode"] = 0
        } else {
            result["returnCode"] = -1
            result["errMsg"] = list[0]
        }
        result["numMsg"] = list[1]
        return result
    }

    @GetMapping("smsCancel/{id}")
    fun smsCancel(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.cancelSms(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("smsStatistic")
    fun smsStatistic(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val mark = req.getParameter("mark")
        val pro = if (mark == "0") "create_at" else "createAt"
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, pro)
        val page = callCenterService.findSmsStatAll(req.getParameter("id"), req.getParameter("acctName"), req.getParameter("type"), req.getParameter("status"), req.getParameter("cstmName"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("startTime"), req.getParameter("endTime"), req.getParameter("content"), mark, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("smsSend")
    fun smsSend(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val mark = req.getParameter("mark")
        val pg = if (mark == "0") PageRequest(currentPage, pageSize, Sort.Direction.DESC, "create_at","comp_name") else PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = callCenterService.findSmsAll(req.getParameter("parentId").toLong(), req.getParameter("status"), req.getParameter("cstmName"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("msgId"), mark, pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("smsReceive")
    fun smsReceive(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = callCenterService.findSmsReceiveAll(req.getParameter("id"), req.getParameter("acctName"), req.getParameter("dptName"), req.getParameter("cstmName"), req.getParameter("name"), req.getParameter("phone"), req.getParameter("startTime"), req.getParameter("endTime"), req.getParameter("content"), req.getParameter("mark"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("outLinker/createOrUpdate")
    fun outLinkCreate(outLink: OutLinker, uid:Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        //校验
        val msg = callCenterService.checkOutLink(outLink)
        if (msg == "") {
            val obj = callCenterService.outLinkSave(outLink, uid)
            result["returnCode"] = 0
            result["newObj"] = obj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = msg
        }
        return result
    }

    @DeleteMapping("outLinker/{id}")
    fun outLinkDel(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.outLinkDel(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("outLinker")
    fun outLinker(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "createAt")
        val page = callCenterService.outLinker(req.getParameter("name"), req.getParameter("phone"), req.getParameter("label"), req.getParameter("remark"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("outLinkerAll")
    fun outLinkerAll(req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val list = callCenterService.outLinkerAll(req.getParameter("name"), req.getParameter("phone"), req.getParameter("label"), req.getParameter("remark"))
        result["returnCode"] = 0
        result["list"] = list
        result["total"] = list.size
        return result
    }

    @PostMapping("outLinkerIds")
    fun outLinkerIds(req: HttpServletRequest): Map<String, Any> {
        return callCenterService.outLinkerIds(req.getParameter("name"), req.getParameter("phone"), req.getParameter("label"), req.getParameter("remark"))
    }

    @PostMapping("batchDel")
    fun batchDelOutLk(ids: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.outLkBatchDel(ids)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("batchUpdate")
    fun batchUpdateOutLk(ids: String, label: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        callCenterService.outLkBatchUpdate(ids,label)
        result["returnCode"] = 0
        return result
    }
}