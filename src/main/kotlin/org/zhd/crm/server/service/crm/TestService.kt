package org.zhd.crm.server.service.crm

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.zhd.crm.server.model.crm.Linker
import org.zhd.crm.server.repository.crm.CustomerRepository
import org.zhd.crm.server.repository.crm.GradeSummaryRepository
import org.zhd.crm.server.repository.crm.LinkerRepository
import org.zhd.crm.server.util.CommUtil
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class TestService {
    @Autowired
    private lateinit var customerRepo: CustomerRepository
    @Autowired
    private lateinit var linkerRepo: LinkerRepository
    @Autowired
    private lateinit var commUtil: CommUtil
    @Autowired
    private lateinit var gradeSummaryRepo: GradeSummaryRepository

    private val log = LoggerFactory.getLogger(TestService::class.java)

    //2019-01-24 分离客户联系人关系
    fun splitLinkAndCstmRelation(){
        val relationList = linkerRepo.findLinkAndCstmRelation()
        if (relationList.isNotEmpty()){
            log.info(">>>relationList size:${relationList.size}")
            val relationMap = relationList.groupBy { m-> m.getLinkPhone() }
            log.info(">>>relationMap size:${relationMap.size}")
            relationMap.keys.map { key->
                val value = relationMap[key]!!//[{},{}]相同的link不同的cstm
                log.info(">>>开始分组修复,key:$key,value size:${value.size}")
                val oldLink = linkerRepo.findOne(value[0].getLinkId())
                value.indices.map { idx->
                    log.info(">>>start idx:$idx")
                    if (idx != 0){
                        val newLink = Linker()//复制旧的联系人信息
                        val newObj = commUtil.autoSetClass(oldLink, newLink, arrayOf("id", "updateAt", "createAt", "customers")) as Linker
                        log.info(">>>oldCstmId:${value[idx].getCstmId()},oldLinkId:${value[idx].getLinkId()}")
                        val cstmObj = customerRepo.findOne(value[idx].getCstmId())//获取目标客户
                        val linkerSet = cstmObj.linkers as MutableSet<Linker>
                        val it = linkerSet.iterator()
                        while (it.hasNext()) {
                            val link = it.next()
                            if (link.id == value[idx].getLinkId()) it.remove()//删除旧的联系人关系
                        }
                        linkerSet.add(newObj)//增加新的联系人关系
                        cstmObj.linkers = linkerSet
                        customerRepo.save(cstmObj)
                        log.info(">>>end idx:$idx")
                    } else {
                        log.info(">>>skip idx:$idx")
                    }
                }
            }
            log.info(">>>split linker & customer relations succeed")
        } else {
            log.info(">>>not need split linker & customer relations")
        }
    }

    // 修复客户分级重复数据
    fun delRepeatData(createAt: String){
        val nameList = gradeSummaryRepo.findRepeatList(createAt)
        val start = Date().time
        if (nameList.isNotEmpty()){
            log.info(">>>时间：$createAt,共有${nameList.size}条重复数据")
            nameList.map { s ->
                val idList = gradeSummaryRepo.findRepeatListByName(createAt, s)
                (1..(idList.size -1)).map { idx ->
                    val delId = idList[idx]
                    log.info(">>>名称：$s,idx：$idx,delId：$delId")
                    gradeSummaryRepo.delete(delId)
                }
            }
        } else {
            log.info(">>>>>>时间：$createAt,没有重复数据")
        }
        val end = Date().time
        log.info(">>>>>>耗时：${(end - start)/1000}秒")
    }

    //处理有两个主联系人的情况
    fun handleMainLink(){
        val codeList = linkerRepo.findLinkerPhone()
        val result = ArrayList<Long>()
        log.info(">>>${codeList.size}")
        codeList.map { code ->
            log.info(">>>$code")
            val list = linkerRepo.findLinkerPhone(code)
            if (list.size > 1){
                result.add(list[0])
            }
        }
        log.info(">>>${result.size}")
        linkerRepo.batchUpdateStatus(0,result)//把第一个主联系人更新
    }
}