package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

	private final SidoAreasRepository sidoAreasRepository;
	private final SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;
	private final SiggAreasRepository siggAreasRepository;
	private final SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;
	private final DiaryRepository diaryRepository;

	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getDiaryClusters(double south, double north, double west, double east, int zoom) {
		if (zoom <= 10) {
			return getSidoAreasClusters(south, north, west, east);
		} else {
			return getSiggAreasClusters(south, north, west, east);
		}
	}

	private List<DiaryClusterResponseDto> getSidoAreasClusters(double south, double north, double west, double east) {
		return sidoAreasRepository.findSidoAreaClusters(south, north, west, east);
	}

	private List<DiaryClusterResponseDto> getSiggAreasClusters(double south, double north, double west, double east) {
		return siggAreasRepository.findSiggAreaClusters(south, north, west, east);
	}

	public void increaseRegionDiaryCount(Double lat, Double lon) {
		sidoAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sido -> sidoAreasDiaryCountRepository.findById(sido.getId()))
			.ifPresent(count -> {
				count.incrementCount();
				sidoAreasDiaryCountRepository.save(count);
			});

		siggAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sigg -> siggAreasDiaryCountRepository.findById(sigg.getGid()))
			.ifPresent(count -> {
				count.incrementCount();
				siggAreasDiaryCountRepository.save(count);
			});
	}

	@Transactional(readOnly = true)
	public List<DiaryMarkerResponseDto> getDiariesInBounds(double south, double north, double west, double east) {
		return diaryRepository.findDiariesInBounds(south, north, west, east);
	}

}
