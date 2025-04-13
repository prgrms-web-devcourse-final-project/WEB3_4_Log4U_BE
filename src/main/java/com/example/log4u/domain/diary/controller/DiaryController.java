package com.example.log4u.domain.diary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.facade.DiaryFacade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

	private final DiaryFacade diaryFacade;

	@GetMapping("/users/{userId}")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getDiariesByUserId(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable(name = "userId") Long targetUserId,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "12") int size
	) {
		PageResponse<DiaryResponseDto> response = diaryFacade.getDiariesByCursor(customOAuth2User.getUserId(),
			targetUserId, cursorId, size);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/users/me")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyDiaries(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "12") int size
	) {
		PageResponse<DiaryResponseDto> response = diaryFacade.getDiariesByCursor(customOAuth2User.getUserId(),
			customOAuth2User.getUserId(), cursorId, size);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<Void> createDiary(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@Valid @RequestBody DiaryRequestDto request
	) {
		diaryFacade.createDiary(customOAuth2User.getUserId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/search")
	public ResponseEntity<PageResponse<DiaryResponseDto>> searchDiaries(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "LATEST") SortType sort,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "12") int size
	) {
		return ResponseEntity.ok(
			diaryFacade.searchDiariesByCursor(keyword, sort, cursorId, size)
		);
	}

	@GetMapping("/{diaryId}")
	public ResponseEntity<DiaryResponseDto> getDiary(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long diaryId
	) {
		DiaryResponseDto diary = diaryFacade.getDiary(customOAuth2User.getUserId(), diaryId);
		return ResponseEntity.ok(diary);
	}

	@PatchMapping("/{diaryId}")
	public ResponseEntity<Void> modifyDiary(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long diaryId,
		@Valid @RequestBody DiaryRequestDto request
	) {
		diaryFacade.updateDiary(customOAuth2User.getUserId(), diaryId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{diaryId}")
	public ResponseEntity<Void> deleteDiary(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long diaryId
	) {
		diaryFacade.deleteDiary(customOAuth2User.getUserId(), diaryId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}