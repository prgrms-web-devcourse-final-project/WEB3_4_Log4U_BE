package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.entity.SidoAreasDiaryCount;
import com.example.log4u.domain.map.entity.SiggAreasDiaryCount;
import com.example.log4u.domain.map.repository.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.SiggAreasRepository;
import com.example.log4u.domain.map.repository.SidoAreasRepository;
import com.example.log4u.domain.map.repository.SiggAreasDiaryCountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

	private final SidoAreasRepository sidoAreasRepository;
	private final SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;
	private final SiggAreasRepository siggAreasRepository;
	private final SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;

	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getDiaryClusters(double south, double north, double west, double east, int zoom) {
		if (zoom <= 10) {
			return getSidoAreasClusters(south, north, west, east);
		} else {
			return getSiggAreasClusters(south, north, west, east);
		}
	}

	private List<DiaryClusterResponseDto> getSidoAreasClusters(double south, double north, double west, double east) {
		return sidoAreasRepository.findSidoAreaClusters(south, north, west, east).stream()
			.map(DiaryClusterResponseDto::of)
			.toList();
	}

	private List<DiaryClusterResponseDto> getSiggAreasClusters(double south, double north, double west, double east) {
		return siggAreasRepository.findSiggAreaClusters(south, north, west, east).stream()
			.map(DiaryClusterResponseDto::of)
			.toList();
	}

	public void increaseRegionDiaryCount(Double lat, Double lon) {
		sidoAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sido -> sidoAreasDiaryCountRepository.findById(sido.getId()))
			.ifPresent(SidoAreasDiaryCount::incrementCount);

		siggAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sigg -> siggAreasDiaryCountRepository.findById(sigg.getGid()))
			.ifPresent(SiggAreasDiaryCount::incrementCount);
	}
}
