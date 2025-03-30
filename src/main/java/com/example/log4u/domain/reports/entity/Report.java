package com.example.log4u.domain.reports.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.log4u.domain.reports.reportTargetType.ReportTargetType;
import com.example.log4u.domain.reports.reportType.ReportType;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long reporterId;

	@Enumerated(EnumType.STRING)
	private ReportTargetType reportTargetType;

	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	private Long reportTargetId;

	private String content;

	@CreatedDate
	private LocalDateTime createdAt;
}
