package org.zhd.crm.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "statisticEntityManagerFactory", transactionManagerRef = "statisticTransactionManager", basePackages = arrayOf("org.zhd.crm.server.repository.statistic"))
class StatisticJpaConfig {
    @Autowired
    private lateinit var jpaProperties: JpaProperties

    @Autowired
    @Qualifier("statisticDataSource")
    private lateinit var statisticDataSource: DataSource

    @Value("\${spring.statistic.datasource.schema}")
    private var dbSchema=""

    @Bean("statisticEntityManagerFactory")
    fun statisticEntityManagerFactory(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean = builder.dataSource(statisticDataSource).properties(getVendorProperties(statisticDataSource)).packages("org.zhd.crm.server.model.statistic", "org.zhd.crm.server.model.common").persistenceUnit("StatisticPersistenceUnit").build()

    private fun getVendorProperties(dataSource: DataSource): Map<String, String> {
        val props = jpaProperties.properties
        props["hibernate.default_schema"] = dbSchema
//        props.keys.map { k ->
//            println("key:>>$k")
//            println("value>>${props[k]}")
//        }
//        jpaProperties.properties = props
        return jpaProperties.getHibernateProperties(dataSource)
    }

    @Bean("statisticTransactionManager")
    fun statisticTransactionManager(builder: EntityManagerFactoryBuilder): PlatformTransactionManager = JpaTransactionManager(statisticEntityManagerFactory(builder).`object`)

}