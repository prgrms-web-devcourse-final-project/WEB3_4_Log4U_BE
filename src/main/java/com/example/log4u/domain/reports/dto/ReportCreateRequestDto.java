package com.example.log4u.domain.reports.dto;

import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.reportType.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ReportCreateRequestDto(
    @NotNull
    ReportType reportType,

    @NotBlank
    @Length(min = 2)
    String content
) {
    public Report toEntity(Report.ReportTargetType reportTargetType, Long targetId){
        return Report.builder()
                .reportTargetType(reportTargetType)
                .reportType(reportType)
                .reportTargetId(targetId)
                .content(content)
                .build();
    }
}
