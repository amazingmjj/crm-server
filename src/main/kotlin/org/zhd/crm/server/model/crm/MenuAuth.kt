package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_menu_auth")
class MenuAuth (@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel(){
    //菜单
    @ManyToOne
    lateinit var menu: Menu
    //权限名
    @Column(name = "auth_name",columnDefinition = "varchar2(30)")
    lateinit var authName : String
    //权限编号
    @Column(name = "auth_code",columnDefinition = "varchar2(30)")
    lateinit var authCode : String
    //样式名称
    @Column(name = "css_name",columnDefinition = "varchar2(30)")
    lateinit var cssName : String
    //方法名称
    @Column(name = "method_name",columnDefinition = "varchar2(30)")
    var methodName : String? = null

}