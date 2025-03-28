package com.example.log4u.domain.supports.dto;

import java.time.LocalDateTime;

import com.example.log4u.domain.supports.supportType.SupportType;

import lombok.Builder;

@Builder
public record SupportOverviewGetResponseDto(
	long id,
	long requesterId,
	SupportType supportType,
	String title,
	LocalDateTime createdAt,
	boolean answered
) {
}
