package com.example.log4u.domain.user.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MyPageController {

	@GetMapping("/users/me/diaries")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyDiaryPage() {
	}

	@GetMapping("/users/me/likes")
	public ResponseEntity<PageResponse<DiaryResponseDto>> getMyLikesPage() {
	}

	@GetMapping("/users/me/followings")
	public ResponseEntity<PageResponse<String>> getMyFollowingPage() {
	}

	@GetMapping("/users/me/followers")
	public ResponseEntity<PageResponse<String>> getMyFollowerPage() {
	}

	@GetMapping("/users/me/subscriptions")
	public ResponseEntity<PageResponse<String>> getMySubscriptionsPage() {
	}
}
