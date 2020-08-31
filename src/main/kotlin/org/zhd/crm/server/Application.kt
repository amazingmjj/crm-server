package org.zhd.crm.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAsync
class Application : SpringBootServletInitializer {
    constructor()

//    @Autowired
//    private lateinit var accessFilter: AccessFilter
//
//    @Bean
//    fun filterRegistration(): FilterRegistrationBean {
//        val registration = FilterRegistrationBean()
//        registration.addUrlPatterns("/*")
//        registration.filter = accessFilter
//        registration.order = Ordered.LOWEST_PRECEDENCE
//        return registration
//    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
