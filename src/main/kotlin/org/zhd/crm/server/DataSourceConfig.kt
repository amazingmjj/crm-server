package org.zhd.crm.server

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DataSourceConfig {
    @Bean("crmDataSource")
    @Primary
    @Qualifier("crmDataSource")
    @ConfigurationProperties(prefix = "spring.crm.datasource")
    fun crmDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean("statisticDataSource")
    @Qualifier("statisticDataSource")
    @ConfigurationProperties(prefix = "spring.statistic.datasource")
    fun statisticDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean("erpDataSource")
    @Qualifier("erpDataSource")
    @ConfigurationProperties(prefix = "spring.erp.datasource")
    fun erpDataSource(): DataSource = DataSourceBuilder.create().build()
}