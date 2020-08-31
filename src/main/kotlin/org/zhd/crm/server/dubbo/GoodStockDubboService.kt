package org.zhd.crm.server.dubbo

import com.alibaba.dubbo.config.annotation.Reference
import com.xyscm.erp.crm.api.ErpGoodsService
import com.xyscm.erp.crm.api.dto.GoodStockDto
import com.xyscm.framework.common.api.PageDto
import com.xyscm.framework.common.api.ResultDTO
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.zhd.crm.server.GlobalConfig

@Service
@Profile("dev", "stage", "prod")
class GoodStockDubboService{
    @Reference(version = GlobalConfig.Dubbo.GROUP_VERSION, group = GlobalConfig.Dubbo.GROUP_NAME, check = false, lazy = true)
    lateinit var erpGoodsService: ErpGoodsService

    private val log = LoggerFactory.getLogger(GoodStockDubboService::class.java)

    //查询库存报价,调用erp dubbo接口
    fun queryGoodStockList(currentPage: Int, pageSize: Int, goodStockDto: GoodStockDto, result: HashMap<String, Any>): HashMap<String, Any>{
        val resultDto: ResultDTO = erpGoodsService.queryGoodsStock(goodStockDto, pageSize, currentPage + 1)//erp是自定义的分页
        log.info(">>>page:$currentPage,$pageSize,message:${resultDto.message}")
        val pageDto = resultDto.data as PageDto
        val list = pageDto.data
        val total = pageDto.recordCount
        result["total"] = total
        result["list"] = list
        return result
    }

    //查询库存报价,调用erp dubbo接口
    fun queryGoodStock(currentPage: Int, pageSize: Int, goodStockDto: GoodStockDto): List<GoodStockDto>{
        val resultDto: ResultDTO = erpGoodsService.queryGoodsStock(goodStockDto, pageSize, currentPage + 1)//erp是自定义的分页
        log.info(">>>page:$currentPage,$pageSize,message:${resultDto.message}")
        val pageDto = resultDto.data as PageDto
        return pageDto.data as List<GoodStockDto>
    }
}