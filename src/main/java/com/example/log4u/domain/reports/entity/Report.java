package com.example.log4u.domain.reports.entity;

import jakarta.persistence.*;
import lombok.Getter;
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

    private reportTargetType reportTargetType;

    private Long reportTargetId;

    private String reportContent;

    @CreatedDate
    private LocalDateTime createdAt;
}
