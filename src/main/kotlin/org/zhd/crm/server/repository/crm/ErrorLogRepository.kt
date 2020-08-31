package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.ErrorLog

interface ErrorLogRepository : CrudRepository<ErrorLog, Long> {
    @Query(value = "from ErrorLog as el where (el.content like %?1% or ?1 is null) and (to_char(el.createAt,'yyyy-MM-dd') >= ?2 or ?2 is null) and (to_char(el.createAt,'yyyy-MM-dd') <= ?3 or ?3 is null)")
    fun findAll(content: String?, startTime: String?, endTime: String?, pg: Pageable): Page<ErrorLog>
}