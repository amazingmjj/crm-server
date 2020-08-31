package org.zhd.crm.server.repository.crm

import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Auth

interface AuthRepository : CrudRepository<Auth, Long> {
}