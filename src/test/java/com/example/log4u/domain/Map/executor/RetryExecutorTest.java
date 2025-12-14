package com.example.log4u.domain.Map.executor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.map.service.MapService;

@ActiveProfiles("test")
@SpringBootTest
public class RetryExecutorTest {

	@MockitoSpyBean
	private RetryExecutor retryExecutor;

	@Autowired
	private MapService mapService;

	@DisplayName("다이어리 클러스터 조회 시 RetryExecutor가 호출된다.")
	@Test
	void retryShouldBeAppliedOnGetDiaryClusters() {
		// given
		String geohash = "wyd";
		int level = 1;

		// when
		mapService.getClustersByRedisCache(geohash, level);

		// then
		verify(retryExecutor, atLeastOnce()).runWithRetry(any());
	}

	@DisplayName("다이어리 마커 조회 시 RetryExecutor가 호출된다.")
	@Test
	void retryShouldBeAppliedOnGetDiaryMarkers() {
		// given
		String geohash = "wyd4k";

		// when
		mapService.getMarkersByRedisCache(geohash);

		// then
		verify(retryExecutor, atLeastOnce()).runWithRetry(any());
	}
}
