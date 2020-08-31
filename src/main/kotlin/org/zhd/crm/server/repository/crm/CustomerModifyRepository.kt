package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerModify

interface CustomerModifyRepository : CrudRepository<CustomerModify, Long> {
	@Query(value = "from CustomerModify as cm where to_char(cm.customer.id) = ?5 and (to_char(cm.createAt,'yyyy-MM-dd') >= ?1 or ?1 is null) and (to_char(cm.createAt,'yyyy-MM-dd') <= ?2 or ?2 is null) and (cm.creator.name like %?3% or ?3 is null) and (cm.columnName like %?4% or ?4 is null)")
	fun findAll(startTime:String?,endTime:String?,acctName:String?,columnName:String?,cstmId: String,pageable: Pageable): Page<CustomerModify>

    //查询某段时间公司名称修改的数据
    @Query(value = "from CustomerModify as cm where to_char(cm.createAt,'yyyy-MM-dd') >= ?1 and to_char(cm.createAt,'yyyy-MM-dd') <= ?2 and cm.columnName = '公司名称'")
    fun findCstmMdy(startTime: String, endTime: String): List<CustomerModify>
}