package com.example.log4u.domain.reports.service;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public Report createDiaryReport(ReportCreateRequestDto reportCreateRequestDto) {
        Report report = reportCreateRequestDto.toEntity();
        report.setReportTargetType(Report.reportTargetType.DIARY);
        reportRepository.save(report);
        return report;
    }

    public Report createCommentReport(ReportCreateRequestDto reportCreateRequestDto) {
        Report report = reportCreateRequestDto.toEntity();
        report.setReportTargetType(Report.reportTargetType.COMMENT);
        reportRepository.save(report);
        return report;
    }
}
