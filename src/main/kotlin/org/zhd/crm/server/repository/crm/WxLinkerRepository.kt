package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Account
import org.zhd.crm.server.model.crm.Customer
import org.zhd.crm.server.model.crm.WxLinker
import org.zhd.crm.server.model.projection.WxLinkerProjection
import javax.transaction.Transactional

interface WxLinkerRepository : CrudRepository<WxLinker, Long> {
    fun findByOpenIdAndTypeAndFkCstm(openId: String, type: Int, cstm: Customer): WxLinker?

    fun findByOpenIdAndTypeAndAcct(openId: String, type: Int, acct: Account): WxLinker?

    @Query(nativeQuery = true, value = "select count(t.id) from t_wx_linker t where t.open_id=?1")
    fun countByOpenId(openId: String): Int

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update T_WX_LINKER t set t.SUBSCRIBE='否' where t.open_id=?1")
    fun batchUpdateBySubscribe(openId: String): Int

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from T_WX_LINKER t where t.acct_id=?1 and t.type=2")
    fun batchAcctLinkerDel(acctId: Long): Int

    /**
     * 当前客户的微信客户
     */
    @Query(value = "from WxLinker t where t.fkCstm.id=?1 and t.type=1")
    fun selectByCstmId(cstmId: Long, pageable: Pageable): Page<WxLinker>

    @Query(value = "from WxLinker t where t.acct.id=?1 and t.type=2")
    fun acctWxLinkers(acctId: Long): List<WxLinker>

    @Query(nativeQuery = true, value = "select t.name, t.app_name, t.app_key, t.avatar, t.open_id, t.subscribe, t.fk_cstm_id as cstm_id from t_wx_linker t LEFT JOIN  t_customer cstm on cstm.id = t.fk_cstm_id where cstm.id=?1 OR t.ACCT_ID=?2 ORDER BY cstm_id desc")
    fun cstmWxLinkers(cstmId: Long, acctId: Long): List<WxLinkerProjection>

}