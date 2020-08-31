package org.zhd.crm.server

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.zhd.crm.server.controller.crm.CustomerManageController

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CstmControllerMockTest {
    @Autowired
    lateinit var mvc: MockMvc
    @Autowired
    lateinit var cstmController: CustomerManageController
    @Before
    fun setUp(){
        mvc = MockMvcBuilders.standaloneSetup(cstmController).build()
    }
    @Test
    @Throws(Exception::class)
    fun getLinker() { //存在懒加载问题，加fetch=FetchType.EAGER 或者 初始化字段值
        val mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/customerManage/linker/162428").accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
        System.out.println(">>>输出:${mvcResult.getResponse().getContentAsString()}")
    }

    @Test
    @Throws(Exception::class)
    fun getHome() {
        val mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/customerManage/home/1").accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
        System.out.println(">>>输出:${mvcResult.getResponse().getContentAsString()}")
    }
}
