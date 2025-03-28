package com.example.log4u.domain.reports.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;

	public void createDiaryReport(
		long reporterId,
		ReportCreateRequestDto reportCreateRequestDto,
		Long diaryId) {
		Report report = reportCreateRequestDto.toEntity(reporterId, Report.ReportTargetType.DIARY, diaryId);
		reportRepository.save(report);
	}

	public void createCommentReport(
		long reporterId,
		ReportCreateRequestDto reportCreateRequestDto,
		Long commentId) {
		Report report = reportCreateRequestDto.toEntity(reporterId, Report.ReportTargetType.COMMENT, commentId);
		reportRepository.save(report);
	}
}
