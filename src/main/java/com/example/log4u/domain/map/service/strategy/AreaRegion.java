package com.example.log4u.domain.map.service.strategy;

import java.util.List;
import java.util.Optional;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.entity.Areas;

public interface AreaRegion<T extends Areas> {
	List<T> findRegionsInBounds(double south, double north, double west, double east);

	Optional<T> findRegionByLatLon(double lat, double lon);

	default String extractAreaName(T area) {
		return area.getName();
	}

	default DiaryClusterResponseDto toDto(T area, Long count) {
		return DiaryClusterResponseDto.of(area, count);
	}
}
