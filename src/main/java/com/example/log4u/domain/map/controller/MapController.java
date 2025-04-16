package com.example.log4u.domain.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.map.dto.MyLocationRequestDto;
import com.example.log4u.domain.map.dto.ReverseGeocodingResponseDto;
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

	@Value("${naver.api.client-id}")
	private String clientId;

	@Value("${naver.api.client-secret}")
	private String secret;

	private final RestTemplate restTemplate;

	@GetMapping("/location")
	public ResponseEntity<ReverseGeocodingResponseDto> getMyLocation(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@ModelAttribute MyLocationRequestDto request
	) {

		String naverMapsUrl = UriComponentsBuilder
			.fromHttpUrl("https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc")
			.queryParam("coords", request.coords())
			.queryParam("output", request.output())
			.queryParam("orders", "legalcode,admcode,addr,roadaddr")
			.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-ncp-apigw-api-key-id", clientId);
		headers.set("x-ncp-apigw-api-key", secret);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<ReverseGeocodingResponseDto> response = restTemplate.exchange(
			naverMapsUrl,
			HttpMethod.GET,
			entity,
			ReverseGeocodingResponseDto.class
		);

		log.debug("역 지오코딩 결과 : " + String.valueOf(response) + "\n");
		return ResponseEntity.ok(response.getBody());
	}

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
