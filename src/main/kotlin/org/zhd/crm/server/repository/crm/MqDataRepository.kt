package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.MqData

interface MqDataRepository : CrudRepository<MqData, Long>{
    @Query(value = "from MqData as mq where (mq.content like %?1% or ?1 is null) and (to_char(mq.createAt,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(mq.createAt,'yyyy-MM-dd') <= ?3 or ?3 is null) and (to_char(mq.msgType) = ?4 or ?4 is null) order by mq.updateAt desc")
    fun findAll(content: String?, startTime: String?, endTime: String?, msgType: String?, pg: Pageable): Page<MqData>
}