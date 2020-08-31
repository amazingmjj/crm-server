package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Message

interface MessageRepository : CrudRepository<Message, Long> {
    @Query(value = "from Message as mg where mg.acctCode = ?1 and mg.type = ?2 and (to_char(mg.createAt,'yyyy-MM-dd') = to_char(current_timestamp,'yyyy-MM-dd') or mg.status = 0) order by mg.createAt desc")
    fun findMsgByAcctId(acctCode: String, type: Int): List<Message>
}