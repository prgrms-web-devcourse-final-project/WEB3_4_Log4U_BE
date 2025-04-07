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

@Tag(name = "지도 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MapController {

	private final MapService mapService;

	@GetMapping("/diaries/cluster")
	public ResponseEntity<List<DiaryClusterResponseDto>> getDiaryClusters(
		@RequestParam double south,
		@RequestParam double north,
		@RequestParam double west,
		@RequestParam double east,
		@RequestParam int zoom
	) {
		List<DiaryClusterResponseDto> clusters = mapService.getDiaryClusters(south, north, west, east, zoom);
		return ResponseEntity.ok(clusters);
	}

	@GetMapping("/diaries/marker")
	public ResponseEntity<List<DiaryMarkerResponseDto>> getDiariesInBounds(
		@RequestParam double south,
		@RequestParam double north,
		@RequestParam double west,
		@RequestParam double east
	) {
		List<DiaryMarkerResponseDto> diaries = mapService.getDiariesInBounds(south, north, west, east);
		return ResponseEntity.ok(diaries);
	}
}
