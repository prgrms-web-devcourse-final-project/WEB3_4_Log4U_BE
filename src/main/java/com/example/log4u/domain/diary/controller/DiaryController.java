package com.example.log4u.domain.diary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

	private final DiaryService diaryService;

	@PostMapping
	public ResponseEntity<Void> createDiary(
		@Valid @RequestBody DiaryRequestDto request
	) {
		User user = mockUser();
		diaryService.saveDiary(user.getUserId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<PageResponse<DiaryResponseDto>> searchDiaries(
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "LATEST") SortType sort,
		@RequestParam(defaultValue = "0") int page
	) {
		return ResponseEntity.ok(
			diaryService.searchDiaries(keyword, sort, page)
		);
	}

	@GetMapping("/{diaryId}")
	public ResponseEntity<DiaryResponseDto> getDiary(
		@PathVariable Long diaryId
	) {
		User user = mockUser();
		DiaryResponseDto diary = diaryService.getDiary(user.getUserId(), diaryId);
		return ResponseEntity.ok(diary);
	}

	@PatchMapping("/{diaryId}")
	public ResponseEntity<Void> modifyDiary(
		@PathVariable Long diaryId,
		@Valid @RequestBody DiaryRequestDto request
	) {
		User user = mockUser();
		diaryService.updateDiary(user.getUserId(), diaryId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{diaryId}")
	public ResponseEntity<?> deleteDiary(
		@PathVariable Long diaryId
	) {
		User user = mockUser();
		diaryService.deleteDiary(user.getUserId(), diaryId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private User mockUser() {
		return User.builder()
			.userId(1L)
			.nickname("목유저")
			.providerId("12345")
			.provider("MOCK")
			.email("mock@mock.com")
			.statusMessage("목유저입니다.")
			.build();
	}
}