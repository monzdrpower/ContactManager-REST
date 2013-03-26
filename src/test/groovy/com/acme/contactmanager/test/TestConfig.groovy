package com.acme.contactmanager.test;


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import org.springframework.orm.hibernate4.HibernateTransactionManager
import org.springframework.orm.hibernate4.LocalSessionFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.EnableWebMvc


@Configuration
@ComponentScan(['net.schastny.contactmanager.dao', 'com.acme.contactmanager.dao', 'net.schastny.contactmanager.web', 'net.schastny.contactmanager.service'])
@EnableTransactionManagement
@PropertySource('classpath:testdb.properties')
@ImportResource('classpath:security.xml')
@EnableWebMvc
class TestConfig {

	@Value('${db.user.name}')
	String userName

	@Value('${db.user.pass}')
	String userPass

	@Bean
	public LocalSessionFactoryBean sessionFactory() {

		LocalSessionFactoryBean bean = new LocalSessionFactoryBean()

		bean.packagesToScan = [
			"com.acme.contactmanager.domain",
			'net.schastny.contactmanager.domain'] as String[]

		Properties props = new Properties()
		props."hibernate.connection.driver_class" = "org.h2.Driver"
		props."hibernate.connection.url" = "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE"
		props."hibernate.connection.username" = userName
		props."hibernate.connection.password" = userPass
		props."hibernate.dialect" = "org.hibernate.dialect.H2Dialect"
		props."hibernate.hbm2ddl.auto" = "create-drop"

		// кстати вот эта штука помогает, когда в тестах тормозит старт контекста на получении метаданных из БД (замечено на Постгрес)
		// http://stackoverflow.com/questions/10075081/hibernate-slow-to-acquire-postgres-connection
		props."hibernate.temp.use_jdbc_metadata_defaults" = "false"

		bean.hibernateProperties = props
		
		bean
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager txManager = new HibernateTransactionManager()
		txManager.autodetectDataSource = false
		txManager.sessionFactory = sessionFactory().object
		txManager
	}
}
