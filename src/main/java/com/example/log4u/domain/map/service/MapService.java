package com.example.log4u.domain.map.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.entity.DiaryGeoHash;
import com.example.log4u.domain.diary.service.DiaryGeohashService;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.map.cache.ClustersCacheService;
import com.example.log4u.domain.map.cache.ClustersLocalCacheService;
import com.example.log4u.domain.map.cache.MarkersCacheService;
import com.example.log4u.domain.map.cache.MarkersLocalCacheService;
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

	private static final int INCREASE = 1;
	private static final int DECREASE = -1;

	private final ClustersCacheService clustersCacheService;
	private final ClustersLocalCacheService clustersLocalCacheService;
	private final MarkersCacheService markersCacheService;
	private final MarkersLocalCacheService markersLocalCacheService;
	private final DiaryService diaryService;
	private final DiaryGeohashService diaryGeohashService;

	private final SidoAreasRepository sidoAreasRepository;
	private final SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;
	private final SiggAreasRepository siggAreasRepository;
	private final SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClusters(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> diaryClusters = switch (level) {
			case 1 -> sidoAreasRepository.findSidoAreasCluster(geohash);
			case 2 -> siggAreasRepository.findSiggAreasCluster(geohash);
			default -> throw new InvalidMapLevelException();
		};
		return GetDiaryClustersResponse.of(diaryClusters);
	}

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByIndexScan(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> diaryClusters = switch (level) {
			case 1 -> sidoAreasRepository.findSidoAreasClusterByIndexScan(geohash);
			case 2 -> siggAreasRepository.findSiggAreasClusterByIndexScan(geohash);
			default -> throw new InvalidMapLevelException();
		};
		return GetDiaryClustersResponse.of(diaryClusters);
	}

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByRedisCache(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> diaryClusters = clustersCacheService.getClusters(geohash, level);
		return GetDiaryClustersResponse.of(diaryClusters);
	}

	@Transactional(readOnly = true)
	public GetDiaryClustersResponse getClustersByLocalCache(String geohash, int level) {
		validateGeohashLength(geohash, 3);
		List<GetDiaryClusterResponse> diaryClusters = clustersLocalCacheService.getClusters(geohash, level);
		return GetDiaryClustersResponse.of(diaryClusters);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkers(String geohash) {
		validateGeohashLength(geohash, 5);
		List<Diary> diaryMarkers = diaryService.getDiariesByGeohash(geohash);
		return GetDiaryMarkersResponse.of(diaryMarkers);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByIndexScan(String geohash) {
		validateGeohashLength(geohash, 5);
		List<Diary> diaryMarkers = diaryService.getDiariesByGeohashByIndexScan(geohash);
		return GetDiaryMarkersResponse.of(diaryMarkers);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByRedisCache(String geohash) {
		validateGeohashLength(geohash, 5);
		List<GetDiaryMarkerResponse> diaryMarkers = markersCacheService.getMarkers(geohash);
		return GetDiaryMarkersResponse.ofMarkers(diaryMarkers);
	}

	@Transactional(readOnly = true)
	public GetDiaryMarkersResponse getMarkersByLocalCache(String geohash) {
		validateGeohashLength(geohash, 5);
		List<GetDiaryMarkerResponse> markers = markersLocalCacheService.getMarkers(geohash);
		return GetDiaryMarkersResponse.ofMarkers(markers);
	}

	public void increaseRegionDiaryCount(double lat, double lon) {
		applyRegionDiaryCount(lat, lon, INCREASE);
	}

	public void decreaseRegionDiaryCount(double lat, double lon) {
		applyRegionDiaryCount(lat, lon, DECREASE);
	}

	public void updateRegionDiaryCount(double oldLat, double oldLon, double newLat, double newLon) {
		applyRegionDiaryCount(oldLat, oldLon, DECREASE);
		applyRegionDiaryCount(newLat, newLon, INCREASE);
	}

	private void applyRegionDiaryCount(double lat, double lon, int delta) {
		SidoAreas sido = sidoAreasRepository.findSidoAreasByLatLon(lat, lon);
		SiggAreas sigg = siggAreasRepository.findSiggAreasByLatLon(lat, lon);
		DiaryGeoHash diaryGeoHash = diaryGeohashService.getGeohashByLatLon(lat, lon);

		switch (delta) {
			case INCREASE -> {
				sidoAreasDiaryCountRepository.increase(sido.getId());
				siggAreasDiaryCountRepository.increase(sigg.getGid());
			}
			case DECREASE -> {
				sidoAreasDiaryCountRepository.decrease(sido.getId());
				siggAreasDiaryCountRepository.decrease(sigg.getGid());
			}
		}

		refreshCaches(sido.getGeohash(), sigg.getGeohash(), diaryGeoHash.getGeohash());
	}

	private void refreshCaches(String sidoGeohash, String siggGeohash, String markerGeohash) {
		clustersLocalCacheService.refresh(sidoGeohash, 1);
		clustersLocalCacheService.refresh(siggGeohash, 2);
		markersLocalCacheService.refresh(markerGeohash);
	}

	private void validateGeohashLength(String geohash, int expectedLength) {
		if (geohash == null || geohash.length() != expectedLength) {
			throw new InvalidGeohashException();
		}
	}
}
