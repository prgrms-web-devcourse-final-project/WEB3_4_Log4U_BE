package com.example.log4u.domain.Map.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.fixture.AreaClusterFixture;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.example.log4u.domain.map.service.MapService;
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

	// @DisplayName("성공 테스트: 줌레벨이 10 이하이면 시/도 클러스터 조회")
	// @Test
	// void getDiaryClusters_sidoAreas_success() {
	// 	// given
	// 	double south = 37.0, north = 38.0, west = 126.0, east = 127.0;
	// 	int zoom = 9;
	//
	// 	given(sidoAreasRepository.findSidoAreaClusters(south, north, west, east))
	// 		.willReturn(AreaClusterFixture.sidoAreaList());
	//
	// 	// when
	// 	List<DiaryClusterResponseDto> result = mapService.getDiaryClusters(south, north, west, east, zoom);
	//
	// 	// then
	// 	assertThat(result).hasSize(2);
	// 	assertThat(result.getFirst().areaName()).isEqualTo("서울특별시");
	// 	assertThat(result.getFirst().diaryCount()).isEqualTo(100L);
	// 	verify(sidoAreasRepository).findSidoAreaClusters(south, north, west, east);
	// }
	//
	// @DisplayName("성공 테스트: 줌레벨이 11 이상이면 시군구 클러스터 조회")
	// @Test
	// void getDiaryClusters_siggAreas_success() {
	// 	// given
	// 	double south = 37.0, north = 38.0, west = 126.0, east = 127.0;
	// 	int zoom = 12;
	//
	// 	given(siggAreasRepository.findSiggAreaClusters(south, north, west, east))
	// 		.willReturn(AreaClusterFixture.siggAreaList());
	//
	// 	// when
	// 	List<DiaryClusterResponseDto> result = mapService.getDiaryClusters(south, north, west, east, zoom);
	//
	// 	// then
	// 	assertThat(result).hasSize(2);
	// 	assertThat(result.get(1).areaName()).isEqualTo("송파구");
	// 	assertThat(result.get(1).diaryCount()).isEqualTo(30L);
	// 	verify(siggAreasRepository).findSiggAreaClusters(south, north, west, east);
	// }

	@DisplayName("성공 테스트: 마커 조회 (줌 14 이상)")
	@Test
	void getDiariesInBounds_success() {
		// given
		double south = 37.0, north = 38.0, west = 126.0, east = 127.0;
		List<DiaryMarkerResponseDto> markers = DiaryMarkerFixture.createDiaryMarkers();

		given(diaryRepository.findDiariesInBounds(south, north, west, east))
			.willReturn(markers);

		// when
		List<DiaryMarkerResponseDto> result = mapService.getDiariesInBounds(south, north, west, east);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).title()).isEqualTo("첫번째 다이어리");
		assertThat(result.get(1).likeCount()).isEqualTo(7L);
		verify(diaryRepository).findDiariesInBounds(south, north, west, east);
	}

}
