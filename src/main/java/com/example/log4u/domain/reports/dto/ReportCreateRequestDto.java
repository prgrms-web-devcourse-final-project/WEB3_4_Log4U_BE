package com.example.log4u.domain.reports.dto;

import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.reportType.ReportType;

public record ReportCreateRequestDto(
    ReportType reportType,
    String content
) {
    public Report toEntity(Report.ReportTargetType reportTargetType, Long targetId){
        return new Report(reportTargetType, reportType, targetId, content);
    }
}
