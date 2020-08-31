package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_login_msg")
class LoginMsg(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel(){
    //账号id
    var acctId: Long? = null
    //登录账号
    var loginAcct: String = ""
    //姓名
    var acctName: String = ""
    //登录时间
    var loginDate: Timestamp? = null
    //登录IP
    var ip: String = ""
    //设备标识(浏览器/And/Ios)
    var deviceType: String = ""
}