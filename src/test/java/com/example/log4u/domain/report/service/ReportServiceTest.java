package com.example.log4u.domain.report.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.reportType.ReportType;
import com.example.log4u.domain.reports.repository.ReportRepository;
import com.example.log4u.domain.reports.service.ReportService;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
	@InjectMocks
	private ReportService reportService;

	@Mock
	private ReportRepository reportRepository;

	@DisplayName("다이어리 신고 테스트")
	@Test
	void createDiaryReportTest() {
		ReportCreateRequestDto reportCreateRequestDto = new ReportCreateRequestDto(ReportType.ETC, "기타 신고");
		reportService.createDiaryReport(1L, reportCreateRequestDto, 1L);

		verify(reportRepository, times(1))
			.save(any());
	}

	@DisplayName("댓글 신고 테스트")
	@Test
	void createCommentReportTest() {
		ReportCreateRequestDto reportCreateRequestDto = new ReportCreateRequestDto(ReportType.ETC, "기타 신고");
		reportService.createCommentReport(1L, reportCreateRequestDto, 1L);

		verify(reportRepository, times(1))
			.save(any());
	}
}
