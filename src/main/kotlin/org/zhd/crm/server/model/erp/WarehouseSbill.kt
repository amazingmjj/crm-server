package org.zhd.crm.server.model.erp

import java.util.*
import javax.persistence.*

@Entity
@Table(name="warehouse_sbill")
class WarehouseSbill {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="sbill_id")
    var sbillId: Long? = null

    @Column(name="sbill_billcode")
    var sbillBillcode: String? = null

    @Column(name="sbill_date")
    lateinit var sbillDate: Date
}