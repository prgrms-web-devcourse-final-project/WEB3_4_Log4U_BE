package com.example.log4u.domain.map.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.service.strategy.AreaRegion;
import com.example.log4u.domain.map.service.strategy.SidoRegionStrategy;
import com.example.log4u.domain.map.service.strategy.SiggRegionStrategy;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMapService {

	private final DiaryRepository diaryRepository;
	private final SidoRegionStrategy sidoRegionStrategy;
	private final SiggRegionStrategy siggRegionStrategy;

	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getMyDiaryClusters(double south, double north, double west, double east,
		int zoom, Long userId) {

		if (zoom < 10) {
			return calculateClusters(sidoRegionStrategy, userId, south, north, west, east);
		} else {
			return calculateClusters(siggRegionStrategy, userId, south, north, west, east);
		}
	}

	private <T> List<DiaryClusterResponseDto> calculateClusters(AreaRegion<T> regionStrategy, Long userId,
		double south, double north, double west, double east) {

		List<T> regions = regionStrategy.findRegionsInBounds(south, north, west, east);
		List<Diary> diaries = diaryRepository.findInBoundsByUserId(userId, south, north, west, east);

		Map<String, Long> diaryCountMap = diaries.stream()
			.map(diary -> {
				return regionStrategy.findRegionByLatLon(diary.getLatitude(), diary.getLongitude())
					.map(regionStrategy::extractAreaName)
					.orElseGet(() -> {
						log.warn("지역 매핑 실패 - diaryId: {}, lat: {}, lon: {}", diary.getDiaryId(), diary.getLatitude(), diary.getLongitude());
						return "UNKNOWN";
					});
			})
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		return regions.stream()
			.map(region -> {
				String name = regionStrategy.extractAreaName(region);
				Long count = diaryCountMap.getOrDefault(name, 0L);
				return regionStrategy.toDto(region, count);
			})
			.toList();
	}
}
