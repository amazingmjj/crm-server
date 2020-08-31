package org.zhd.crm.server.repository.crm

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Dictionary

interface DictionaryRepository : CrudRepository<Dictionary, Long> {
    @Query(value = "select dict.value from Dictionary as dict where dict.name = ?1 and dict.type = 0")
    fun findByName(name: String): String?

    @Query(value = "from Dictionary as dict where dict.type = 1")
    fun findSinaDict(): List<Dictionary>
}