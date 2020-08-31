package org.zhd.crm.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.socket.server.standard.ServerEndpointExporter

@Configuration
@Profile("dev")
class WebSocketConfig {
    @Bean
    fun serverEndpointExporter(): ServerEndpointExporter = ServerEndpointExporter()
}