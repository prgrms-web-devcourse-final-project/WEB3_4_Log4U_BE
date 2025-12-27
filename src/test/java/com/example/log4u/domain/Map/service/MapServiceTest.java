package com.example.log4u.domain.Map.service;

import static com.example.log4u.domain.map.exception.MapErrorCode.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.log4u.common.ServiceTest;
import com.example.log4u.domain.map.exception.InvalidGeohashException;
import com.example.log4u.domain.map.service.MapService;

class MapServiceTest extends ServiceTest {

	@Autowired
	private MapService mapService;

	@DisplayName("지역 클러스터 목록을 불러오는 경우, Geohash 문자열 길이가 3이 아니면 예외가 발생한다.")
	@Test
	void getClustersWithInvalidGeohashLength() {
		// given
		String geohash = "wydm6";
		int level = 1;

		// when, then
		assertThatThrownBy(() -> mapService.getClustersByRedisCache(geohash, level))
			.isInstanceOf(InvalidGeohashException.class)
			.hasMessage(INVALID_GEOHASH.getErrorMessage());
	}

	@DisplayName("마커 목록을 불러오는 경우, Geohash 문자열 길이가 5가 아니면 예외가 발생한다.")
	@Test
	void getMarkersWithInvalidGeohashLength() {
		// given
		String geohash = "wydm6ef2";

		// when, then
		assertThatThrownBy(() -> mapService.getMarkersByRedisCache(geohash))
			.isInstanceOf(InvalidGeohashException.class)
			.hasMessage(INVALID_GEOHASH.getErrorMessage());
	}
}
