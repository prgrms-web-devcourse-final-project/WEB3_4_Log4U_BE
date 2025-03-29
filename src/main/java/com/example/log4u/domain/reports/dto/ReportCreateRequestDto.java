package com.example.log4u.domain.reports.dto;

import org.hibernate.validator.constraints.Length;

import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.reportTargetType.ReportTargetType;
import com.example.log4u.domain.reports.reportType.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequestDto(
	@NotNull
	ReportType reportType,

	@NotBlank
	@Length(min = 2)
	String content
) {
	public Report toEntity(long reporterId, ReportTargetType reportTargetType, Long targetId) {
		return Report.builder()
			.reporterId(reporterId)
			.reportTargetType(reportTargetType)
			.reportType(reportType)
			.reportTargetId(targetId)
			.content(content)
			.build();
	}
}
