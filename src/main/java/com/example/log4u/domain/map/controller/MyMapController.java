package com.example.log4u.domain.map.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.service.MyMapService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "나의 지도 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maps")
public class MyMapController {

	private final MyMapService myMapService;

	@GetMapping("/my/diaries/clusters")
	public ResponseEntity<List<DiaryClusterResponseDto>> getMyDiaryClusters(
		@RequestParam double south,
		@RequestParam double north,
		@RequestParam double west,
		@RequestParam double east,
		@RequestParam int zoom,
		@RequestParam Long userId // 실무에서는 인증 기반으로 가져오겠지만 지금은 param 사용
	) {
		List<DiaryClusterResponseDto> clusters = myMapService.getMyDiaryClusters(south, north, west, east, zoom, userId);
		return ResponseEntity.ok(clusters);
	}
}
