package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "t_team_task")//任务信息表
class TeamTask(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    //任务名称
    var taskName: String = ""
    //任务描述
    @Column(columnDefinition = "varchar2(4000)")
    var description: String? = null
    //任务类型
    @ManyToOne
    lateinit var fkTaskType: TaskType
    //预计消耗时间
    var estimatedTime: Double = 0.0
    //实际消耗时间
    var actualTime: Double = 0.0
    //任务状态：0待处理 1暂不处理 2进行中 3开发完成 4待验证 5测试中 6已完成 7退回 8关闭
    var status: Int = 0
    //所属：0开发 1测试
    var belong: Int = 0
    //优先级 0次要 1一般 2严重 3紧急 4致命
    var priority: Int = 0
    //交付时间（规定）
    var deliveryTime: Timestamp? = null
    //开始时间（状态变为进行中，测试中）
    var startTime: Timestamp? = null
    //结束时间（状态变为开发完成，已完成，退回，关闭）
    var endTime: Timestamp? = null
    //项目名称
    @ManyToOne
    lateinit var fkProject: Project
    //指派人
    @ManyToOne
    lateinit var reporter: Account
    //经办人
    @ManyToOne
    lateinit var operator: Account
    //原因说明
    var reason: String? = null
    //父id
    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    var fkParent: TeamTask? = null
    //子状态（当存在父节点时）0待处理 1暂不处理 2进行中 3开发完成 4待验证 5测试中 6已完成 7退回 8关闭
    var subStatus: Int = -1
    //图片url
    var picUrl: String? = null
    //文件url
    var fileUrl: String? = null
    //原始文件名
    var fileName: String? = null
}