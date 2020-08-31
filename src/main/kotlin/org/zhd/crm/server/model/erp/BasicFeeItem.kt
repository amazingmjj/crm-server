package org.zhd.crm.server.model.erp

import javax.persistence.*

@Entity
@Table(name="BASIC_FEEITEM")
public class BasicFeeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="FEEITEM_ID")
    var id: Long? = null

    @Column(name="feeitem_code")
    var code: String? = null

    @Column(name="feeitem_name")
    var feeItemName: String =""

    @Column(name="feeclass_name")
    var feeClassName: String =""

    @Column(name="feeitem_inprice")
    var feeInPrice: Double=0.00

    @Column(name="feeitem_exprice")
    var feeExPrice: Double=0.00
}