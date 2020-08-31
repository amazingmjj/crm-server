package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.TaskPlanning

interface TaskPlanningRepository : CrudRepository<TaskPlanning, Long> {
    @Query(value = "from TaskPlanning tk where tk.year = ?1 and tk.month = ?2 and tk.type = ?3 and (tk.orgName like %?4% or ?4 is null) and (tk.dptName like %?5% or ?5 is null) and (tk.acctName like %?6% or ?6 is null)")
    fun findAll(year: Int, month: Int, type: Int, orgName: String?, dptName: String?, acctName: String?, pg: Pageable): Page<TaskPlanning>

    fun findByAcctCode(code: String): List<TaskPlanning>

    fun findByDptCode(code: String): List<TaskPlanning>

    @Query(value = "select COALESCE(sum(tk.amountTask),0.0), COALESCE(sum(tk.onlineTask),0.0), COALESCE(sum(tk.offlineTask),0.0), COALESCE(sum(tk.boardTask),0.0), COALESCE(sum(tk.highValueTask),0.0), COALESCE(sum(tk.firstCustNum),0.0), COALESCE(sum(tk.secondCustNum),0.0) from TaskPlanning tk where tk.year = ?1 and (to_char(tk.month) = ?2 or ?2 is null) and tk.type = ?3 and (tk.orgName = ?4 or ?4 is null) and (tk.dptName = ?5 or ?5 is null) and (tk.acctCode = ?6 or ?6 is null)")
    fun taskCount(year: Int, month: String?, type: Int, orgName: String?, dptName: String?, acctCode: String?): List<Double>

    @Query(value = "select COALESCE(sum(tk.amountTask),0.0), COALESCE(sum(tk.onlineTask),0.0), COALESCE(sum(tk.offlineTask),0.0), COALESCE(sum(tk.boardTask),0.0), COALESCE(sum(tk.highValueTask),0.0), COALESCE(sum(tk.firstCustNum),0.0), COALESCE(sum(tk.secondCustNum),0.0) from TaskPlanning tk where tk.year = ?1 and tk.month in(?2) and tk.type = ?3 and (tk.orgName = ?4 or ?4 is null) and (tk.dptName = ?5 or ?5 is null) and (tk.acctCode = ?6 or ?6 is null)")
    fun taskCount(year: Int, monthList: List<Int>, type: Int, orgName: String?, dptName: String?, acctCode: String?): List<Double>

    @Query(value = "select count(tk.id) from TaskPlanning tk where tk.year = ?1 and tk.month = ?2 and tk.type = ?3 and (tk.compName = ?4 or ?4 is null) and (tk.orgName = ?5 or ?5 is null) and (tk.dptName = ?6 or ?6 is null) and (tk.acctCode = ?7 or ?7 is null)")
    fun taskCountRepeats(year: Int, month: Int, type: Int, compName: String?, orgName: String?, dptName: String?, acctCode: String?): Int
}