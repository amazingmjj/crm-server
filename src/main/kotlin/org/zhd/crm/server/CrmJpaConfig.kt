package org.zhd.crm.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "crmEntityManagerFactory", transactionManagerRef = "crmTransactionManager", basePackages = arrayOf("org.zhd.crm.server.repository.crm"))
class CrmJpaConfig {
    @Autowired
    private lateinit var jpaProperties: JpaProperties

    @Autowired
    @Qualifier("crmDataSource")
    private lateinit var crmDataSource: DataSource

    @Bean("crmEntityManagerFactory")
    @Primary
    fun crmEntityManagerFactory(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean = builder.dataSource(crmDataSource).properties(getVendorProperties(crmDataSource)).packages("org.zhd.crm.server.model.crm", "org.zhd.crm.server.model.common").persistenceUnit("CrmPersistenceUnit").build()

    private fun getVendorProperties(dataSource: DataSource): Map<String, String> {
//        val props = HashMap<String, String>()
//        props["hibernate.default_schema"] = dbName
//        jpaProperties.properties = props
        return jpaProperties.getHibernateProperties(dataSource)
    }

    @Bean("crmTransactionManager")
    @Primary
    fun crmTransactionManager(builder: EntityManagerFactoryBuilder): PlatformTransactionManager = JpaTransactionManager(crmEntityManagerFactory(builder).`object`)

}