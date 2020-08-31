package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_task_coefficient")//任务系数表
class TaskCoefficient(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null): BaseModel() {
    //数值 一次成功,一次再修改,多次再修改
    var value: String = "1.0,0.8,0.5"
    //类型 0自定义 1项目
    var type: Int = 0
    //状态 0启用 1停用
    var status: Int = 0
    //项目名称
    @OneToOne
    var fkProject: Project? = null
    //任务编号
    var taskId: Long? = null
    //备注
    var remark: String? = null
}