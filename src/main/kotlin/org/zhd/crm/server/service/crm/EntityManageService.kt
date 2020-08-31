package org.zhd.crm.server.service.crm

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.repository.crm.AccountRepository
import org.zhd.crm.server.util.CommUtil
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import javax.servlet.http.HttpServletRequest

@Service
class EntityManageService {
    @PersistenceContext
    private lateinit var entityManager: EntityManager //实体管理对象
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var acctRepo: AccountRepository

    private val log = LoggerFactory.getLogger(EntityManageService::class.java)

    fun nativeQuery(req: HttpServletRequest, queryStr: String, countQueryStr: String, clazz: Class<*>, queryMap: Map<String, Any>?, excludeFields: Array<String>?) = handleQuery(req, queryStr, countQueryStr, queryMap, excludeFields, queryMap, true, clazz)

    fun hqlQuery(req: HttpServletRequest, queryStr: String, countQueryStr: String, queryMap: Map<String, Any>?, excludeFields: Array<String>?) = handleQuery(req, queryStr, countQueryStr, queryMap, excludeFields, queryMap, false)

    fun handleQuery(req: HttpServletRequest, queryStr: String, countQueryStr: String, queryMap: Map<String, Any>?, excludeFields: Array<String>?, countQueryMap: Map<String, Any>?, nativeQuery: Boolean = false, clazz: Class<*>? = null): Map<String, Any> {
        val mainStr = StringBuffer()
        val queryPart = StringBuffer()
        mainStr.append(queryStr)
        if (queryMap != null) {
            queryMap.keys.map { k ->
                if (req.getParameter(k) != null) {
                    queryPart.append(" " + queryMap.get(k).toString().replace("#", req.getParameter(k)))
                }
                if ("append".equals(k)) queryPart.append(" " + queryMap.get("append"))
            }
        }
        mainStr.append(queryPart.toString())
        var query: Query? = null
        if (nativeQuery) query = this.entityManager.createNativeQuery(mainStr.toString(), clazz!!) else query = this.entityManager.createQuery(mainStr.toString())
        val currentPage = req.getParameter("currentPage")
        val pageSize = req.getParameter("pageSize")
        query.firstResult = if (currentPage != null) Integer.parseInt(currentPage) else 0
        query.maxResults = if (pageSize != null) Integer.parseInt(pageSize) else 10
        val mainResult = query.resultList as List<Any>
        val countStr = StringBuffer()
        countStr.append(countQueryStr)
        countStr.append(queryPart.toString())
        var ctQuery: Query? = null
        if (nativeQuery) ctQuery = this.entityManager.createNativeQuery(countStr.toString()) else ctQuery = this.entityManager.createQuery(countStr.toString())
        val total = ctQuery.singleResult.toString().toInt()
        entityManager.close()
        var list = ArrayList<Any>()
        if (mainResult.isNotEmpty()) {
            val first = mainResult[0]
            if (first.javaClass.isArray) {
                list = mainResult as ArrayList<Any>
            } else {
                if (excludeFields != null) {
                    mainResult.map { mb ->
                        list.add(commUtil.entityToMap(mb, excludeFields))
                    }
                } else {
                    list = mainResult as ArrayList<Any>
                }
            }
        }
        val resultMap = HashMap<String, Any>()
        resultMap["list"] = list
        resultMap["total"] = total
        return resultMap
    }

