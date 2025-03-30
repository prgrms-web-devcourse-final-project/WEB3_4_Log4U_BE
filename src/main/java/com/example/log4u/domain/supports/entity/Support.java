package com.example.log4u.domain.supports.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.log4u.domain.supports.supportType.SupportType;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Support {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long requesterId;

	private SupportType supportType;

	private String title;

	private String content;

	@CreatedDate
	private LocalDateTime createdAt;

	@Setter
	private String answerContent;

	private LocalDateTime answeredAt;
}
