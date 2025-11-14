package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.MarkerCacheDao;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.exception.InvalidGeohashException;
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
	private final MarkerCacheDao markerCacheDao;
	private final ClusterCacheDao clusterCacheDao;

	/**
	 * 캐싱 전략: Look-Aside + Write-Around
	 * 		[HIT] geohash -> Redis에 저장된 클러스터 배열(JSON) 읽어 반환
	 * 		[MISS] DB에서 geohash 셀 내 시/군/구 조회하여 Redis에 배열로 저장 후 반환
	 * level 기준:
	 *   	level 1: 시/도 단위 클러스터 (sido)
	 *   	level 2: 시/군/구 단위 클러스터 (sigg)
	 */
	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getDiaryClusters(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<DiaryClusterResponseDto> clusters = clusterCacheDao.load(geohash, level);
		if (clusters == null) {
			clusters = clusterCacheDao.loadAndCache(geohash, level);
		}
		return clusters;
	}

	/**
	 * 캐싱 전략: Look-Aside + Write-Around
	 * 		[HIT]  geohash -> Redis에 저장된 클러스터 배열(JSON) 읽어 반환
	 * 		[MISS] DB에서 geohash 셀 내 다이어리 조회하여 Redis에 배열로 저장 후 반환
	 */
	@Transactional(readOnly = true)
	public List<DiaryMarkerResponseDto> getDiaryMarkers(String geohash) {
		validateGeohashLength(geohash, 5);
		List<DiaryMarkerResponseDto> markers = markerCacheDao.load(geohash);
		if (markers == null) {
			markers = markerCacheDao.loadAndCache(geohash);
		}
		return markers;
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
