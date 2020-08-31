package org.zhd.crm.server.controller.crm

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.zhd.crm.server.controller.CrmBaseController

@RestController
class LoginController : CrmBaseController() {
    private val log = LoggerFactory.getLogger(LoginController::class.java)
    @PostMapping("login")
    fun login(acct: String, pwd: String): Map<String, Any> {
        val result = HashMap<String, Any>()
        var accout = accountService.findByLoginAcct(acct)
        if (accout == null) {
            result.put("errMsg", "账户不存在")
            result.put("returnCode", -1)
        } else {
            if (accout.status == 0) {
                result.put("returnCode", -1)
                result.put("errMsg", "账号已被禁用")
            } else if (accout.pwd == pwd) {
                if(acct=="admin"){
                    accout.extraAuths=menuAuthService.findAllExtraAuth(accout)
                }
                result.put("returnCode", 0)
                result.put("currentUser", accout)
                result.put("errMsg", "登录成功")
            } else {
                result.put("returnCode", -1)
                result.put("errMsg", "用户名或密码错误")
            }
        }
        return result
    }

    @PostMapping("admin/resetPwd")
    fun adminResetPwd(id: Long): Map<String, Any> {
        val result = HashMap<String, Any>()
        val acct = settingService.acctFindById(id)
        acct.pwd = "1f82c942befda29b6ed487a51da199f78fce7f05"
        settingService.acctSave(acct)
        result.put("returnCode", 0)
        return result
    }
}