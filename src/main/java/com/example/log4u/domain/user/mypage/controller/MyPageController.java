package com.example.log4u.domain.user.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.domain.user.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MyPageController {
	private final MyPageService myPageService;

	@GetMapping("/users/me/diaries")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyDiaryPage(
		@RequestParam(required = false) Long cursorId
	) {
		Long userId = 1L;
		return ResponseEntity.ok(myPageService.getMyDiariesByCursor(userId, cursorId));
	}

	@GetMapping("/users/me/likes")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyLikesPage() {
	}

	@GetMapping("/users/me/followings")
	public ResponseEntity<PageResponse<UserThumbnailResponseDto>> getMyFollowingPage(
		@RequestParam(required = false) Long cursorId
	) {
		Long userId = 1L;
		return ResponseEntity.ok(myPageService.getMyFollowings(userId, cursorId));
	}

	@GetMapping("/users/me/followers")
	public ResponseEntity<PageResponse<UserThumbnailResponseDto>> getMyFollowerPage(
		@RequestParam(required = false) Long cursorId
	) {
		Long userId = 1L;
		return ResponseEntity.ok(myPageService.getMyFollowers(userId, cursorId));
	}

	@GetMapping("/users/me/subscriptions")
	public ResponseEntity<PageResponse<String>> getMySubscriptionsPage() {
	}
}
