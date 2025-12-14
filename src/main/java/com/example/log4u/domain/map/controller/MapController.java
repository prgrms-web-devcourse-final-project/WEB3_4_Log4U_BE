package com.example.log4u.domain.map.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.map.dto.response.GetDiaryClustersResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkersResponse;
import com.example.log4u.domain.map.service.MapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "지도 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
@Slf4j
public class MapController {

	private final MapService mapService;

	@Operation(summary = "다이어리 클러스터 조회 (DB Full Table Scan)")
	@GetMapping("/diaries/cluster")
	public ResponseEntity<GetDiaryClustersResponse> getClusters(
		@Parameter(description = "조회 기준 geohash (예: 'wyd')") @RequestParam String geohash,
		@Parameter(description = "클러스터 레벨 (1: 시/도, 2: 시/군/구)") @RequestParam int level) {
		GetDiaryClustersResponse response = mapService.getClusters(geohash, level);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "다이어리 클러스터 조회 (DB Index Range Scan)")
	@GetMapping("/diaries/cluster-index")
	public ResponseEntity<GetDiaryClustersResponse> getClustersByIndexScan(
		@Parameter(description = "조회 기준 geohash (예: 'wyd')") @RequestParam String geohash,
		@Parameter(description = "클러스터 레벨 (1: 시/도, 2: 시/군/구)") @RequestParam int level) {
		GetDiaryClustersResponse response = mapService.getClustersByIndexScan(geohash, level);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "다이어리 클러스터 조회 (Redis Cache)")
	@GetMapping("/diaries/cluster-redis")
	public ResponseEntity<GetDiaryClustersResponse> getClustersByRedis(
		@Parameter(description = "조회 기준 geohash (예: 'wyd')") @RequestParam String geohash,
		@Parameter(description = "클러스터 레벨 (1: 시/도, 2: 시/군/구)") @RequestParam int level) {
		GetDiaryClustersResponse response = mapService.getClustersByRedisCache(geohash, level);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "다이어리 마커 조회 (DB Full Table Scan)")
	@GetMapping("/diaries/marker")
	public ResponseEntity<GetDiaryMarkersResponse> getMarkers(
		@Parameter(description = "조회 기준 geohash (예: 'wydm6')") @RequestParam String geohash) {
		GetDiaryMarkersResponse response = mapService.getMarkers(geohash);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "다이어리 마커 조회 (DB Index Range Scan)")
	@GetMapping("/diaries/marker-index")
	public ResponseEntity<GetDiaryMarkersResponse> getMarkersByIndexScan(
		@Parameter(description = "조회 기준 geohash (예: 'wydm6')") @RequestParam String geohash) {
		GetDiaryMarkersResponse response = mapService.getMarkersByIndexScan(geohash);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "다이어리 마커 조회 (Redis Cache)")
	@GetMapping("/diaries/marker-redis")
	public ResponseEntity<GetDiaryMarkersResponse> getMarkersByRedis(
		@Parameter(description = "조회 기준 geohash (예: 'wydm6')") @RequestParam String geohash) {
		GetDiaryMarkersResponse response = mapService.getMarkersByRedisCache(geohash);
		return ResponseEntity.ok(response);
	}

}
