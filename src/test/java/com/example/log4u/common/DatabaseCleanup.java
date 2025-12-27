package com.example.log4u.common;

import static org.testcontainers.shaded.com.google.common.base.CaseFormat.*;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanup {

	private final EntityManagerFactory mysqlEmf;
	private final EntityManagerFactory postgresEmf;

	public DatabaseCleanup(
		@Qualifier("mysqlEntityManagerFactory") EntityManagerFactory mysqlEmf,
		@Qualifier("postgresqlEntityManagerFactory") EntityManagerFactory postgresEmf) {
		this.mysqlEmf = mysqlEmf;
		this.postgresEmf = postgresEmf;
	}

	public void executeAll() {
		cleanupOne(mysqlEmf);
		cleanupOne(postgresEmf);
	}

	private void cleanupOne(EntityManagerFactory entityManagerFactory) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();

			entityManager.flush();
			entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
			List<String> tableNames = extractTableNames(entityManagerFactory);
			for (String table : tableNames) {
				entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
			}
			entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

			entityManager.getTransaction().commit();
	}

	private List<String> extractTableNames(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.getMetamodel().getEntities().stream()
			.filter(e -> e.getJavaType().isAnnotationPresent(Entity.class))
			.map(this::resolveTableName)
			.distinct()
			.toList();
	}

	private String resolveTableName(EntityType<?> entityType) {
		Class<?> javaType = entityType.getJavaType();
		Table table = javaType.getAnnotation(Table.class);

		if (table != null && !table.name().isEmpty()) {
			return table.name();
		}
		return UPPER_CAMEL.to(LOWER_UNDERSCORE, javaType.getSimpleName());
	}
}
