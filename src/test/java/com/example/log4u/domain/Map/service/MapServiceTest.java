package com.example.log4u.domain.Map.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.DiaryCacheDao;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.exception.InvalidGeohashException;
import com.example.log4u.domain.map.exception.InvalidMapLevelException;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.example.log4u.domain.map.service.MapService;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.DiaryMarkerFixture;

@DisplayName("지도 API 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MapServiceTest {

	@InjectMocks
	private MapService mapService;

	@Mock
	private SidoAreasRepository sidoAreasRepository;

	@Mock
	private SiggAreasRepository siggAreasRepository;

	@Mock
	private DiaryRepository diaryRepository;

	@Mock
	private DiaryCacheDao diaryCacheDao;

	@Mock
	private DiaryService diaryService;

	@Mock
	private DiaryGeohashService diaryGeohashService;

	@Mock
	private ClusterCacheDao clusterCacheDao;

	private static final String GEOHASH = "abc";
	private static final int VALID_LEVEL_1 = 1;
	private static final int VALID_LEVEL_2 = 2;

	private final List<DiaryClusterResponseDto> mockResult = List.of(
		new DiaryClusterResponseDto("서울", 1L, 37.5665, 126.9780, 10L)
	);

	@DisplayName("성공: 클러스터 캐시 HIT")
	@Test
	void getDiaryClusters_success_cacheHit() {
		// given
		given(clusterCacheDao.getDiaryCluster(GEOHASH, VALID_LEVEL_1)).willReturn(Optional.of(mockResult));

		// when
		List<DiaryClusterResponseDto> result = mapService.getDiaryClusters(GEOHASH, VALID_LEVEL_1);

		// then
		assertThat(result).isEqualTo(mockResult);
		verify(sidoAreasRepository, never()).findByGeohashPrefix(any());
		verify(clusterCacheDao, never()).setDiaryCluster(any(), anyInt(), any(), any());
	}

	@DisplayName("성공: 캐시 MISS → DB 조회 → 캐시 저장")
	@Test
	void getDiaryClusters_success_cacheMiss_then_dbHit_and_cacheStore() {
		// given
		given(clusterCacheDao.getDiaryCluster(GEOHASH, VALID_LEVEL_1)).willReturn(Optional.empty());
		given(sidoAreasRepository.findByGeohashPrefix(GEOHASH)).willReturn(mockResult);

		// when
		List<DiaryClusterResponseDto> result = mapService.getDiaryClusters(GEOHASH, VALID_LEVEL_1);

		// then
		assertThat(result).isEqualTo(mockResult);
		verify(clusterCacheDao).setDiaryCluster(eq(GEOHASH), eq(VALID_LEVEL_1), eq(mockResult), any());
	}

	@DisplayName("실패: 유효하지 않은 level")
	@Test
	void getDiaryClusters_invalidLevel() {
		// given
		int invalidLevel = 99;

		// expect
		assertThatThrownBy(() -> mapService.getDiaryClusters(GEOHASH, invalidLevel))
			.isInstanceOf(InvalidMapLevelException.class);
	}

	@DisplayName("실패: geohash 길이 불일치")
	@Test
	void getDiaryClusters_invalidGeohashLength() {
		// given
		String invalidGeohash = "abcd";

		// expect
		assertThatThrownBy(() -> mapService.getDiaryClusters(invalidGeohash, VALID_LEVEL_1))
			.isInstanceOf(InvalidGeohashException.class)
			.hasMessageContaining("geohash 길이가 유효하지 않습니다");
	}

	@DisplayName("성공 : geohash 캐시 HIT + 모든 diary 캐시 HIT")
	@Test
	void getDiariesByGeohash_success_allCacheHit() {
		// given
		String geohash = "wydmt";
		Set<Long> cachedIds = Set.of(1L, 2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerFixture.createDiaryMarker(1L);
		DiaryMarkerResponseDto dto2 = DiaryMarkerFixture.createDiaryMarker(2L);

		given(diaryCacheDao.getDiaryIdSetFromCache("wydmt")).willReturn(cachedIds);
		given(diaryCacheDao.getDiariesFromCacheBulk(List.of(1L, 2L))).willReturn(List.of(dto1, dto2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash("wydmt");

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryRepository, never()).findAllById(any());
		verify(diaryGeohashService, never()).getDiaryIdsByGeohash(any());
	}


	@DisplayName("성공 : geohash 캐시 HIT + 모든 diary 캐시 MISS")
	@Test
	void getDiariesByGeohash_success_allDiaryCacheMiss() {
		// given
		String geohash = "wydmt";
		Set<Long> cachedIds = Set.of(1L, 2L);
		Diary diary1 = DiaryFixture.createDiaryFixture(1L);
		Diary diary2 = DiaryFixture.createDiaryFixture(2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerResponseDto.of(diary1);
		DiaryMarkerResponseDto dto2 = DiaryMarkerResponseDto.of(diary2);

		given(diaryCacheDao.getDiaryIdSetFromCache("wydmt")).willReturn(cachedIds);
		given(diaryCacheDao.getDiariesFromCacheBulk(List.of(1L, 2L))).willReturn(List.of());
		given(diaryService.getDiaries(List.of(1L, 2L))).willReturn(List.of(diary1, diary2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash("wydmt");

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryCacheDao).cacheAllDiaries(List.of(dto1, dto2));
	}

	@DisplayName("성공 : geohash 캐시 HIT + 일부 diary 캐시 MISS")
	@Test
	void getDiariesByGeohash_success_partialDiaryCacheMiss() {
		// given
		String geohash = "wydmt";
		Set<Long> cachedIds = Set.of(1L, 2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerFixture.createDiaryMarker(1L);
		Diary diary2 = DiaryFixture.createDiaryFixture(2L);
		DiaryMarkerResponseDto dto2 = DiaryMarkerResponseDto.of(diary2);

		given(diaryCacheDao.getDiaryIdSetFromCache("wydmt")).willReturn(cachedIds);
		given(diaryCacheDao.getDiariesFromCacheBulk(List.of(1L, 2L))).willReturn(List.of(dto1));
		given(diaryService.getDiaries(List.of(2L))).willReturn(List.of(diary2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash("wydmt");

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryCacheDao).cacheAllDiaries(List.of(dto2));
	}

	@DisplayName("성공 : geohash 캐시 MISS → DB 조회 → 모든 diary 캐시 HIT")
	@Test
	void getDiariesByGeohash_success_geohashMiss_allDiaryCacheHit() {
		// given
		String geohash = "wydmt";
		List<Long> diaryIdsFromDb = List.of(1L, 2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerFixture.createDiaryMarker(1L);
		DiaryMarkerResponseDto dto2 = DiaryMarkerFixture.createDiaryMarker(2L);

		given(diaryCacheDao.getDiaryIdSetFromCache(geohash)).willReturn(Collections.emptySet());
		given(diaryGeohashService.getDiaryIdsByGeohash(geohash)).willReturn(diaryIdsFromDb);
		given(diaryCacheDao.getDiariesFromCacheBulk(diaryIdsFromDb)).willReturn(List.of(dto1, dto2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash(geohash);

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryCacheDao).cacheDiaryIdSetByGeohash(geohash, diaryIdsFromDb);
		verify(diaryService, never()).getDiaries(any());
	}

	@DisplayName("성공 : geohash 캐시 MISS → DB 조회 → 모든 diary 캐시 MISS")
	@Test
	void getDiariesByGeohash_success_geohashMiss_allDiaryCacheMiss() {
		// given
		String geohash = "wydmt";
		List<Long> diaryIdsFromDb = List.of(1L, 2L);
		Diary diary1 = DiaryFixture.createDiaryFixture(1L);
		Diary diary2 = DiaryFixture.createDiaryFixture(2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerResponseDto.of(diary1);
		DiaryMarkerResponseDto dto2 = DiaryMarkerResponseDto.of(diary2);

		given(diaryCacheDao.getDiaryIdSetFromCache(geohash)).willReturn(Collections.emptySet());
		given(diaryGeohashService.getDiaryIdsByGeohash(geohash)).willReturn(diaryIdsFromDb);
		given(diaryCacheDao.getDiariesFromCacheBulk(diaryIdsFromDb)).willReturn(List.of());
		given(diaryService.getDiaries(diaryIdsFromDb)).willReturn(List.of(diary1, diary2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash(geohash);

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryCacheDao).cacheDiaryIdSetByGeohash(geohash, diaryIdsFromDb);
		verify(diaryCacheDao).cacheAllDiaries(List.of(dto1, dto2));
	}

	@DisplayName("성공 : geohash 캐시 MISS → DB 조회 → diary 일부 캐시 MISS")
	@Test
	void getDiariesByGeohash_success_geohashCacheMissAndDiaryCacheMiss() {
		// given
		String geohash = "abcde";
		List<Long> dbIds = List.of(1L, 2L);
		DiaryMarkerResponseDto dto1 = DiaryMarkerFixture.createDiaryMarker(1L);
		Diary diary2 = DiaryFixture.createDiaryFixture(2L);
		DiaryMarkerResponseDto dto2 = DiaryMarkerResponseDto.of(diary2);

		given(diaryCacheDao.getDiaryIdSetFromCache(geohash)).willReturn(Collections.emptySet());
		given(diaryGeohashService.getDiaryIdsByGeohash(geohash)).willReturn(dbIds);
		given(diaryCacheDao.getDiariesFromCacheBulk(dbIds)).willReturn(List.of(dto1));
		given(diaryService.getDiaries(List.of(2L))).willReturn(List.of(diary2));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash(geohash);

		// then
		assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
		verify(diaryCacheDao).cacheDiaryIdSetByGeohash(geohash, dbIds);
		verify(diaryCacheDao).cacheAllDiaries(List.of(dto2));
	}

	@DisplayName("성공 : Redis 예외 발생 시 fallback 작동: DB에서 조회됨")
	@Test
	void getDiariesByGeohash_redisFailureHandledInternally() {
		// given
		String geohash = "abcde";
		given(diaryCacheDao.getDiaryIdSetFromCache(geohash)).willReturn(Collections.emptySet());

		List<Long> diaryIds = List.of(1L);
		Diary diary = DiaryFixture.createDiaryFixture(1L);
		given(diaryGeohashService.getDiaryIdsByGeohash(geohash)).willReturn(diaryIds);
		given(diaryService.getDiaries(diaryIds)).willReturn(List.of(diary));

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash(geohash);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().diaryId()).isEqualTo(1L);
		verify(diaryService).getDiaries(diaryIds);
	}

	@DisplayName("예외 : diaryId 존재하나 DB에 diary 없음")
	@Test
	void diaryIdExistsButDiaryMissingInDb() {
		// given
		String geohash = "wydmt";
		Set<Long> cachedIds = Set.of(100L);
		given(diaryCacheDao.getDiaryIdSetFromCache(geohash)).willReturn(cachedIds);
		given(diaryCacheDao.getDiariesFromCacheBulk(List.of(100L))).willReturn(List.of());
		given(diaryService.getDiaries(List.of(100L))).willReturn(Collections.emptyList());

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesByGeohash(geohash);

		// then
		assertThat(result).isEmpty();
	}

}
