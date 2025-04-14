package com.example.log4u.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.like.dto.request.LikeAddRequestDto;
import com.example.log4u.domain.like.dto.response.LikeAddResponseDto;
import com.example.log4u.domain.like.dto.response.LikeCancelResponseDto;
import com.example.log4u.domain.like.service.LikeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "좋아요 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

	private final LikeService likeService;

	@PostMapping
	public ResponseEntity<LikeAddResponseDto> addLike(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@Valid @RequestBody LikeAddRequestDto requestDto
	) {
		Long userId = customOAuth2User.getUserId();

		LikeAddResponseDto response = likeService.addLike(userId, requestDto);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{diaryId}")
	public ResponseEntity<LikeCancelResponseDto> cancelLike(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long diaryId) {
		Long userId = customOAuth2User.getUserId();

		LikeCancelResponseDto response = likeService.cancelLike(userId, diaryId);
		return ResponseEntity.ok(response);
	}
}
