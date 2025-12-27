package com.example.log4u.domain.Map.executor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.log4u.common.RedisTestContainersConfig;
import com.example.log4u.common.executor.DistributedLockExecutor;
import com.example.log4u.common.infra.cache.CacheManager;
import com.example.log4u.domain.map.cache.ClustersCacheService;
import com.example.log4u.domain.map.cache.MarkersCacheService;

@Import(RedisTestContainersConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class DistributedLockExecutorTest {

	@MockitoSpyBean
	private DistributedLockExecutor distributedLockExecutor;

	@Autowired
	private ClustersCacheService clustersCacheService;

	@Autowired
	private MarkersCacheService markersCacheService;

	@MockitoSpyBean
	private CacheManager cacheManager;

	@BeforeEach
	protected void setUp() {
		cacheManager.init();
		when(cacheManager.tryLock(anyString())).thenReturn(true);
	}

	@DisplayName("지역 클러스터 목록 캐시를 갱신하는 경우, DistributedLockExecutor가 호출된다.")
	@Test
	void distributedLockExecutorShouldBeAppliedForClusters() {
		// given
		String geohash = "wyd";
		int level = 1;

		// when
		clustersCacheService.refresh(geohash, level);

		// then
		verify(distributedLockExecutor, atLeastOnce()).runWithLock(anyString(), (Supplier<Object>)any());
	}

	@DisplayName("다이어리 마커 캐시를 로드/갱신하는 경우, DistributedLockExecutor가 호출된다.")
	@Test
	void distributedLockExecutorShouldBeAppliedForMarkers() {
		// given
		String geohash = "wyd4k";

		// when
		markersCacheService.refresh(geohash);

		// then
		verify(distributedLockExecutor, atLeastOnce()).runWithLock(anyString(), (Supplier<Object>)any());
	}

	@DisplayName("DistributedLockExecutor 실행 시 분산락을 획득하고 해제한다.")
	@Test
	void distributedLockExecutorShouldTryLockAndRelease() {
		// given
		String lockKey = "lockKey";

		// when
		distributedLockExecutor.runWithLock(lockKey, () -> {
		});

		// then
		verify(cacheManager).tryLock(anyString());
		verify(cacheManager).releaseLock(anyString());
	}
}
