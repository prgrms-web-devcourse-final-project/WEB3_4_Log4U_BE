package com.example.log4u.domain.reports.entity;

import com.example.log4u.domain.reports.reportType.ReportType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)

@Entity
@EntityListeners(AuditingEntityListener.class)
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
}
