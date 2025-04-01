package com.example.log4u.domain.reports.entity;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.domain.reports.reportTargetType.ReportTargetType;
import com.example.log4u.domain.reports.reportType.ReportType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)

@Entity
public class Report extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long reporterId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportTargetType reportTargetType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	@Column(nullable = false)
	private Long reportTargetId;

	@Column(nullable = false)
	private String content;
}
