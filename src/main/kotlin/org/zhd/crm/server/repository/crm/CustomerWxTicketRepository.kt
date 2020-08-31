package org.zhd.crm.server.repository.crm

import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Customer
import org.zhd.crm.server.model.crm.CustomerWxTicket

interface CustomerWxTicketRepository : CrudRepository<CustomerWxTicket, Long> {
    fun findByFkCstmAndAppKey(cstm: Customer, appKey: String): CustomerWxTicket?
}