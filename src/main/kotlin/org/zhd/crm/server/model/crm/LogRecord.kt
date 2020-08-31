package org.zhd.crm.server.model.crm

import org.zhd.crm.server.model.common.BaseModel
import javax.persistence.*

@Entity
@Table(name = "t_log_record")
class LogRecord(@Id @GeneratedValue(strategy = GenerationType.TABLE) var id: Long? = null) : BaseModel() {
    var reqUri: String? = null
    // 请求类型
    var reqMethod: String? = null
    // 调用类型
    var classMethod: String? = null
    // 请求地址
    var ipAddr: String? = null
    // 入参
    @Lob
    var inParams: String? = null
    // 出参
    @Lob
    var outParams: String? = null
    // 请求时间
    var elapsedTime: Long? = null
    // 状态 0 成功 -1 失败
    var status: Int? = null
    // 失败原因
    @Lob
    var errMsg: String? = null
    // 日志描述
    var description: String? = null

    constructor(classMethod: String, reqURI: String, reqMethod: String, ipAddr: String) : this() {
        this.classMethod = classMethod
        this.reqUri = reqURI
        this.reqMethod = reqMethod
        this.ipAddr = ipAddr
    }
}