package org.zhd.crm.server.model.common

import java.io.Serializable
import java.sql.Timestamp
import java.util.*
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@MappedSuperclass
open class BaseModel : Serializable {
    var createAt: Timestamp? = null
    lateinit var updateAt: Timestamp

    @PrePersist
    fun setInitValue() {
        if (this.javaClass.name.indexOf("Customer") > 0 && this.createAt != null) {
            // 用于客户批量导入
        } else {
            this.createAt = Timestamp(Date().time)
        }
        this.updateAt = Timestamp(Date().time)
    }

    @PreUpdate
    fun setUpdateAt() {
        this.updateAt = Timestamp(Date().time)
    }

}