package com.example.log4u.fixture;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

import java.util.ArrayList;
import java.util.List;

public class AreaClusterFixture {

	public static DiaryClusterResponseDto createSidoAreaFixture(Long id, String name, double lat, double lon, long diaryCount) {
		return new DiaryClusterResponseDto(
			name,
			id,
			lat,
			lon,
			diaryCount
		);
	}

	public static DiaryClusterResponseDto createSiggAreaFixture(Long id, String name, double lat, double lon, long diaryCount) {
		return new DiaryClusterResponseDto(
			name,
			id,
			lat,
			lon,
			diaryCount
		);
	}

	public static List<DiaryClusterResponseDto> sidoAreaList() {
		List<DiaryClusterResponseDto> list = new ArrayList<>();
		list.add(createSidoAreaFixture(1L, "서울특별시", 37.5665, 126.9780, 100L));
		list.add(createSidoAreaFixture(2L, "경기도", 37.4138, 127.5183, 80L));
		return list;
	}

	public static List<DiaryClusterResponseDto> siggAreaList() {
		List<DiaryClusterResponseDto> list = new ArrayList<>();
		list.add(createSiggAreaFixture(101L, "강남구", 37.4979, 127.0276, 42L));
		list.add(createSiggAreaFixture(102L, "송파구", 37.5145, 127.1050, 30L));
		return list;
	}
}
