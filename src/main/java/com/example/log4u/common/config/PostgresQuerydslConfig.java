package com.example.log4u.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class PostgresQuerydslConfig {

	@PersistenceContext(unitName = "postgresqlEntityManagerFactory")
	private EntityManager postgresEntityManager;

	@Bean(name = "postgresJPAQueryFactory")
	public JPAQueryFactory postgresJPAQueryFactory() {
		return new JPAQueryFactory(postgresEntityManager);
	}
}
