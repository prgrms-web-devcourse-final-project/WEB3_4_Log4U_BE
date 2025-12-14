package com.example.log4u.domain.Map.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.MarkerCacheDao;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryClustersResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkersResponse;
import com.example.log4u.domain.map.exception.InvalidGeohashException;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.example.log4u.domain.map.service.MapService;
import com.example.log4u.fixture.DiaryFixture;

@DisplayName("지도 API 단위 테스트")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MapServiceTest {

	@InjectMocks
	private MapService mapService;

	@Mock
	private SidoAreasRepository sidoAreasRepository;

	@Mock
	private SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;

	@Mock
	private SiggAreasRepository siggAreasRepository;

	@Mock
	private SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;

	@Mock
	private MarkerCacheDao markerCacheDao;

	@Mock
	private ClusterCacheDao clusterCacheDao;

	@Mock
	private DiaryGeohashService diaryGeohashService;

	@Mock
	private RetryExecutor retryExecutor;

	private static final String GEOHASH_L3 = "abc";
	private static final String GEOHASH_L5 = "abcde";

	private static final int LEVEL_SIDO = 1;
	private static final int LEVEL_SIGG = 2;

	private final List<GetDiaryClusterResponse> clusters = List.of(
		new GetDiaryClusterResponse("서울", 1L, 37.5665, 126.9780, 10L)
	);

	private final List<GetDiaryMarkerResponse> markers = List.of(
		GetDiaryMarkerResponse.of(DiaryFixture.createDiaryFixture(1L))
	);

	@BeforeEach
	void setUp() {
		given(retryExecutor.runWithRetry(any()))
			.willAnswer(invocation -> {
				@SuppressWarnings("unchecked")
				Supplier<?> supplier = invocation.getArgument(0);
				return supplier.get();
			});
	}

	@DisplayName("성공: 클러스터 캐시 HIT")
	@Test
	void getDiaryClusters_cacheHit() {
		// given
		given(clusterCacheDao.load(GEOHASH_L3, LEVEL_SIDO)).willReturn(clusters);

		// when
		GetDiaryClustersResponse result = mapService.getClustersByRedisCache(GEOHASH_L3, LEVEL_SIDO);

		// then
		assertThat(result).isEqualTo(GetDiaryClustersResponse.of(clusters));
		verify(clusterCacheDao).load(GEOHASH_L3, LEVEL_SIDO);
		verify(clusterCacheDao, never()).loadAndCache(anyString(), anyInt());
	}


	@DisplayName("성공: 캐시 MISS → DAO.loadAndCache 호출")
	@Test
	void getDiaryClusters_cacheMiss_thenLoadAndCache() {
		// given
		given(clusterCacheDao.load(GEOHASH_L3, LEVEL_SIDO)).willReturn(null);
		given(clusterCacheDao.loadAndCache(GEOHASH_L3, LEVEL_SIDO)).willReturn(clusters);

		// when
		GetDiaryClustersResponse result = mapService.getClustersByRedisCache(GEOHASH_L3, LEVEL_SIDO);

		// then
		assertThat(result).isEqualTo(GetDiaryClustersResponse.of(clusters));
		verify(clusterCacheDao).load(GEOHASH_L3, LEVEL_SIDO);
		verify(clusterCacheDao).loadAndCache(GEOHASH_L3, LEVEL_SIDO);
	}

	@DisplayName("실패: geohash 길이 불일치(클러스터)")
	@Test
	void getDiaryClusters_invalidGeohashLength() {
		// given
		String invalid = "abcd"; // 길이 4 → level(1/2) 기대 길이 3과 불일치

		// expect
		assertThatThrownBy(() -> mapService.getClustersByRedisCache(invalid, LEVEL_SIDO))
			.isInstanceOf(InvalidGeohashException.class);
		verifyNoInteractions(clusterCacheDao);
	}

	@DisplayName("성공: 마커 캐시 HIT")
	@Test
	void getDiaryMarkers_cacheHit() {
		// given
		given(markerCacheDao.load(GEOHASH_L5)).willReturn(markers);

		// when
		GetDiaryMarkersResponse result = mapService.getMarkersByRedisCache(GEOHASH_L5);

		// then
		assertThat(result).isEqualTo(GetDiaryMarkersResponse.ofMarkers(markers));
		verify(markerCacheDao).load(GEOHASH_L5);
		verify(markerCacheDao, never()).loadAndCache(anyString());
	}

	@DisplayName("성공: 마커 캐시 MISS → DAO.loadAndCache 호출")
	@Test
	void getDiaryMarkers_cacheMiss_thenLoadAndCache() {
		// given
		given(markerCacheDao.load(GEOHASH_L5)).willReturn(null);
		given(markerCacheDao.loadAndCache(GEOHASH_L5)).willReturn(markers);

		// when
		GetDiaryMarkersResponse result = mapService.getMarkersByRedisCache(GEOHASH_L5);

		// then
		assertThat(result).isEqualTo(GetDiaryMarkersResponse.ofMarkers(markers));
		verify(markerCacheDao).load(GEOHASH_L5);
		verify(markerCacheDao).loadAndCache(GEOHASH_L5);
	}

	@DisplayName("실패: geohash 길이 불일치(마커)")
	@Test
	void getDiaryMarkers_invalidGeohashLength() {
		// given
		String invalid = "abcd";

		// expect
		assertThatThrownBy(() -> mapService.getMarkersByRedisCache(invalid))
			.isInstanceOf(InvalidGeohashException.class);
		verifyNoInteractions(markerCacheDao);
	}
}
