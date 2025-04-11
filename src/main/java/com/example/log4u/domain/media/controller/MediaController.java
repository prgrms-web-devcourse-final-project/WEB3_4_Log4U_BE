package com.example.log4u.domain.media.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.media.dto.MediaResponseDto;
import com.example.log4u.domain.media.dto.PresignedUrlRequestDto;
import com.example.log4u.domain.media.dto.PresignedUrlResponseDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.domain.media.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

	private final MediaService mediaService;
	private final S3Service presignedUrlService;

	@PostMapping("/presigned-url")
	public ResponseEntity<PresignedUrlResponseDto> getPresignedUrl(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody PresignedUrlRequestDto request
	) {
		PresignedUrlResponseDto response = presignedUrlService.generatePresignedUrl(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{mediaId}")
	public ResponseEntity<MediaResponseDto> getMedia(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long mediaId
	) {
		Media media = mediaService.getMediaById(mediaId);
		return ResponseEntity.ok(MediaResponseDto.of(media));
	}

	@DeleteMapping("/{mediaId}")
	public ResponseEntity<Void> deleteMedia(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long mediaId
	) {
		mediaService.deleteMediaById(mediaId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
