package com.example.log4u.domain.map.service.strategy;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SidoRegionStrategy implements AreaRegion<SidoAreas> {

	private final SidoAreasRepository sidoAreasRepository;

	@Override
	public List<SidoAreas> findRegionsInBounds(double south, double north, double west, double east) {
		return sidoAreasRepository.findWithinBoundingBox(south, north, west, east));
	}

	@Override
	public Optional<SidoAreas> findRegionByLatLon(double lat, double lon) {
		return sidoAreasRepository.findRegionByLatLon(lat, lon);
	}

	@Override
	public String extractAreaName(SidoAreas area) {
		return area.getName();
	}

	@Override
	public DiaryClusterResponseDto toDto(SidoAreas area, Long count) {
		return DiaryClusterResponseDto.of(area, count);
	}
}
