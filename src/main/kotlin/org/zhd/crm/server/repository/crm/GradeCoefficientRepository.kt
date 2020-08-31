package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.GradeCoefficient
import javax.transaction.Transactional


interface GradeCoefficientRepository : CrudRepository<GradeCoefficient, Long> {
    @Query(value = "from GradeCoefficient gc where gc.parentId = ?1")
    fun findAll(parentId: Long, sort: Sort): List<GradeCoefficient>

    fun findByName(name: String): GradeCoefficient

    fun findByParentId(pid: Long): List<GradeCoefficient>

    @Modifying
    @Transactional
    @Query(value = "delete from GradeCoefficient gc where gc.parentId = ?1")
    fun deleteDebtGrade(parentId: Long)

    @Query(value = "select gc.name from GradeCoefficient gc where gc.parentId =10")
    fun findCategory(): List<String>

    @Query(value = "select gc.name from GradeCoefficient gc where gc.parentId =3")
    fun findRegion(): List<String>

    @Query(value = "from GradeCoefficient gc where gc.parentId is null and gc.id != 1")
    fun findParentList(): List<GradeCoefficient>
}