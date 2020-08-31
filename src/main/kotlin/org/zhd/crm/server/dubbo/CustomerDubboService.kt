package org.zhd.crm.server.dubbo

import com.alibaba.dubbo.config.annotation.Reference
import com.xyscm.erp.crm.api.ErpCstService
import com.xyscm.erp.crm.api.ErpGoodsService
import com.xyscm.framework.common.api.ResultDTO
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.zhd.crm.server.GlobalConfig

@Service
@Profile("dev", "stage", "prod")
class CustomerDubboService {
    @Reference(version = GlobalConfig.Dubbo.GROUP_VERSION, group = GlobalConfig.Dubbo.GROUP_NAME, check = false, lazy = true)
    lateinit var erpCstService: ErpCstService

    private val log = LoggerFactory.getLogger(CustomerDubboService::class.java);

    fun findCstNeverDeliver(allCstStr: String):String{
        log.info("查询未开单客户开始，待查询客户为:$allCstStr")
        val resultDto: ResultDTO = erpCstService.findNeverDeliveryCustomer(allCstStr)
        val status = resultDto.status
        return if (status == 0){
            resultDto.data as String
        }else{
            log.error("查询未开单客户失败")
            ""
        }
    }

}