package com.example.log4u.domain.Map.executor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.log4u.common.RedisTestContainersConfig;
import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.cache.ClustersCacheService;
import com.example.log4u.domain.map.cache.MarkersCacheService;

@Import(RedisTestContainersConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public class RetryExecutorTest {

	@MockitoSpyBean
	private RetryExecutor retryExecutor;

	@Autowired
	private ClustersCacheService clustersCacheService;

	@Autowired
	private MarkersCacheService markersCacheService;

	@DisplayName("지역 클러스터 목록을 불러오는 경우, RetryExecutor가 호출된다.")
	@Test
	void retryShouldBeAppliedOnGetDiaryClusters() {
		// given
		String geohash = "wyd";
		int level = 1;

		// when
		clustersCacheService.getClusters(geohash, level);

		// then
		verify(retryExecutor, atLeastOnce()).runWithRetry(any());
	}

	@DisplayName("마커 목록을 불러오는 경우, RetryExecutor가 호출된다.")
	@Test
	void retryShouldBeAppliedOnGetDiaryMarkers() {
		// given
		String geohash = "wyd4k";

		// when
		markersCacheService.getMarkers(geohash);

		// then
		verify(retryExecutor, atLeastOnce()).runWithRetry(any());
	}
}
