package org.zhd.crm.server.service.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.*
import org.zhd.crm.server.repository.crm.*
import javax.transaction.Transactional

@Service
@Transactional
class BasicDataService {
	// 业务关系
	@Autowired
	private lateinit var busiRelationRepo: BusiRelationRepository
	// 客户性质
	@Autowired
	private lateinit var customPropertyRepo: CustomPropertyRepository
	// 物资品类
	@Autowired
	private lateinit var supplyCatalogRepo: SupplyCatalogRepository
	// 物资用途
	@Autowired
	private lateinit var purposeRepo: PurposeRepository
	// 加工需求
	@Autowired
	private lateinit var processReqRepo: ProcessRequirementRepository
    // 所属行业
    @Autowired
    private lateinit var industryRepo: IndustryRepository

	//保存业务关系
	fun busiRelationSave(busiRelation: BusiRelation) = busiRelationRepo.save(busiRelation)

	//查询单个业务关系
	fun findBusiRelationById(id: Long?) = busiRelationRepo.findOne(id)

	//根据名称查询
	fun findBusiRelationByName(name: String) = busiRelationRepo.findByName(name)

	//删除业务关系
	fun delBusiRelationById(id: Long?) = busiRelationRepo.delete(id)

	// TODO 利用nativeQuery查询语句 复杂查询可以使用到
	fun findBusiRelationList(currentPage: Int, pageSize: Int, vararg values: String): List<BusiRelation> {
		val lastRow = pageSize * currentPage
		val firstRow = lastRow + 1 - pageSize
		return busiRelationRepo.findListPaginate(firstRow, lastRow)
	}

	//查询所有业务关系,支持name或id模糊查询
	fun findBusiRelationList(name: String?, id: String?, pageable: Pageable) = busiRelationRepo.findAll(name, id, pageable)

	//查询所有
	fun findBusiRelationAll() = busiRelationRepo.findAll()

	//批量更新业务关系状态
	fun busiRelationBatchUpdate(status: Int, ids: List<Long>) = busiRelationRepo.batchUpdateStatus(status, ids)

	//查询所有客户性质,支持name或id模糊查询
	fun findCustomPropertyList(name: String?, id: String?, pageable: Pageable) = customPropertyRepo.findAll(name, id, pageable)

	//查询所有
	fun findCustomPropertyAll() = customPropertyRepo.findAll()

	//根据名称查询
	fun findCustomPropertyByName(name: String) = customPropertyRepo.findByName(name)

	//保存客户性质
	fun customPropertySave(customProperty: CustomProperty) = customPropertyRepo.save(customProperty)

	//查询单个客户性质
	fun findCustomPropertyById(id: Long?) = customPropertyRepo.findOne(id)

	//删除客户性质
	fun delCustomPropertyById(id: Long?) = customPropertyRepo.delete(id)

	//批量更新客户性质状态
	fun customPropertyBatchUpdate(status: Int, ids: List<Long>) = customPropertyRepo.batchUpdateStatus(status, ids)

	//查询所有物资品类,支持name或id模糊查询
	fun findSupplyCatalogList(name: String?, id: String?, pageable: Pageable) = supplyCatalogRepo.findAll(name, id, pageable)

	//查询所有
	fun findSupplyCatalogAll() = supplyCatalogRepo.findAll()

	//根据名称查询
	fun findSupplyCatalogByName(name: String) = supplyCatalogRepo.findByName(name)

	//保存物资品类
	fun supplyCatalogSave(supplyCatalog: SupplyCatalog) = supplyCatalogRepo.save(supplyCatalog)

	//查询单个物资品类
	fun findSupplyCatalogById(id: Long?) = supplyCatalogRepo.findOne(id)

	//删除物资品类
	fun delSupplyCatalogById(id: Long?) = supplyCatalogRepo.delete(id)

	//批量更新物资品类状态
	fun supplyCatalogBatchUpdate(status: Int, ids: List<Long>) = supplyCatalogRepo.batchUpdateStatus(status, ids)

	//查询所有物资用途,支持name或id模糊查询
	fun findPurposeList(name: String?, id: String?, pageable: Pageable) = purposeRepo.findAll(name, id, pageable)

	//查询所有
	fun findPurposeAll() = purposeRepo.findAll()

	//根据名称查询
	fun findPurposeByName(name: String) = purposeRepo.findByName(name)

	//保存物资用途
	fun purposeSave(purpose: Purpose) = purposeRepo.save(purpose)

	//查询单个物资用途
	fun findPurposeById(id: Long?) = purposeRepo.findOne(id)

	//删除物资用途
	fun delPurposeById(id: Long?) = purposeRepo.delete(id)

	//批量更新物资用途状态
	fun purposeBatchUpdate(status: Int, ids: List<Long>) = purposeRepo.batchUpdateStatus(status, ids)

	//查询所有加工需求,支持name或id模糊查询
	fun findProcessReqList(name: String?, id: String?, pageable: Pageable) = processReqRepo.findAll(name, id, pageable)

	//查询所有
	fun findProcessReqAll() = processReqRepo.findAll()

	//根据名称查询
	fun findProcessReqByName(name: String) =  processReqRepo.findByName(name)

	//保存加工需求
	fun processReqSave(processReq: ProcessRequirement) =  processReqRepo.save(processReq)

	//查询单个加工需求
	fun findProcessReqById(id: Long?) = processReqRepo.findOne(id)

	//删除加工需求
	fun delProcessReqById(id: Long?) = processReqRepo.delete(id)

	//批量更新加工需求状态
	fun processReqBatchUpdate(status: Int, ids: List<Long>) = processReqRepo.batchUpdateStatus(status, ids)

    //查询所有行业,支持name或id模糊查询
    fun findIndustryList(name: String?, id: String?, pageable: Pageable) = industryRepo.findAll(name, id, pageable)

    //保存所属行业
    fun industrySave(industry: Industry) = industryRepo.save(industry)

    //查询单个行业
    fun findIndustryById(id: Long?) = industryRepo.findOne(id)

    //根据名称查询
    fun findIndustryByname(name: String) = industryRepo.findByName(name)

    //删除行业
    fun delIndustryById(id: Long?) = industryRepo.delete(id)

    //批量更新行业状态
    fun industryBatchUpdate(status: Int, ids: List<Long>) = industryRepo.batchUpdateStatus(status, ids)

    //查询所有
    fun findIndustryAll() = industryRepo.findAll()
}