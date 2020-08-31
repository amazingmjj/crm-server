package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerRecord
import java.sql.Timestamp

interface CustomerRecordRepository: CrudRepository<CustomerRecord, Long> {
    @Query(value="from CustomerRecord as cstrd join cstrd.fkCustom.linkers as link where link.mainStatus = 1 and to_char(cstrd.type) = ?8 and (cstrd.fkCustom.compName like %?1% or ?1 is null) and (link.name like %?2% or ?2 is null) and (link.phone like %?3% or ?3 is null) and (to_char(cstrd.createAt,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(cstrd.createAt,'yyyy-MM-dd') <= ?5 or ?5 is null) and (cstrd.fkCustom.fkDpt.name like %?6% or ?6 is null) and (cstrd.fkCustom.fkAcct.name like %?7% or ?7 is null)")
    fun findAll(compName:String?, name:String?, phone:String?, startTime: String?, endTime: String?, dptName:String?,
                acctName:String?, type:String, pageable: Pageable): Page<CustomerRecord>

    @Query(value = "select count(cstrd.id) from CustomerRecord as cstrd where cstrd.type = 4 and (cstrd.fkCustom.fkAcct.platformCode = ?1 or ?1 is null) and to_char(cstrd.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun secondCstmCount(platformCode: String?, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select count(cstrd.id) from CustomerRecord as cstrd where cstrd.type = 4 and cstrd.fkCustom.fkAcct.platformCode in(?1) and to_char(cstrd.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun secondCstmCount(empList: List<String>, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select count(cstrd.id) from CustomerRecord as cstrd where cstrd.type = 4 and (cstrd.fkCustom.fkAcct.platformCode = ?1 or ?1 is null) and to_char(cstrd.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun secondCstmCounting(empCode: String?, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select count(cstrd.id) from CustomerRecord as cstrd where cstrd.type = 4 and cstrd.fkCustom.fkAcct.platformCode in(?1) and to_char(cstrd.createAt,'yyyy-MM-dd') between to_char(?2,'yyyy-MM-dd') and to_char(?3,'yyyy-MM-dd')")
    fun secondCstmCounting(empCodeList: List<String>, startTime: Timestamp, endTime: Timestamp): Double

    @Query(value = "select count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?2 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?3 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?4 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?5 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?6 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?7 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?8 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') between ?9 and ?10 then cstrd.id end) from CustomerRecord as cstrd where cstrd.type = 4 and (cstrd.fkCustom.fkAcct.platformCode = ?1 or ?1 is null)")
    fun secondCstmCountList(platformCode: String?, firstTime: String, secondTime: String, thirdTime: String, fourthTime: String, fifthTime: String, sixthTime: String, seventhTime: String, startTime:String, endTime:String): List<Double>

    @Query(value = "select count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?2 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?3 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?4 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?5 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?6 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?7 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') = ?8 then cstrd.id end),count(case when to_char(cstrd.createAt,'yyyy-MM-dd') between ?9 and ?10 then cstrd.id end) from CustomerRecord as cstrd where cstrd.type = 4 and cstrd.fkCustom.fkAcct.platformCode in(?1)")
    fun secondCstmCountList(empList: List<String>, firstTime: String, secondTime: String, thirdTime: String, fourthTime: String, fifthTime: String, sixthTime: String, seventhTime: String, startTime:String, endTime:String): List<Double>

}