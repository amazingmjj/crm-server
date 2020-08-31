package org.zhd.crm.server

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.command.ActiveMQQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerContainerFactory

@Configuration
@EnableJms
open class ActiveMqConfig {
    @Bean
    fun xyCustomChangeQueue() = ActiveMQQueue(GlobalConfig.Activemq.MQ_CRM2XY_NAME)

    @Bean
    fun erpCustomChangeQueue() = ActiveMQQueue(GlobalConfig.Activemq.MQ_CRM2ERP_NAME)

    @Bean
    fun crmDelayQueue() = ActiveMQQueue(GlobalConfig.Activemq.MQ_CRM_DELAY_NAME)

    @Bean
    fun jmsListenerContainerTopic(connectionFactory: ActiveMQConnectionFactory): JmsListenerContainerFactory<*> {
        val factory = DefaultJmsListenerContainerFactory()
        factory.setPubSubDomain(true)
        factory.setConnectionFactory(connectionFactory)
        return factory
    }

}