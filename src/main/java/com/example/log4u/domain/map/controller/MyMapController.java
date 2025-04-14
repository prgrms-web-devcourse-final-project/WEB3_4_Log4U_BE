package com.example.log4u.domain.map.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
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
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		List<DiaryClusterResponseDto> clusters =
			myMapService.getMyDiaryClusters(south, north, west, east, zoom, customOAuth2User.getUserId());
		return ResponseEntity.ok(clusters);
	}

	@GetMapping("/my/diaries/marker")
	public ResponseEntity<List<DiaryMarkerResponseDto>> getMyDiariesInBounds(
		@RequestParam double south,
		@RequestParam double north,
		@RequestParam double west,
		@RequestParam double east,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		List<DiaryMarkerResponseDto> diaries = myMapService.getMyDiariesInBounds(customOAuth2User.getUserId(), south,
			north, west, east);
		return ResponseEntity.ok(diaries);
	}

}
