package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.MobileInfo

interface MobileInfoRepository : CrudRepository<MobileInfo, Long> {
    @Query(value = "from MobileInfo as mb where mb.deviceNum = ?1 and mb.type = ?2")
    fun findOne(deviceNum: String, type: Int): MobileInfo?

    @Query(value = "from MobileInfo as mb where to_char(mb.updateAt,'yyyy-MM-dd') < ?1")
    fun findAll(day: String): List<MobileInfo>

    fun findByAcctId(id: Long): List<MobileInfo>?
}