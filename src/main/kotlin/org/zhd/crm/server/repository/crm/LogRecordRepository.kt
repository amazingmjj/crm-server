package org.zhd.crm.server.repository.crm

import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.LogRecord

interface LogRecordRepository : CrudRepository<LogRecord, Long> {
}