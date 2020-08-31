package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CommonCstmLinker
import javax.transaction.Transactional

interface CommonCstmLinkerRepository : CrudRepository<CommonCstmLinker, Long> {
    @Query(nativeQuery = true, value = "select  * from ref_cstm_comm_link where cstm_id = ?1 and linker_id=?2")
    fun uniqueOne(cstmId: Long, linkerId: Long): CommonCstmLinker?

    @Query("from CommonCstmLinker t where t.cstmId = ?1 and status = 1")
    fun commonLinkerByCstmId(cstmId: Long): CommonCstmLinker?

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update ref_cstm_comm_link set status = 0 where cstm_id=?1")
    fun batchUpdateMark(cstmId: Long)

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from ref_cstm_comm_link where cstm_id=?1 and linker_id=?2")
    fun deleteOne(cstmId: Long, linkerId: Long)
}