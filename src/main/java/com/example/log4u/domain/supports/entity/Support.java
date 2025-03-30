package com.example.log4u.domain.supports.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.log4u.domain.supports.supportType.SupportType;

import jakarta.persistence.Column;
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

	@Column(nullable = false)
	private Long requesterId;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private SupportType supportType;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@CreatedDate
	private LocalDateTime createdAt;

	@Setter
	@Column(nullable = true)
	private String answerContent;

	@Column(nullable = true)
	private LocalDateTime answeredAt;
}
