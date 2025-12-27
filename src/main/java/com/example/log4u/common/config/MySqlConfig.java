package com.example.log4u.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(
	basePackages = {
		"com.example.log4u.common",
		"com.example.log4u.domain.comment",
		"com.example.log4u.domain.diary",
		"com.example.log4u.domain.follow",
		"com.example.log4u.domain.like",
		"com.example.log4u.domain.media",
		"com.example.log4u.domain.reports",
		"com.example.log4u.domain.supports",
		"com.example.log4u.domain.user",
		"com.example.log4u.domain.subscription",
		"com.example.log4u.domain.hashtag"
	},
	entityManagerFactoryRef = "mysqlEntityManagerFactory",
	transactionManagerRef = "mysqlTransactionManager"
)
public class MySqlConfig {

	@Bean(name = "mysqlDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties mysqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "mysqlHikariConfig")
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig mysqlHikariConfig() {
		return new HikariConfig();
	}

	@Bean(name = "mysqlDataSource")
	@Primary
	public DataSource mysqlDataSource(
		@Qualifier("mysqlDataSourceProperties") DataSourceProperties properties,
		@Qualifier("mysqlHikariConfig") HikariConfig hikariConfig
	) {
		HikariDataSource dataSource = properties.initializeDataSourceBuilder()
				.type(HikariDataSource.class)
				.build();

		dataSource.setMaximumPoolSize(hikariConfig.getMaximumPoolSize());
		dataSource.setMinimumIdle(hikariConfig.getMinimumIdle());
		dataSource.setConnectionTimeout(hikariConfig.getConnectionTimeout());
		dataSource.setIdleTimeout(hikariConfig.getIdleTimeout());
		dataSource.setMaxLifetime(hikariConfig.getMaxLifetime());

		return new LazyConnectionDataSourceProxy(dataSource);
	}

	@Bean(name = "mysqlEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(@Qualifier("mysqlDataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan(
			"com.example.log4u.common",
			"com.example.log4u.domain.comment",
			"com.example.log4u.domain.diary",
			"com.example.log4u.domain.follow",
			"com.example.log4u.domain.like",
			"com.example.log4u.domain.media",
			"com.example.log4u.domain.reports",
			"com.example.log4u.domain.supports",
			"com.example.log4u.domain.user",
			"com.example.log4u.domain.subscription",
			"com.example.log4u.domain.hashtag");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(true);
		vendorAdapter.setGenerateDdl(true);
		em.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.format_sql", true);
		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean(name = "mysqlTransactionManager")
	@Primary
	public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean mysqlEmf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(mysqlEmf.getObject());
		return transactionManager;
	}
}
