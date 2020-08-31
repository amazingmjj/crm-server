package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.GradeSummary
import java.sql.Timestamp
import javax.transaction.Transactional

interface GradeSummaryRepository : CrudRepository<GradeSummary, Long> {

    @Query(value = "from GradeSummary gs where gs.compName = ?1 and to_char(gs.summaryDate, 'YYYY-MM-DD') = ?2")
    fun findObj(compName: String, date: String): GradeSummary?

    @Query(value = "from GradeSummary gs where to_char(gs.summaryDate, 'YYYY-MM-DD')=?1 and (gs.compName like %?2% or ?2 is null) and (gs.transformDate between ?3 and ?4 or (?3 is null and ?4 is null)) and (gs.dptName like %?5% or ?5 is null) and (gs.acctName like %?6% or ?6 is null) and ((gs.summary >= ?7 and gs.summary <= ?8) or (?7 is null and ?8 is null)) and (gs.summaryLevel = ?9 or ?9 is null) and (to_char(gs.xyCondition) = ?10 or ?10 is null)")
    fun findDayStatistic(summaryDate: String, compName: String?, transDateStart: Timestamp?, transDateEnd: Timestamp?, dptName: String?, acctName: String?, min: Double?, max: Double?, level: String?, xyCondition: String?, pageable: Pageable): Page<GradeSummary>

    @Query(value = "from GradeSummary gs where gs.compName = ?1 and ((gs.summaryDate between ?2 and ?3) or (?2 is null and ?3 is null))")
    fun findCstmStatistic(compName: String, startDate: Timestamp?, endDate: Timestamp?, pageable: Pageable): Page<GradeSummary>

    @Query(nativeQuery = true, value = "select a.comp_name from t_grade_summary a where to_char(a.create_at,'yyyy-MM-dd') = ?1 group by a.comp_name having count(a.comp_name)>1")
    fun findRepeatList(createAt: String): List<String>

    @Query(nativeQuery = true, value = "select a.id from t_grade_summary a where to_char(a.create_at,'yyyy-MM-dd') = ?1 and a.comp_name = ?2")
    fun findRepeatListByName(createAt: String, compName: String): List<Long>

    @Query(nativeQuery = true, value = "select count(1) from T_GRADE_SUMMARY where to_char(SUMMARY_DATE, 'YYYY-MM-DD') = ?1")
    fun totalDayCount(summaryDate: String): Int

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update T_GRADE_SUMMARY set SUMMARY_LEVEL=?1 where id in (select t2.id from (select rownum as rn, t.* from(select * from T_GRADE_SUMMARY where to_char(SUMMARY_DATE, 'YYYY-MM-DD') = ?2 order by SUMMARY desc) t ) t2 where  t2.rn between  ?3 and ?4)")
    fun batchUpdateLevel(level: String, summaryDate: String, startRow: Int, endRow: Int)

    @Query(nativeQuery = true, value="select distinct (SUMMARY_LEVEL) from T_GRADE_SUMMARY where to_char(SUMMARY_DATE, 'yyyy-mm-dd') = ?1 order by SUMMARY_LEVEL")
    fun queryLevelComb(dateStr: String): List<String>
}