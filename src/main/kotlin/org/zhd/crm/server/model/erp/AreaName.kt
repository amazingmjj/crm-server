package org.zhd.crm.server.model.erp

import javax.persistence.*

@Entity
@Table(name = "BASIC_AREA")
class AreaName {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "AREA_ID")
    var id: Long = 0
    @Column(name = "AREA_NAME")
    var name: String? = null
}