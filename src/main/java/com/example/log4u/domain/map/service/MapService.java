package com.example.log4u.domain.map.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.DiaryCacheDao;
import com.example.log4u.domain.map.cache.RedisTTLPolicy;
import com.example.log4u.domain.map.exception.InvalidGeohashException;
import com.example.log4u.domain.map.exception.InvalidMapLevelException;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

	private final SidoAreasRepository sidoAreasRepository;
	private final SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;
	private final SiggAreasRepository siggAreasRepository;
	private final SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;
	private final DiaryCacheDao diaryCacheDao;
	private final ClusterCacheDao clusterCacheDao;
	private final DiaryService diaryService;
	private final DiaryGeohashService diaryGeohashService;

	/**
	 * 캐싱 전략: Look-Aside + Write-Around
	 * 	- Redis에서 클러스터 데이터를 먼저 조회 (캐시 HIT 시 바로 응답)
	 * 	- 캐시 MISS 시 DB에서 조회 후 Redis에 저장하고 응답
	 *
	 * level 기준:
	 *   - level 1: 시/도 단위 클러스터 (sido)
	 *   - level 2: 시/군/구 단위 클러스터 (sigg)
	 */
	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getDiaryClusters(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		return clusterCacheDao.getDiaryCluster(geohash, level)
			.orElseGet(() -> {
				List<DiaryClusterResponseDto> dbResult = loadClustersFromDb(geohash, level);
				clusterCacheDao.setDiaryCluster(geohash, level, dbResult, RedisTTLPolicy.CLUSTER_TTL);
				return dbResult;
			});
	}

	private List<DiaryClusterResponseDto> loadClustersFromDb(String geohash, int level) {
		return switch (level) {
			case 1 -> sidoAreasRepository.findByGeohashPrefix(geohash);
			case 2 -> siggAreasRepository.findByGeohashPrefix(geohash);
			default -> throw new InvalidMapLevelException();
		};
	}

	/**
	 * 캐싱 전략: Look-Aside + Write-Around
	 *
	 * [1단계] geohash → diaryIds 조회
	 *   - Redis(Set)에서 diaryIds 조회 (캐시 HIT 시 바로 사용)
	 *   - 캐시 MISS 시 DB 조회 후 Redis에 저장 (Write-Around)
	 *
	 * [2단계] diaryId → DiaryMarkerDto 조회
	 *   - Redis(String)에서 Diary DTO 조회 (캐시 HIT 시 바로 사용)
	 *   - 캐시 MISS 시 DB에서 조회 후 Redis에 저장 (Write-Around)
	 */
	@Transactional(readOnly = true)
	public List<DiaryMarkerResponseDto> getDiariesByGeohash(String geohash) {
		validateGeohashLength(geohash, 5);
		List<Long> diaryIds = loadDiaryIdsFromGeoCache(geohash);
		return loadDiaryDtosFromCache(diaryIds);
	}

	private List<Long> loadDiaryIdsFromGeoCache(String geohash) {
		Set<Long> cachedIds = diaryCacheDao.getDiaryIdSetFromCache(geohash);
		if (!cachedIds.isEmpty())
			return new ArrayList<>(cachedIds);

		List<Long> diaryIds = diaryGeohashService.getDiaryIdsByGeohash(geohash);
		diaryCacheDao.cacheDiaryIdSetByGeohash(geohash, diaryIds);
		return diaryIds;
	}


	private List<DiaryMarkerResponseDto> loadDiaryDtosFromCache(List<Long> diaryIds) {
		List<DiaryMarkerResponseDto> cachedDtos = diaryCacheDao.getDiariesFromCacheBulk(diaryIds);
		List<Long> missedIds = getCacheMissedIds(diaryIds, cachedDtos);
		if (missedIds.isEmpty())
			return cachedDtos;

		List<DiaryMarkerResponseDto> loaded = loadAndCacheMissedDiaries(missedIds);
		return Stream.concat(cachedDtos.stream(), loaded.stream()).toList();
	}

	private List<Long> getCacheMissedIds(List<Long> allIds, List<DiaryMarkerResponseDto> cachedDtos) {
		Set<Long> cachedIds = cachedDtos.stream()
			.map(DiaryMarkerResponseDto::diaryId)
			.collect(Collectors.toSet());

		return allIds.stream()
			.filter(id -> !cachedIds.contains(id))
			.collect(Collectors.toList());
	}

	private List<DiaryMarkerResponseDto> loadAndCacheMissedDiaries(List<Long> missedIds) {
		List<Diary> diaries = diaryService.getDiaries(missedIds);
		List<DiaryMarkerResponseDto> result = diaries.stream()
			.map(DiaryMarkerResponseDto::of)
			.toList();

		diaryCacheDao.cacheAllDiaries(result);
		return result;
	}

	private void validateGeohashLength(String geohash, int expectedLength) {
		if (geohash == null || geohash.length() != expectedLength) {
			throw new InvalidGeohashException();
		}
	}

	@Transactional
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

	@Transactional
	public void decreaseRegionDiaryCount(Double lat, Double lon) {
		sidoAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sido -> sidoAreasDiaryCountRepository.findById(sido.getId()))
			.ifPresent(count -> {
				count.decrementCount();
				sidoAreasDiaryCountRepository.save(count);
			});

		siggAreasRepository.findRegionByLatLon(lat, lon)
			.flatMap(sigg -> siggAreasDiaryCountRepository.findById(sigg.getGid()))
			.ifPresent(count -> {
				count.decrementCount();
				siggAreasDiaryCountRepository.save(count);
			});
	}
}
