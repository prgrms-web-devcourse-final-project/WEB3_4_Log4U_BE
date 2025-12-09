package com.example.log4u.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
@EnableJpaRepositories(
	basePackages = {
		"com.example.log4u.domain.map.repository"
	},
	entityManagerFactoryRef = "postgresqlEntityManagerFactory",
	transactionManagerRef = "postgresqlTransactionManager"
)
public class PostgreSqlConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.second-datasource")
	public HikariDataSource postgresqlHikariDataSource() {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "postgresqlDataSource")
	public DataSource postgresqlDataSource(
		@Qualifier("postgresqlHikariDataSource") HikariDataSource hikariDataSource
	) {
		return new LazyConnectionDataSourceProxy(hikariDataSource);
	}

	@Bean(name = "postgresqlEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean postgresqlEntityManagerFactory(
		@Qualifier("postgresqlDataSource") DataSource dataSource
	) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("com.example.log4u.domain.map.entity");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(true);
		vendorAdapter.setGenerateDdl(true);
		em.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.format_sql", true);
		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean(name = "postgresqlTransactionManager")
	public PlatformTransactionManager postgresqlTransactionManager(
		@Qualifier("postgresqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean postgresqlEmf
	) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(postgresqlEmf.getObject());
		return transactionManager;
	}
}
