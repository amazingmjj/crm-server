package org.zhd.crm.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.zhd.crm.server.interceptor.CsrfInterceptor

@Configuration
class InterceptorConfig : WebMvcConfigurerAdapter() {
    @Autowired
    private lateinit var csrfInterceptor: CsrfInterceptor

    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry?.addInterceptor(csrfInterceptor)
                ?.excludePathPatterns("/test/**", "/file/download/**", "/file/export/**", "/webjars/**", "/swagger-resources/**", "/swagger**", "/api/**")
                ?.addPathPatterns("/**")
    }
}