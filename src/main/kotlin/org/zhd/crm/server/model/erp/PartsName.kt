package org.zhd.crm.server.model.erp

import javax.persistence.*

@Entity
@Table(name="BASIC_PARTSNAME")
class PartsName {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="PARTSNAME_ID")
    var id: Long? = null
    @Column(name="PARTSNAME_NAME")
    var name: String? = null
}