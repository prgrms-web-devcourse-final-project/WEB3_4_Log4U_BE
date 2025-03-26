package com.example.log4u.domain.supports.entity;

import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SupportType supportType;

    private String title;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    private String answerContent;

    private LocalDateTime answeredAt;
}
