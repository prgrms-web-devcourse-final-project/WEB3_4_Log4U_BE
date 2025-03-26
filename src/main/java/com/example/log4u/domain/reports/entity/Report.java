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
public class Report {
    public enum reportTargetType{
        DIARY,
        COMMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private reportTargetType reportTargetType;

    @Setter
    private ReportType reportType;

    @Setter
    private Long reportTargetId;

    @Setter
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
}
