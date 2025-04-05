package com.example.log4u.fixture;


import java.util.List;

import com.example.log4u.domain.map.dto.response.AreaClusterProjection;

public class AreaClusterFixture {

	public static AreaClusterProjection createSidoArea(Long id, String name, double lat, double lon, long diaryCount) {
		return new AreaClusterProjectionStub(id, name, lat, lon, diaryCount);
	}

	public static AreaClusterProjection createSiggArea(Long id, String name, double lat, double lon, long diaryCount) {
		return new AreaClusterProjectionStub(id, name, lat, lon, diaryCount);
	}

	public static List<AreaClusterProjection> sidoAreaList() {
		return List.of(
			createSidoArea(1L, "서울특별시", 37.5, 126.9, 100L),
			createSidoArea(2L, "경기도", 37.6, 127.0, 80L)
		);
	}

	public static List<AreaClusterProjection> siggAreaList() {
		return List.of(
			createSiggArea(11L, "강남구", 37.4, 127.0, 42L),
			createSiggArea(12L, "송파구", 37.5, 127.1, 30L)
		);
	}
}
