package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_sina_futures")
class SinaFutures(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //名称
    var futureName: String = ""
    //物资代码
    var futureCode: String = ""
    //最新价
    var latestPrice: String? = null
    //开盘价
    var openPrice: String? = null
    //最高价
    var highestPrice: String? = null
    //最低价
    var lowestPrice: String? = null
    //结算价
    var settlementPrice: String? = null
    //昨结算
    var ydaySettlementPrice: String? = null
    //持仓量
    var inventory: String? = null
    //成交量
    var turnover: String? = null
    //买价
    var purchasePrice: String? = null
    //卖价
    var sellingPrice: String? = null
    //买量
    var purchaseWeight: String? = null
    //卖量
    var sellingWeight: String? = null
    //时间
    var datetime: String? = null
}