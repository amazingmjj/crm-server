package org.zhd.crm.server.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.zhd.crm.server.dubbo.GoodStockDubboService
import org.zhd.crm.server.service.crm.*
import org.zhd.crm.server.util.CommUtil
import javax.servlet.http.HttpServletRequest

@Controller
open class CrmBaseController {
    @Autowired
    protected lateinit var request: HttpServletRequest
    @Autowired
    protected lateinit var commUtil: CommUtil
    @Autowired
    protected lateinit var accountService: AccountService
    @Autowired
    protected lateinit var basicDataService: BasicDataService
    @Autowired
    protected lateinit var settingService: SettingService
    @Autowired
    protected lateinit var customerService: CustomerService
    @Autowired
    protected lateinit var callCenterService: CallCenterService
    @Autowired(required = false)
    protected lateinit var salesManageService: SalesManageService
    @Autowired(required = false)
    protected lateinit var goodStockDubboService: GoodStockDubboService
    @Autowired
    protected lateinit var teamTaskService: TeamTaskService
    @Autowired
    protected lateinit var busiRelationService: BusiRelationService
    @Autowired
    lateinit var menuAuthService: MenuAuthService
    @Autowired
    lateinit var menuService: MenuService

}