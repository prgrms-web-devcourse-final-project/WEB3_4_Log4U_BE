package com.example.log4u.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.like.dto.request.LikeAddRequestDto;
import com.example.log4u.domain.like.dto.response.LikeAddResponseDto;
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
	public ResponseEntity<LikeAddResponseDto> addLike(@Valid @RequestBody LikeAddRequestDto requestDto) {
		Long userId = 1L; // 실제 구현에서는 토큰에서 추출

		LikeAddResponseDto response = likeService.addLike(userId, requestDto);
		return ResponseEntity.ok(response);
	}
}
