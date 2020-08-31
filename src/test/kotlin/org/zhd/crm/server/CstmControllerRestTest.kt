package org.zhd.crm.server

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.zhd.crm.server.service.crm.SettingService
import org.zhd.crm.server.util.CommUtil
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//指定web环境，随机端口
class CstmControllerRestTest {
    @LocalServerPort
    var port: Int = 0
    lateinit var base: URL
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate;
    @Autowired
    lateinit var settingService: SettingService
    @Autowired
    lateinit var commUtil: CommUtil
    @Before
    fun setUp() {
        this.base = URL("http://localhost:$port/")
    }

    @Test
    fun testGet(){
        val url = base.toString()+"customerManage/linker/162428"
        val headers = HttpHeaders()
        headers.set("zhdcrm-proxy-token",getToken())
        val httpEntity = HttpEntity<String>(null,headers)
        System.out.println(">>>>$url")
        val context = testRestTemplate.exchange(url,HttpMethod.GET,httpEntity,String::class.java)
        System.out.println(">>>>$context")
    }

    @Test
    fun getOppty(){
        val url = base.toString()+"mobile/busiOppty/206111"
        val headers = HttpHeaders()
        headers.set("zhdcrm-proxy-token",getToken())
        val httpEntity = HttpEntity<String>(null,headers)
        System.out.println(">>>>$url")
        val context = testRestTemplate.exchange(url,HttpMethod.GET,httpEntity,String::class.java)
        System.out.println(">>>>$context")
    }

    @Test
    fun updateStatus(){
        val url = base.toString()+"mobile/cstmCall/update"
        val headers = HttpHeaders()
        headers.set("zhdcrm-proxy-token",getToken())
        val map = LinkedMultiValueMap<String,String>()//封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        map["id"] = "249661"
        map["longitude"] = "有个"
        map["latitude"] = "帅哥"
        map["locateAddr"] = "就是"
        map["remark"] = "开发"
        val httpEntity = HttpEntity<MultiValueMap<String, String>>(map,headers)
        System.out.println(">>>>$url")
        val context = testRestTemplate.exchange(url,HttpMethod.POST,httpEntity,String::class.java)
        System.out.println(">>>>$context")
    }

    @Test
    fun testPost(){
        val url = base.toString()+"customerManage/linker/createOrUpdate"

        val headers = HttpHeaders()
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)//提交方式默认表单提交
        headers.set("zhdcrm-proxy-token",getToken())

        val map = LinkedMultiValueMap<String,String>()//封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        map["name"] = "陈帅帅123"
        map["phone"] = "123123111"
        map["id"] = "76319"
        map["cstmId"] = "6057"
        map["uid"] = "1"

        val httpEntity = HttpEntity<MultiValueMap<String, String>>(map,headers)
        System.out.println(">>>>$url")
        val context = testRestTemplate.exchange(url,HttpMethod.POST,httpEntity,String::class.java)
        System.out.println(">>>>$context")
    }

    fun getToken(): String{
        val dateStr = SimpleDateFormat("yyyy-MM-dd").format(Date())
        return commUtil.sha1("${dateStr}zhdcrm")
    }
}
