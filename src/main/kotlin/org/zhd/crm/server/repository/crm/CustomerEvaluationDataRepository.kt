package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerEvaluationData
import javax.transaction.Transactional

interface CustomerEvaluationDataRepository : CrudRepository<CustomerEvaluationData, Long> {
    @Query("select count(t.id) from CustomerEvaluationData as t where t.parentId=?1")
    fun countByParentId(pid: Long): Int

    fun findByParentId(pid: Long): List<CustomerEvaluationData>

    @Query("delete CustomerEvaluationData as t where t.id not in ?1 and t.parentId=?2")
    @Transactional
    @Modifying
    fun deleteIdByIds(ids: List<Long>, pid: Long)

    @Query("delete CustomerEvaluationData as t where t.parentId=?1")
    @Transactional
    @Modifying
    fun deleteByParentId(pid: Long)
}