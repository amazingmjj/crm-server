package org.zhd.crm.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
@EnableJpaRepositories(entityManagerFactoryRef = "erpEntityManagerFactory", transactionManagerRef = "erpTransactionManager", basePackages = arrayOf("org.zhd.crm.server.repository.erp"))
class ErpJpaConfig {
    @Autowired
    private lateinit var jpaProperties: JpaProperties

    @Autowired
    @Qualifier("erpDataSource")
    private lateinit var erpDataSource: DataSource

    @Bean("erpEntityManagerFactory")
    fun erpEntityManagerFactory(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean = builder.dataSource(erpDataSource).properties(getVendorProperties(erpDataSource)).packages("org.zhd.crm.server.model.erp", "org.zhd.crm.server.model.common").persistenceUnit("ErpPersistenceUnit").build()

    private fun getVendorProperties(dataSource: DataSource): Map<String, String> {
//        val props = HashMap<String, String>()
//        props["hibernate.default_schema"] = dbName
//        jpaProperties.properties = props
        return jpaProperties.getHibernateProperties(dataSource)
    }

    @Bean("erpTransactionManager")
    fun erpTransactionManager(builder: EntityManagerFactoryBuilder): PlatformTransactionManager = JpaTransactionManager(erpEntityManagerFactory(builder).`object`)
}