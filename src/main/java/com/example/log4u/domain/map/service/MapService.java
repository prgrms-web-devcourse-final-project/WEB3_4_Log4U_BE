package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.executor.RetryExecutor;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.entity.DiaryGeoHash;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.map.cache.dao.ClusterCacheDao;
import com.example.log4u.domain.map.cache.dao.MarkerCacheDao;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryClustersResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkersResponse;
import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.entity.SiggAreas;
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
	private final ClustersLocalCacheService clustersLocalCacheService;
	private final MarkersLocalCacheService markersLocalCacheService;

	private final SidoAreasRepository sidoAreasRepository;
	private final SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;
	private final SiggAreasRepository siggAreasRepository;
	private final SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;
	private final DiaryService diaryService;
	private final MarkerCacheDao markerCacheDao;
	private final ClusterCacheDao clusterCacheDao;
	private final DiaryGeohashService diaryGeohashService;

	private final RetryExecutor retryExecutor;

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClusters(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> areas = switch (level) {
				case 1 -> sidoAreasRepository.findSidoAreasCluster(geohash);
				case 2 -> siggAreasRepository.findSiggAreasCluster(geohash);
				default -> throw new InvalidMapLevelException();
			};
		return GetDiaryClustersResponse.of(areas);
	}

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByIndexScan(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> areas = switch (level) {
				case 1 -> sidoAreasRepository.findSidoAreasClusterByIndexScan(geohash);
				case 2 -> siggAreasRepository.findSiggAreasClusterByIndexScan(geohash);
				default -> throw new InvalidMapLevelException();
			};
		return GetDiaryClustersResponse.of(areas);
	}

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByRedisCache(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryClusterResponse> areas = clusterCacheDao.load(geohash, level);
			if (areas == null) {
				areas = clusterCacheDao.loadAndCache(geohash, level);
	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByLocalCache(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> diaryClusters = clustersLocalCacheService.getClusters(geohash, level);
		return GetDiaryClustersResponse.of(diaryClusters);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkers(String geohash) {
		List<Diary> diaries = diaryService.getDiariesByGeohash(geohash);
		return GetDiaryMarkersResponse.ofDiaries(diaries);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByIndexScan(String geohash) {
		List<Diary> diaries = diaryService.getDiariesByGeohashByIndexScan(geohash);
		return GetDiaryMarkersResponse.ofDiaries(diaries);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByRedisCache(String geohash) {
		validateGeohashLength(geohash, 5);
		return retryExecutor.runWithRetry(() -> {
			List<GetDiaryMarkerResponse> markers = markerCacheDao.load(geohash);
			if (markers == null) {
				markers = markerCacheDao.loadAndCache(geohash);
	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByLocalCache(String geohash) {
		validateGeohashLength(geohash, 5);
		List<GetDiaryMarkerResponse> markers = markersLocalCacheService.getMarkers(geohash);
		return GetDiaryMarkersResponse.ofMarkers(markers);
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
