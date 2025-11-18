package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.DiaryGeoHash;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.MarkerCacheDao;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.entity.SiggAreas;
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
	private final DiaryGeohashService diaryGeohashService;

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

	@Transactional
	public void increaseRegionDiaryCount(Double lat, Double lon) {
		SidoAreas sido = updateSidoCount(lat, lon, +1);
		SiggAreas sigg = updateSiggCount(lat, lon, +1);
		clusterCacheDao.evictSido(sido.getGeohash());
		clusterCacheDao.evictSigg(sigg.getGeohash());
	}

	@Transactional
	public void decreaseRegionDiaryCount(Double lat, Double lon) {
		SidoAreas sido = updateSidoCount(lat, lon, -1);
		SiggAreas sigg = updateSiggCount(lat, lon, -1);
		clusterCacheDao.evictSido(sido.getGeohash());
		clusterCacheDao.evictSigg(sigg.getGeohash());
	}

	@Transactional
	public void updateRegionDiaryCount(double oldLat, double oldLon, double newLat, double newLon) {
		boolean sameSido = sidoAreasRepository.isSameSidoRegion(oldLat, oldLon, newLat, newLon);
		boolean sameSigg = siggAreasRepository.isSameSiggRegion(oldLat, oldLon, newLat, newLon);

		if (!sameSido) {
			updateSidoCount(oldLat, oldLon, -1);
			updateSidoCount(newLat, newLon, +1);
		}

		if (!sameSigg) {
			updateSiggCount(oldLat, oldLon, -1);
			updateSiggCount(newLat, newLon, +1);
		}

		DiaryGeoHash diaryGeoHash = diaryGeohashService.getGeohashByLatLon(oldLat, oldLon);
		markerCacheDao.evict(diaryGeoHash.getGeohash());
	}

	private SidoAreas updateSidoCount(Double lat, Double lon, int delta) {
		SidoAreas sido = sidoAreasRepository.findSidoAreasByLatLon(lat, lon);
		sidoAreasDiaryCountRepository.findById(sido.getId())
			.ifPresent(count -> {
				if (delta > 0) {
					count.incrementCount();
				} else if (delta < 0) {
					count.decrementCount();
				}
			});
		return sido;
	}

	private SiggAreas updateSiggCount(Double lat, Double lon, int delta) {
		SiggAreas sigg = siggAreasRepository.findSiggAreasByLatLon(lat, lon);
		siggAreasDiaryCountRepository.findById(sigg.getGid())
			.ifPresent(count -> {
				if (delta > 0) {
					count.incrementCount();
				} else if (delta < 0) {
					count.decrementCount();
				}
			});
		return sigg;
	}

	private void validateGeohashLength(String geohash, int expectedLength) {
		if (geohash == null || geohash.length() != expectedLength) {
			throw new InvalidGeohashException();
		}
	}
}