    //hql 当条件多于5个
    fun pageForQuery(req: HttpServletRequest, queryStr: String, queryMap: Map<String, String>?, excludeFields: Array<String>?, nativeQuery: Boolean): Map<String, Any> {
        val mainStr = StringBuffer()
        mainStr.append("$queryStr where 1=1")
        if (queryMap != null){
            queryMap.keys.map { k ->
                if (req.getParameter(k) != null) {
                    mainStr.append(" ${queryMap[k]!!.replace("#", req.getParameter(k))}")
                }
                if ("append" == k) mainStr.append(" ${queryMap["append"]}")
            }
        }
        log.info(">>>mainStr:$mainStr")
        var query: Query? = null
        if (nativeQuery) query = this.entityManager.createNativeQuery(mainStr.toString()) else query = this.entityManager.createQuery(mainStr.toString())
        //分页
        val currentPage = req.getParameter("currentPage")
        val pageSize = req.getParameter("pageSize")
        if (currentPage != null && pageSize != null){
            query.firstResult = currentPage.toInt() * pageSize.toInt()
            query.maxResults = pageSize.toInt()
        } else {
            query.firstResult = 0
            query.maxResults = 10
        }
        //结果集
        val mainResult = query.resultList as List<Any>
        var list = ArrayList<Any>()
        var mark = true
        if (mainResult.isNotEmpty()) {
            val first = mainResult[0]
            if (first.javaClass.isArray) {
                list = mainResult as ArrayList<Any>
                mark = false
            } else {
                if (excludeFields != null) {
                    mainResult.map { mb ->
                        list.add(commUtil.entityToMap(mb, excludeFields))
                    }
                } else {
                    list = mainResult as ArrayList<Any>
                }
            }
        }
        //总数
        val countStr = if (mark) "select count(1) $mainStr" else "select count(1) from${mainStr.split("from")[1]}"
        log.info(">>>countStr:$countStr")
        var ctQuery: Query? = null
        if (nativeQuery) ctQuery = this.entityManager.createNativeQuery(countStr.toString()) else ctQuery = this.entityManager.createQuery(countStr.toString())
        val total = ctQuery.singleResult.toString().toInt()
        entityManager.close()
        //返回
        val resultMap = HashMap<String, Any>()
        resultMap["resultCode"] = 0
        resultMap["list"] = list
        resultMap["total"] = total
        return resultMap
    }

    /**
     * queryMap有几个特定的key:mainStr,append1..n,orderBy
     */
    fun handleNativeQuery(result: HashMap<String, Any>, req: HttpServletRequest, queryMap: Map<String, String>?): Map<String, Any> {
        val mainStr = StringBuffer()
        if (queryMap != null){
            mainStr.append("${queryMap["mainStr"]} where 1=1")
            queryMap.keys.map { k ->
                if (req.getParameter(k) != null) {
                    mainStr.append(" ${queryMap[k]!!.replace("#", req.getParameter(k))}")
                }
                if (k.indexOf("append") >= 0) mainStr.append(" ${queryMap[k]}")
            }
            mainStr.append(" ${queryMap["orderBy"]}")
        }
        log.info(">>>mainStr:$mainStr")
        val query = this.entityManager.createNativeQuery(mainStr.toString())
        //分页
        val currentPage = req.getParameter("currentPage")
        val pageSize = req.getParameter("pageSize")
        if (currentPage != null && pageSize != null){
            query.firstResult = currentPage.toInt() * pageSize.toInt()
            query.maxResults = pageSize.toInt()
        } else {
            query.firstResult = 0
            query.maxResults = 10
        }
        //结果集
        val mainResult = query.resultList as List<Any>
        //总数
        val countStr = "select count(1) from${mainStr.split("from")[1]}"
        log.info(">>>countStr:$countStr")
        val ctQuery = this.entityManager.createNativeQuery(countStr.toString())
        val total = ctQuery.singleResult.toString().toInt()
        entityManager.close()
        //返回
        result["list"] = mainResult
        result["total"] = total
        return result
    }

    fun dataLevelStr(uid: Long, acctKey: String, dptKey: String, orgKey: String): String {
        val uAcct = acctRepo.findOne(uid)
        val dataLevelStr = StringBuffer()
        if ("业务员" == uAcct.dataLevel) dataLevelStr.append("and $acctKey = '${uAcct.id}'")
        else if ("部门" == uAcct.dataLevel) dataLevelStr.append("and $dptKey = '${uAcct.fkDpt.id}'")
        else if ("机构" == uAcct.dataLevel) dataLevelStr.append("and $orgKey = '${uAcct.fkDpt.fkOrg.id}'")
        else dataLevelStr.append("")
        return dataLevelStr.toString()
    }
}