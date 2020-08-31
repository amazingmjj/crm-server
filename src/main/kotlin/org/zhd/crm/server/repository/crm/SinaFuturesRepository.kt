package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.SinaFutures

interface SinaFuturesRepository : CrudRepository<SinaFutures, Long> {
    @Query(value = "select count(1) from SinaFutures as sf where sf.futureCode = ?1 and sf.datetime = ?2")
    fun repeatCount(futureCode: String, datetime: String): Int

    @Query(value = "from SinaFutures as sf where to_char(sf.createAt,'yyyy-MM-dd') = ?1 and sf.futureCode = ?2 order by sf.datetime asc")
    fun findSinaData(currentTime: String, futureCode: String): List<SinaFutures>
}