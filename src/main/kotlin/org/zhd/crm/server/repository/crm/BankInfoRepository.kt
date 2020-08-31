package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.BankInfo

interface BankInfoRepository: CrudRepository<BankInfo, Long> {
	@Query(value = "from BankInfo as bk where bk.fkCstm.id = ?1")
	fun findAll(id:Long): List<BankInfo>

	@Query(value = "from BankInfo as bk where bk.fkCstm.id = ?1 and bk.mainAcct = 1")
	fun findBankInfo(id: Long?): BankInfo?

	@Query(value="select count (bk.id) from BankInfo as bk where bk.bankAcct = ?1 and bk.fkCstm.id = ?2")
	fun bankCount(bankAcct: String, id: Long): Int
}