package com.example.log4u.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.log4u.common.infra.cache.CacheManager;

@Import(RedisTestContainersConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class ServiceTest {

	@Autowired
	protected CacheManager cacheManager;

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@BeforeEach
	protected void setUp() {
		cacheManager.init();
		databaseCleanup.executeAll();
	}
}
