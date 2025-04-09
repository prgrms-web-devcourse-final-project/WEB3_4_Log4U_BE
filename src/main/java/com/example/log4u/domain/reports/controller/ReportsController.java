package com.example.log4u.domain.reports.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportsController {
	private final ReportService reportService;

	@PostMapping("/diaries/{diaryId}")
	public ResponseEntity<Void> createReportForDiary(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody ReportCreateRequestDto reportCreateRequestDto,
		@PathVariable Long diaryId
	) {
		long reporterId = customOAuth2User.getUserId();
		reportService.createDiaryReport(reporterId, reportCreateRequestDto, diaryId);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/comments/{commentId}")
	public ResponseEntity<Void> createReport(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody ReportCreateRequestDto reportCreateRequestDto,
		@PathVariable Long commentId
	) {
		long reporterId = customOAuth2User.getUserId();
		reportService.createCommentReport(reporterId, reportCreateRequestDto, commentId);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
