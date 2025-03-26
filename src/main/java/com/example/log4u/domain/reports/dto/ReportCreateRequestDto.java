package com.example.log4u.domain.reports.dto;

import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.reportType.ReportType;

public record ReportCreateRequestDto(
    ReportType reportType,
    Long targetId,
    String content
) {
    public Report toEntity(){
        Report report = new Report();
        report.setReportTargetId(targetId);
        report.setContent(content);
        report.setReportType(reportType);
        return report;
    }
}
