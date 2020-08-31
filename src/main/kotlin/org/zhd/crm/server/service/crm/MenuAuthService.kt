package org.zhd.crm.server.service.crm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.Account
import org.zhd.crm.server.model.crm.ExtraAuth
import org.zhd.crm.server.model.crm.MenuAuth
import org.zhd.crm.server.repository.crm.MenuAuthRepository
import kotlin.collections.HashSet

@Service
class MenuAuthService {
    @Autowired
    lateinit var menuAuthRepository: MenuAuthRepository

    fun saveMenuAuth(menuAuth: MenuAuth): MenuAuth = menuAuthRepository.save(menuAuth)

    fun findUniqueMenuAuth(menuId: Long,authName: String) = menuAuthRepository.findUniqueMenuAuth(menuId, authName)

    fun findById(id: Long): MenuAuth = menuAuthRepository.findOne(id)

    fun deleteById(id: Long) = menuAuthRepository.delete(id)

    fun findByPage(menuId:Long?,authName:String?,pageable: Pageable) = menuAuthRepository.findByPage(menuId, authName, pageable)

    fun findByRole(name: String?, id: String?) = menuAuthRepository.findByRole(name,id)

    fun findByAcct(name: String?, loginAcct: String?, orgName: String?, dptName: String?, position: String?, phone: String?, roleName: String?, dataLevel: String?)
            = menuAuthRepository.findByAcct(name,loginAcct,orgName,dptName,position, phone, roleName, dataLevel)

    fun findAllExtraAuth(acct:Account) :Set<ExtraAuth>{
        val menuAuthList = menuAuthRepository.findAll()
        val extraAuthSet = HashSet<ExtraAuth>()
        for (menuAuth in menuAuthList){
            var extraAuth = ExtraAuth()
            if (null!=acct.extraAuths&& acct.extraAuths!!.isNotEmpty()){
                val oldExtraAuthList = acct.extraAuths!!.filter { item -> item.fkMenuAuth.id == menuAuth.id }
                if (oldExtraAuthList.isNotEmpty()) extraAuth = oldExtraAuthList[0]
            }
            extraAuth.fkMenuAuth=menuAuth
            extraAuth.hasCheck=1
            extraAuthSet.add(extraAuth)
        }
        return extraAuthSet
    }
}


