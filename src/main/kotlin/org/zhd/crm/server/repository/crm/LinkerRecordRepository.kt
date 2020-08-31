package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.LinkerRecord

interface LinkerRecordRepository : CrudRepository<LinkerRecord, Long>{
	@Query(value = "select rcd from LinkerRecord as rcd,Account as acct where acct.name = rcd.delName and (rcd.cstmName like %?1% or ?1 is null) and (rcd.name like %?2% or ?2 is null) and (rcd.phone like %?3% or ?3 is null) and (to_char(rcd.createAt,'yyyy-MM-dd') >= ?4 or ?4 is null) and (to_char(rcd.createAt,'yyyy-MM-dd') <= ?5 or ?5 is null) and (to_char(rcd.mainStatus) = ?6 or ?6 is null) and (to_char(rcd.sex) = ?7 or ?7 is null) and (rcd.position like %?8% or ?8 is null) and (to_char(acct.id) = ?9 or ?9 is null) and (to_char(acct.fkDpt.id) = ?10 or ?10 is null) and (to_char(acct.fkDpt.fkOrg.id)= ?11 or ?11 is null)")
	fun findAll(compName: String?, name: String?, phone: String?, startTime: String?, endTime: String?, mainStatus: String?, sex: String?, position: String?, acctId: String?, dptId: String?, orgId: String?, pageable: Pageable): Page<LinkerRecord>
}