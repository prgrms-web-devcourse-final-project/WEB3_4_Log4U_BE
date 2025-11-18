package com.example.log4u.domain.map.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.map.service.MapService;

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

	@GetMapping("/diaries/cluster")
	public ResponseEntity<List<DiaryClusterResponseDto>> getDiaryClusters(
		@RequestParam String geohash,
		@RequestParam int level
	) {
		List<DiaryClusterResponseDto> clusters = mapService.getDiaryClusters(geohash, level);
		return ResponseEntity.ok(clusters);
	}

	@GetMapping("/diaries/marker")
	public ResponseEntity<List<DiaryMarkerResponseDto>> getDiaryMarkers(@RequestParam String geohash) {
		List<DiaryMarkerResponseDto> diaries = mapService.getDiaryMarkers(geohash);
		return ResponseEntity.ok(diaries);
	}
}
