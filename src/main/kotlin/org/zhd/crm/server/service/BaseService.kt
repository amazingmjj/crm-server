package org.zhd.crm.server.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.service.crm.EntityManageService
import org.zhd.crm.server.util.CommUtil
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

abstract class BaseService<T : CrudRepository<M, k>, M, k : Serializable>(dao: T) {
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var entityManageService: EntityManageService
    var dao: T
        protected set

    init {
        this.dao = dao
    }

    fun save(obj: M) = this.dao?.save(obj)

    fun findOne(id: k) = this.dao?.findOne(id)

    fun deleteOne(id: k) = this.dao!!.delete(id)

    fun findAll() = this.dao.findAll()

    fun update(obj: Any, extends: Array<String> = arrayOf("createAt", "updateAt")): M {
        val t = obj.javaClass.getMethod("getId")
        val id = t.invoke(obj) as k
        var target = this.dao.findOne(id)
        target = commUtil.autoSetClass(obj, target as Any, extends) as M
        return target
    }

    fun nativeQuery(req: HttpServletRequest, queryStr: String, countQueryStr: String, clazz: Class<*>, queryMap: Map<String, Any>? = null, excludeFields: Array<String>? = null) = entityManageService.nativeQuery(req, queryStr, countQueryStr, clazz, queryMap, excludeFields)

    fun hqlQuery(req: HttpServletRequest, queryStr: String, countQueryStr: String, queryMap: Map<String, Any>? = null, excludeFields: Array<String>? = null) = entityManageService.hqlQuery(req, queryStr, countQueryStr, queryMap, excludeFields)
}