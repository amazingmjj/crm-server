package org.zhd.crm.server

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringRunner
import org.zhd.crm.server.model.crm.Auth
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.repository.crm.AuthRepository
import java.util.*
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class CrmOperation{
    @Autowired
    private lateinit var authRepo: AuthRepository
    @Autowired
    private lateinit var acctRepo: AccountRepository

    private val log = LoggerFactory.getLogger(CstmServiceTest::class.java)

    @Test
    @Transactional //解决懒加载
    @Rollback(false) //解决自动回滚
    fun upDateAcctAuth(){
        //当新的菜单赋予角色权限后，需要更新所有账户，使其拥有的对应菜单权限
        val acct = acctRepo.findOne(142)
//        val obj = acctRepo.findAll()
//        obj.map { acct ->
            log.info(">>>账号为：${acct.name}")
            //原权限
            val originMap = HashMap<Long, Long>()
            acct.auths.map { s ->
                originMap[s.fkMenu.id!!] = s.id!!
            }
            log.info(">>>原权限有：${originMap.size}项")
            //新权限
            val newMap = HashMap<Long, Long>()
            acct.fkRole.auths.map { s ->
                newMap[s.fkMenu.id!!] = s.id!!
            }
            val auths = HashSet<Auth>()
            originMap.keys.map { key ->
                val originAuth = authRepo.findOne(originMap[key])
                //新权限中没有该菜单，则取该菜单权限,有则以新权限为准
                if (!newMap.containsKey(key)) auths.add(originAuth)
            }
            acct.fkRole.auths.map{ ath ->
                auths.add(ath)
            }
            log.info(">>>新权限有：${auths.size}项")

            acct.auths = auths
            acctRepo.save(acct)
//        }
    }
}