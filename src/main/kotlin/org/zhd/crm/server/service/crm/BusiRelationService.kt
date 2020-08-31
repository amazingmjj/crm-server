package org.zhd.crm.server.service.crm

import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.BusiRelation
import org.zhd.crm.server.repository.crm.BusiRelationRepository
import org.zhd.crm.server.service.BaseService

@Service
class BusiRelationService(dao: BusiRelationRepository) : BaseService<BusiRelationRepository, BusiRelation, Long>(dao) {
}