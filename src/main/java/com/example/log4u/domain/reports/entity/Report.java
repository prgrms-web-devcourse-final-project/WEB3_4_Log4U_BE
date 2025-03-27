package com.example.log4u.domain.reports.entity;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.reportType.ReportType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    public enum ReportTargetType{
        DIARY,
        COMMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportTargetType reportTargetType;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private Long reportTargetId;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    public Report(ReportTargetType reportTargetType, ReportType reportType, Long reportTargetId, String content) {
        this.reportTargetType = reportTargetType;
        this.reportType = reportType;
        this.reportTargetId = reportTargetId;
        this.content = content;
    }
}
