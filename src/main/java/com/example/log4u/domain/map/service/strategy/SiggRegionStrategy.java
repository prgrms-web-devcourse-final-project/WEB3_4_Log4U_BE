package com.example.log4u.domain.map.service.strategy;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.entity.SiggAreas;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SiggRegionStrategy implements AreaRegion<SiggAreas> {

	private final SiggAreasRepository siggAreasRepository;

	@Override
	public List<SiggAreas> findRegionsInBounds(double west, double south, double east, double north) {
		return siggAreasRepository.findWithinBoundingBox(west, south, east, north);
	}

	@Override
	public Optional<SiggAreas> findRegionByLatLon(double lat, double lon) {
		return siggAreasRepository.findRegionByLatLon(lat, lon);
	}

	@Override
	public String extractAreaName(SiggAreas area) {
		return area.getSggName();
	}

	@Override
	public DiaryClusterResponseDto toDto(SiggAreas area, Long count) {
		return DiaryClusterResponseDto.of(area, count);
	}
}
