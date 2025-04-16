package com.example.log4u.domain.map.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.redis.RedisDao;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
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
	private final DiaryRepository diaryRepository;
	private final RedisDao redisDao;

	@Transactional(readOnly = true)
	public List<DiaryClusterResponseDto> getDiaryClusters(double south, double north, double west, double east, int zoom) {
		String redisKey;
		List<DiaryClusterResponseDto> clusters;

		// 줌 레벨 기준으로 캐시 키 결정 + Redis 조회
		if (zoom <= 10) {
			redisKey = "cluster:sido";
			clusters = redisDao.getList(redisKey, DiaryClusterResponseDto.class);

			// 캐시에 없으면 DB 조회 후 저장
			if (clusters == null) {
				clusters = sidoAreasRepository.findAllWithDiaryCount();  // 시/도 전체 조회
				redisDao.setList(redisKey, clusters, Duration.ofMinutes(5));
				log.info("[REDIS] 시/도 클러스터 캐시 새로 저장: {}", redisKey);
			}
		} else {
			redisKey = "cluster:sigg";
			clusters = redisDao.getList(redisKey, DiaryClusterResponseDto.class);

			// 캐시에 없으면 DB 조회 후 저장
			if (clusters == null) {
				clusters = siggAreasRepository.findAllWithDiaryCount();  // 시/군/구 전체 조회
				redisDao.setList(redisKey, clusters, Duration.ofMinutes(5));
				log.info("[REDIS] 시/군/구 클러스터 캐시 새로 저장: {}", redisKey);
			}
		}

		// 범위 필터링
		return clusters.stream()
			.filter(cluster ->
				cluster.lat() >= south && cluster.lat() <= north &&
				cluster.lon() >= west && cluster.lon() <= east)
			.toList();
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

	@Transactional(readOnly = true)
	public List<DiaryMarkerResponseDto> getDiariesInBounds(double south, double north, double west, double east) {
		return diaryRepository.findDiariesInBounds(south, north, west, east);
	}

}
