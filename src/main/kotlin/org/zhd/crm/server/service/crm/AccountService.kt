package org.zhd.crm.server.service.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.Account
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.service.BaseService

@Service
class AccountService(accountRepo: AccountRepository) : BaseService<AccountRepository, Account, Long>(accountRepo){
//    @Autowired
//    private lateinit var acctRepo: AccountRepository

    fun findByLoginAcct(acct: String) = this.dao.findByLoginAcct(acct)
}