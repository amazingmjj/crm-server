package org.zhd.crm.server.service.erp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.repository.erp.AreaNameRepository
import org.zhd.crm.server.repository.erp.PartsNameRepository

@Service
class CommService {
    @Autowired
    private lateinit var areaNameRepo: AreaNameRepository
    @Autowired
    private lateinit var partsNameRepo: PartsNameRepository

    fun findByAreaName(name: String?) = areaNameRepo.findByName(name)
    fun findByPartsName(name: String?) = partsNameRepo.findByName(name)
}