package org.zhd.crm.server

import com.alibaba.fastjson.JSON
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.zhd.crm.server.controller.crm.CustomerManageController
import org.zhd.crm.server.model.crm.Customer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CstmControllerTest {
    @Autowired
    lateinit var controller: CustomerManageController
    @Test
    @Throws(Exception::class)
    fun customerFindById() { //存在懒加载问题，加fetch=FetchType.EAGER 或者 初始化字段值
        val result = controller.customerFindById(162427)
        System.out.println("returnCode:${result["returnCode"]}")
        if (result["returnCode"] == 0){
            val obj = result["obj"] as Customer
            System.out.println("obj:${JSON.toJSONString(obj)}")
        }
        else System.out.println("errMsg:${result["errMsg"]}")
    }
}