package org.zhd.crm.server.model.crm

import com.fasterxml.jackson.annotation.JsonIgnore
import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_role")
class Role(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    @Column(unique = true, nullable = false)
    var name: String = ""

    @JsonIgnore
    @OneToMany(mappedBy = "fkRole")
    lateinit var accts: Set<Account>

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "ref_role_auth")
    lateinit var auths: Set<Auth>

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "ref_role_extra_auth")
    var extraAuths: Set<ExtraAuth>? = null

    @Column(columnDefinition = "int default 1")
    var status: Int = 1
}