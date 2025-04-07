package com.example.log4u.domain.Map.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.entity.SiggAreas;
import com.example.log4u.domain.map.service.MyMapService;
import com.example.log4u.domain.map.service.strategy.SidoRegionStrategy;
import com.example.log4u.domain.map.service.strategy.SiggRegionStrategy;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.SidoAreasFixture;
import com.example.log4u.fixture.SiggAreasFixture;

@DisplayName("나의 다이어리 클러스터 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MyMapServiceTest {

	@InjectMocks
	private MyMapService myMapService;

	@Mock
	private DiaryRepository diaryRepository;

	@Mock
	private SidoRegionStrategy sidoRegionStrategy;

	@Mock
	private SiggRegionStrategy siggRegionStrategy;

	@DisplayName("성공 테스트 : 줌레벨 9일 경우 시도 기준 나의 다이어리 클러스터 조회")
	@Test
	void getMyDiaryClusters_sido_success() {
		// given
		Long userId = 1L;
		double south = 33.0, north = 39.0, west = 125.0, east = 130.0;
		int zoom = 9;

		SidoAreas seoul = SidoAreasFixture.createSidoAreaFixture(1L, "서울특별시", 37.5665, 126.9780);
		SidoAreas gyeonggi = SidoAreasFixture.createSidoAreaFixture(2L, "경기도", 37.4138, 127.5183);
		List<SidoAreas> sidoList = List.of(seoul, gyeonggi);

		List<Diary> diaries = List.of(
			DiaryFixture.createCustomDiaryFixture(1L, userId, "title", "content", "thumb1.jpg", null, 37.5665, 126.9780,
				null, 0L),
			DiaryFixture.createCustomDiaryFixture(2L, userId, "title", "content", "thumb2.jpg", null, 37.5666, 126.9781,
				null, 0L),
			DiaryFixture.createCustomDiaryFixture(3L, userId, "title", "content", "thumb3.jpg", null, 37.4138, 127.5183,
				null, 0L)
		);

		given(sidoRegionStrategy.findRegionsInBounds(west, south, east, north)).willReturn(sidoList);
		given(diaryRepository.findInBoundsByUserId(userId, south, north, west, east)).willReturn(diaries);
		given(sidoRegionStrategy.findRegionByLatLon(anyDouble(), anyDouble()))
			.willAnswer(invocation -> {
				double lat = invocation.getArgument(0);
				if (lat == 37.5665 || lat == 37.5666)
					return Optional.of(seoul);
				return Optional.of(gyeonggi);
			});
		given(sidoRegionStrategy.extractAreaName(any())).willAnswer(
			invocation -> ((SidoAreas)invocation.getArgument(0)).getName());
		given(sidoRegionStrategy.toDto(any(), anyLong())).willAnswer(invocation -> {
			SidoAreas s = invocation.getArgument(0);
			Long count = invocation.getArgument(1);
			return new DiaryClusterResponseDto(s.getName(), s.getId(), s.getLat(), s.getLon(), count);
		});

		// when
		List<DiaryClusterResponseDto> result = myMapService.getMyDiaryClusters(south, north, west, east, zoom, userId);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).diaryCount() + result.get(1).diaryCount()).isEqualTo(3);
	}

	@DisplayName("성공 테스트 : 줌레벨 10 이상일 경우 시군구 기준 나의 다이어리 클러스터 조회")
	@Test
	void getMyDiaryClusters_sigg_success() {
		// given
		Long userId = 1L;
		double south = 33.0, north = 39.0, west = 125.0, east = 130.0;
		int zoom = 10;

		SiggAreas anyArea = SiggAreasFixture.createSiggAreaFixture(1L, "강남구", 37.4979, 127.0276);
		List<SiggAreas> siggList = List.of(anyArea);

		Diary diary = DiaryFixture.createCustomDiaryFixture(1L, userId, "title", "content", "thumb.jpg", null, 37.4979,
			127.0276, null, 0L);

		given(siggRegionStrategy.findRegionsInBounds(west, south, east, north)).willReturn(siggList);
		given(diaryRepository.findInBoundsByUserId(userId, south, north, west, east)).willReturn(List.of(diary));
		given(siggRegionStrategy.findRegionByLatLon(anyDouble(), anyDouble())).willReturn(Optional.of(anyArea));
		given(siggRegionStrategy.extractAreaName(any())).willReturn(anyArea.getSggName());
		given(siggRegionStrategy.toDto(any(), anyLong())).willReturn(
			new DiaryClusterResponseDto(anyArea.getSggName(), anyArea.getGid(), anyArea.getLat(), anyArea.getLon(), 1L)
		);

		// when
		List<DiaryClusterResponseDto> result = myMapService.getMyDiaryClusters(south, north, west, east, zoom, userId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).diaryCount()).isEqualTo(1);
	}

	@DisplayName("성공 테스트 : 다이어리가 없을 때 지역별 클러스터 count = 0")
	@Test
	void getMyDiaryClusters_noDiaries_success() {
		// given
		Long userId = 1L;
		double south = 33.0, north = 39.0, west = 125.0, east = 130.0;
		int zoom = 9;

		SidoAreas seoul = SidoAreasFixture.createSidoAreaFixture(1L, "서울특별시", 37.5665, 126.9780);
		List<SidoAreas> sidoList = List.of(seoul);

		given(sidoRegionStrategy.findRegionsInBounds(west, south, east, north)).willReturn(sidoList);
		given(diaryRepository.findInBoundsByUserId(userId, south, north, west, east)).willReturn(List.of());
		given(sidoRegionStrategy.extractAreaName(any())).willReturn("서울특별시");
		given(sidoRegionStrategy.toDto(any(), eq(0L))).willReturn(
			new DiaryClusterResponseDto(seoul.getName(), seoul.getId(), seoul.getLat(), seoul.getLon(), 0L)
		);

		// when
		List<DiaryClusterResponseDto> result = myMapService.getMyDiaryClusters(south, north, west, east, zoom, userId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().diaryCount()).isEqualTo(0);
	}
}
