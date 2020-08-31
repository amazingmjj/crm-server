package org.zhd.crm.server.repository.statistic

import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.statistic.GoodStock

interface GoodStockRepository : CrudRepository<GoodStock, Long> {

    fun findBySumGoodsBatch(sumGoodsBatch: String): GoodStock?
}