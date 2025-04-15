package com.example.log4u.common.redis;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClusterCacheInitializer {

	private final RedisDao redisDao;
	private final SidoAreasRepository sidoAreasRepository;
	private final SiggAreasRepository siggAreasRepository;

	@PostConstruct
	public void init() {
		//시/도 클러스터 캐시 초기화 (zoom 1~10 공통 사용)
		List<DiaryClusterResponseDto> sidoClusters = sidoAreasRepository.findAllWithDiaryCount();
		String sidoKey = "cluster:sido";
		redisDao.setList(sidoKey, sidoClusters, Duration.ofMinutes(5));
		log.info("[REDIS] 시/도 클러스터 캐시 저장 완료: {}", sidoKey);

		//시/군/구 클러스터 캐시 초기화 (zoom 11~13 공통 사용)
		List<DiaryClusterResponseDto> siggClusters = siggAreasRepository.findAllWithDiaryCount();
		String siggKey = "cluster:sigg";
		redisDao.setList(siggKey, siggClusters, Duration.ofMinutes(5));
		log.info("[REDIS] 시/군/구 클러스터 캐시 저장 완료: {}", siggKey);
	}
}
