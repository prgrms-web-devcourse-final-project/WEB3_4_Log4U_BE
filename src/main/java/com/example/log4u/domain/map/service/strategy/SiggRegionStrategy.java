package com.example.log4u.domain.map.service.strategy;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.entity.SiggAreas;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SiggRegionStrategy implements AreaRegion<SiggAreas> {

	private final SiggAreasRepository siggAreasRepository;

	@Override
	public List<SiggAreas> findRegionsInBounds(double south, double north, double west, double east) {
		return siggAreasRepository.findWithinBoundingBox(south, north, west, east);
	}

	@Override
	public Optional<SiggAreas> findRegionByLatLon(double lat, double lon) {
		return siggAreasRepository.findRegionByLatLon(lat, lon);
	}
}
