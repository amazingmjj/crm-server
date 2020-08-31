package org.zhd.crm.server.service.erp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.repository.erp.BasicFeeItemRepository

@Service
class FeeItemService {
    @Autowired
    private lateinit var basicFeeItemRepository: BasicFeeItemRepository

    fun findFeeItemByName(name:String,className:String) = basicFeeItemRepository.findByItemName(name,className);
}