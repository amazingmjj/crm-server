package org.zhd.crm.server.controller.crm

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import org.zhd.crm.server.controller.CrmBaseController
import org.zhd.crm.server.model.crm.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("basicData")
class BasicDataController : CrmBaseController() {
	@PostMapping("busiRelation")
	fun busiRelationList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
		val result = HashMap<String, Any>()
		val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
		val page = basicDataService.findBusiRelationList(req.getParameter("name"), req.getParameter("id"), pg)
		result["returnCode"] = 0
		result["list"] = page.content
		result["total"] = page.totalElements
		return result
	}

	@PostMapping("busiRelation/create")
	fun busiRelationCreate(busiRelation: BusiRelation): Map<String, Any> {
		val result = HashMap<String, Any>()
		val newobj = basicDataService.busiRelationSave(busiRelation)
		if (newobj.id!! > 0) {
			result["returnCode"] = 0
			result["newObj"] = newobj
		} else {
			result["returnCode"] = -1
			result["errMsg"] = "保存失败"
		}
		return result
	}

	@PostMapping("busiRelation/update")
	fun busiRelationUpdate(busiRelation: BusiRelation): Map<String, Any> {
		val result = HashMap<String, Any>()
		val originObj = basicDataService.findBusiRelationById(busiRelation.id)
		originObj.name = busiRelation.name
		originObj.status = busiRelation.status
		basicDataService.busiRelationSave(originObj)
		result["returnCode"] = 0
		result["originObj"] = originObj
		return result
	}

	@DeleteMapping("busiRelation/{id}")
	fun busiRelationDelete(@PathVariable("id") id: Long): Map<String, Any> {
		val result = HashMap<String, Any>()
		basicDataService.delBusiRelationById(id)
		result["returnCode"] = 0
		return result
	}

	@PostMapping("busiRelation/batchUpdate")
	fun busiRelationBatchUpdate(ids: String, status: Int): Map<String, Any> {
		val result = HashMap<String, Any>()
		val longIds = ids.split(",").map { s -> s.toLong() }
		basicDataService.busiRelationBatchUpdate(status, longIds)
		result["returnCode"] = 0
		return result
	}

	@GetMapping("busiRelation/queryCombo")
	fun busiRelationCombo(): Map<String, Any> {
		val result = HashMap<String, Any>()
		result["returnCode"] = 0
		result["list"] = basicDataService.findBusiRelationAll()
		return result
	}

	@PostMapping("customProperty")
	fun customPropertyList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
		val result = HashMap<String, Any>()
		val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
		val page = basicDataService.findCustomPropertyList(req.getParameter("name"), req.getParameter("id"), pg)
		result["returnCode"] = 0
		result["list"] = page.content
		result["total"] = page.totalElements
		return result
	}

	@PostMapping("customProperty/create")
	fun customPropertyCreate(customProperty: CustomProperty): Map<String, Any> {
		val result = HashMap<String, Any>()
		val newobj = basicDataService.customPropertySave(customProperty)
		if (newobj.id!! > 0) {
			result["returnCode"] = 0
			result["newObj"] = newobj
		} else {
			result["returnCode"] = -1
			result["errMsg"] = "保存失败"
		}
		return result
	}

	@PostMapping("customProperty/update")
	fun customPropertyUpdate(customProperty: CustomProperty): Map<String, Any> {
		val result = HashMap<String, Any>()
		val originObj = basicDataService.findCustomPropertyById(customProperty.id)
		originObj.name = customProperty.name
		originObj.status = customProperty.status
		basicDataService.customPropertySave(originObj)
		result["returnCode"] = 0
		result["originObj"] = originObj
		return result
	}

	@DeleteMapping("customProperty/{id}")
	fun customPropertyDelete(@PathVariable("id") id: Long): Map<String, Any> {
		val result = HashMap<String, Any>()
		basicDataService.delCustomPropertyById(id)
		result["returnCode"] = 0
		return result
	}

	@PostMapping("customProperty/batchUpdate")
	fun customPropertyBatchUpdate(ids: String, status: Int): Map<String, Any> {
		val result = HashMap<String, Any>()
		val longIds = ids.split(",").map { s -> s.toLong() }
		basicDataService.customPropertyBatchUpdate(status, longIds)
		result["returnCode"] = 0
		return result
	}

	@GetMapping("customProperty/queryCombo")
	fun customProCombo(): Map<String, Any> {
		val result = HashMap<String, Any>()
		result["returnCode"] = 0
		result["list"] = basicDataService.findCustomPropertyAll()
		return result
	}

	@PostMapping("supplyCatalog")
	fun supplyCatalogList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
		val result = HashMap<String, Any>()
		val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
		val page = basicDataService.findSupplyCatalogList(req.getParameter("name"), req.getParameter("id"), pg)
		result["returnCode"] = 0
		result["list"] = page.content
		result["total"] = page.totalElements
		return result
	}

	@PostMapping("supplyCatalog/create")
	fun supplyCatalogCreate(supplyCatalog: SupplyCatalog): Map<String, Any> {
		val result = HashMap<String, Any>()
		val newobj = basicDataService.supplyCatalogSave(supplyCatalog)
		if (newobj.id!! > 0) {
			result["returnCode"] = 0
			result["newObj"] = newobj
		} else {
			result["returnCode"] = -1
			result["errMsg"] = "保存失败"
		}
		return result
	}

	@PostMapping("supplyCatalog/update")
	fun supplyCatalogUpdate(supplyCatalog: SupplyCatalog): Map<String, Any> {
		val result = HashMap<String, Any>()
		val originObj = basicDataService.findSupplyCatalogById(supplyCatalog.id)
		originObj.name = supplyCatalog.name
		originObj.status = supplyCatalog.status
		basicDataService.supplyCatalogSave(originObj)
		result["returnCode"] = 0
		result["originObj"] = originObj
		return result
	}

	@DeleteMapping("supplyCatalog/{id}")
	fun supplyCatalogDelete(@PathVariable("id") id: Long): Map<String, Any> {
		val result = HashMap<String, Any>()
		basicDataService.delSupplyCatalogById(id)
		result["returnCode"] = 0
		return result
	}

	@PostMapping("supplyCatalog/batchUpdate")
	fun supplyCatalogBatchUpdate(ids: String, status: Int): Map<String, Any> {
		val result = HashMap<String, Any>()
		val longIds = ids.split(",").map { s -> s.toLong() }
		basicDataService.supplyCatalogBatchUpdate(status, longIds)
		result["returnCode"] = 0
		return result
	}

	@GetMapping("supplyCatalog/queryCombo")
	fun supplyCatalogCombo(): Map<String, Any> {
		val result = HashMap<String, Any>()
		result["returnCode"] = 0
		result["list"] = basicDataService.findSupplyCatalogAll()
		return result
	}

	@PostMapping("purpose")
	fun purposeList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
		val result = HashMap<String, Any>()
		val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
		val page = basicDataService.findPurposeList(req.getParameter("name"), req.getParameter("id"), pg)
		result["returnCode"] = 0
		result["list"] = page.content
		result["total"] = page.totalElements
		return result
	}

	@PostMapping("purpose/create")
	fun purposeCreate(purpose: Purpose): Map<String, Any> {
		val result = HashMap<String, Any>()
		val newobj = basicDataService.purposeSave(purpose)
		if (newobj.id!! > 0) {
			result["returnCode"] = 0
			result["newObj"] = newobj
		} else {
			result["returnCode"] = -1
			result["errMsg"] = "保存失败"
		}
		return result
	}

	@PostMapping("purpose/update")
	fun purposeUpdate(purpose: Purpose): Map<String, Any> {
		val result = HashMap<String, Any>()
		val originObj = basicDataService.findPurposeById(purpose.id)
		originObj.name = purpose.name
		originObj.status = purpose.status
		basicDataService.purposeSave(originObj)
		result["returnCode"] = 0
		result["originObj"] = originObj
		return result
	}

	@DeleteMapping("purpose/{id}")
	fun purposeDelete(@PathVariable("id") id: Long): Map<String, Any> {
		val result = HashMap<String, Any>()
		basicDataService.delPurposeById(id)
		result["returnCode"] = 0
		return result
	}

	@PostMapping("purpose/batchUpdate")
	fun purposeBatchUpdate(ids: String, status: Int): Map<String, Any> {
		val result = HashMap<String, Any>()
		val longIds = ids.split(",").map { s -> s.toLong() }
		basicDataService.purposeBatchUpdate(status, longIds)
		result["returnCode"] = 0
		return result
	}

	@GetMapping("purpose/queryCombo")
	fun purposeCombo(): Map<String, Any> {
		val result = HashMap<String, Any>()
		result["returnCode"] = 0
		result["list"] = basicDataService.findPurposeAll()
		return result
	}

	@PostMapping("processReq")
	fun processReqList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
		val result = HashMap<String, Any>()
		val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
		val page = basicDataService.findProcessReqList(req.getParameter("name"), req.getParameter("id"), pg)
		result["returnCode"] = 0
		result["list"] = page.content
		result["total"] = page.totalElements
		return result
	}

	@PostMapping("processReq/create")
	fun processReqCreate(processReq: ProcessRequirement): Map<String, Any> {
		val result = HashMap<String, Any>()
		val newobj = basicDataService.processReqSave(processReq)
		if (newobj.id!! > 0) {
			result["returnCode"] = 0
			result["newObj"] = newobj
		} else {
			result["returnCode"] = -1
			result["errMsg"] = "保存失败"
		}
		return result
	}

	@PostMapping("processReq/update")
	fun processReqUpdate(processReq: ProcessRequirement): Map<String, Any> {
		val result = HashMap<String, Any>()
		val originObj = basicDataService.findProcessReqById(processReq.id)
		originObj.name = processReq.name
		originObj.status = processReq.status
		basicDataService.processReqSave(originObj)
		result["returnCode"] = 0
		result["originObj"] = originObj
		return result
	}

	@DeleteMapping("processReq/{id}")
	fun processReqDelete(@PathVariable("id") id: Long): Map<String, Any> {
		val result = HashMap<String, Any>()
		basicDataService.delProcessReqById(id)
		result["returnCode"] = 0
		return result
	}

	@PostMapping("processReq/batchUpdate")
	fun processReqBatchUpdate(ids: String, status: Int): Map<String, Any> {
		val result = HashMap<String, Any>()
		val longIds = ids.split(",").map { s -> s.toLong() }
		basicDataService.processReqBatchUpdate(status, longIds)
		result["returnCode"] = 0
		return result
	}

	@GetMapping("processReq/queryCombo")
	fun processReqCombo(): Map<String, Any> {
		val result = HashMap<String, Any>()
		result["returnCode"] = 0
		result["list"] = basicDataService.findProcessReqAll()
		return result
	}

    @PostMapping("industry")
    fun industryList(currentPage: Int, pageSize: Int, req: HttpServletRequest): Map<String, Any> {
        val result = HashMap<String, Any>()
        val pg = PageRequest(currentPage, pageSize, Sort.Direction.DESC, "updateAt")
        val page = basicDataService.findIndustryList(req.getParameter("name"), req.getParameter("id"), pg)
        result["returnCode"] = 0
        result["list"] = page.content
        result["total"] = page.totalElements
        return result
    }

    @PostMapping("industry/create")
    fun industryCreate(industry: Industry): Map<String, Any> {
        val result = HashMap<String, Any>()
        val newobj = basicDataService.industrySave(industry)
        if (newobj.id!! > 0) {
            result["returnCode"] = 0
            result["newObj"] = newobj
        } else {
            result["returnCode"] = -1
            result["errMsg"] = "保存失败"
        }
        return result
    }

    @PostMapping("industry/update")
    fun industryUpdate(industry: Industry): Map<String, Any> {
        val result = HashMap<String, Any>()
        val originObj = basicDataService.findIndustryById(industry.id)
        originObj.name = industry.name
        originObj.status = industry.status
        basicDataService.industrySave(originObj)
        result["returnCode"] = 0
        result["originObj"] = originObj
        return result
    }

    @DeleteMapping("industry/{id}")
    fun industryDelete(@PathVariable("id") id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        basicDataService.delIndustryById(id)
        result["returnCode"] = 0
        return result
    }

    @PostMapping("industry/batchUpdate")
    fun industryBatchUpdate(ids: String, status: Int): Map<String, Any> {
        val result = HashMap<String, Any>()
        val longIds = ids.split(",").map { s -> s.toLong() }
        basicDataService.industryBatchUpdate(status, longIds)
        result["returnCode"] = 0
        return result
    }

    @GetMapping("industry/queryCombo")
    fun industryCombo(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["returnCode"] = 0
        result["list"] = basicDataService.findIndustryAll()
        return result
    }
}