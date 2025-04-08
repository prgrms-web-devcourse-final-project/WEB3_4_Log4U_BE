package com.example.log4u.domain.user.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.subscription.dto.SubscriptionResponseDto;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.domain.user.mypage.service.MyPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MyPageController {
	private final MyPageService myPageService;

	@GetMapping("/users/me/diaries")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyDiaryPage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false, name = "visibilityType") VisibilityType visibilityType,
		@RequestParam(required = false) Long cursorId
	) {
		long userId = customOAuth2User.getUserId();
		return ResponseEntity.ok(myPageService.getMyDiariesByCursor(userId, visibilityType, cursorId));
	}

	@GetMapping("/users/me/likes")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyLikesPage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) Long cursorId
	) {
		long userId = customOAuth2User.getUserId();
		return ResponseEntity.ok(myPageService.getLikeDiariesByCursor(userId, cursorId));
	}

	@GetMapping("/users/me/followings")
	public ResponseEntity<PageResponse<UserThumbnailResponseDto>> getMyFollowingPage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) Long cursorId
	) {
		long userId = customOAuth2User.getUserId();
		return ResponseEntity.ok(myPageService.getMyFollowings(userId, cursorId));
	}

	@GetMapping("/users/me/followers")
	public ResponseEntity<PageResponse<UserThumbnailResponseDto>> getMyFollowerPage(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) Long cursorId
	) {
		long userId = customOAuth2User.getUserId();
		return ResponseEntity.ok(myPageService.getMyFollowers(userId, cursorId));
	}

	@GetMapping("/users/me/subscriptions")
	public ResponseEntity<SubscriptionResponseDto> getMySubscriptions(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		long userId = customOAuth2User.getUserId();
		return ResponseEntity.ok(myPageService.getMySubscription(userId));
	}
}
