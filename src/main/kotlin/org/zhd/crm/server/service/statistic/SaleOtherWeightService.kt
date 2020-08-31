package org.zhd.crm.server.service.statistic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.statistic.SaleOtherWeight
import org.zhd.crm.server.repository.statistic.SaleOtherWeightRepository
import javax.transaction.Transactional

@Service
@Transactional
class SaleOtherWeightService {

    @Autowired
    private lateinit var saleOtherWeightRepository: SaleOtherWeightRepository

    fun saveOtherWeight(saleOtherWeight: SaleOtherWeight): SaleOtherWeight = saleOtherWeightRepository.save(saleOtherWeight)

    fun findById(id: Long): SaleOtherWeight = saleOtherWeightRepository.findOne(id)

    fun updateWeight(id: Long,weight: Double) = saleOtherWeightRepository.updateById(id,weight)

    fun deleteById(id: Long) = saleOtherWeightRepository.delete(id)

    fun findByPage(ny:String?,empCode:String?,pageable: Pageable) = saleOtherWeightRepository.findByPage(ny, empCode, pageable)
}