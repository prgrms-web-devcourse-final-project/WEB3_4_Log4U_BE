package com.example.log4u.domain.supports.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.service.SupportService;
import com.example.log4u.domain.supports.supportType.SupportType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supports")
public class SupportController {
	private final SupportService supportService;

	@PostMapping
	public ResponseEntity<Void> createSupport(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody @Valid SupportCreateRequestDto supportCreateRequestDto
	) {
		long requesterId = customOAuth2User.getUserId();
		supportService.createSupport(requesterId, supportCreateRequestDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<SupportOverviewGetResponseDto>> getSupportOverviewPage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(required = false) SupportType supportType
	) {
		long requesterId = customOAuth2User.getUserId();
		Page<SupportOverviewGetResponseDto> supportOverviewPage = supportService.getSupportPage(requesterId, page,
			supportType);
		return ResponseEntity.ok().body(supportOverviewPage);
	}

	@GetMapping("/{supportId}")
	public ResponseEntity<SupportGetResponseDto> getSupportBySupportId(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long supportId) {
		long requesterId = customOAuth2User.getUserId();
		SupportGetResponseDto supportGetResponseDto = supportService.getSupportById(requesterId, supportId);
		return ResponseEntity.ok().body(supportGetResponseDto);
	}
}
