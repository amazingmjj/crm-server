package org.zhd.crm.server.model.crm

import com.fasterxml.jackson.annotation.JsonIgnore
import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "t_linker")
class Linker(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var name: String = ""
    @Column(nullable = false)
    var phone: String = ""
    // 1男 2女 3未知
    @Column(columnDefinition = "int default 1")
    var sex: Int = 1
    var age: Int = 0
    // 学历
    var edu: String? = null
    // 籍贯
    var nativePlace: String? = null
    // 微信号
    var wxNo: String? = null
    // QQ号
    var qqNo: String? = null
    // 微博名称
    var wbName: String? = null
    // 其他联系方式
    var otherLinkWay: String? = null
    // 是否是主联系人 0 不是 1是
    @Column(columnDefinition = "int default 0")
    var mainStatus: Int = 0
    /**
     * 常用联系人
     * @author samy
     * @date 2019/08/19
     * @content 0 为非常用 1 为常用联系人 (每个
     */
    @Transient
    var commMark: Int = 0
    // 职位
    var position: String? = null
    @JsonIgnore
    @ManyToMany(mappedBy = "linkers")
    lateinit var customers: Set<Customer>

    @ManyToOne
    lateinit var creator: Account

    @Column(columnDefinition = "varchar2(255) default ''")
    var remark: String? = null

}