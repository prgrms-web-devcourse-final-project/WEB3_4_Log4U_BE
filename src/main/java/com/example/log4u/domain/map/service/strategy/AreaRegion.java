package com.example.log4u.domain.map.service.strategy;

import java.util.List;
import java.util.Optional;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

public interface AreaRegion<T> {
	List<T> findRegionsInBounds(double south, double north, double west, double east);

	Optional<T> findRegionByLatLon(double lat, double lon);

	String extractAreaName(T area);

	DiaryClusterResponseDto toDto(T area, Long count);
}
