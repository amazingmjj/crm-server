package org.zhd.crm.server.service.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.repository.crm.MenuRepository

@Service
class MenuService {
    @Autowired
    private lateinit var menuRepository: MenuRepository

    fun findMenuList(name: String?,pageable: Pageable) = menuRepository.findMenuList(name,pageable)

    fun findById(menuId:Long) = menuRepository.findOne(menuId)
}