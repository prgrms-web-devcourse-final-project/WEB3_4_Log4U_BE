package com.example.log4u.domain.supports.dto;

import com.example.log4u.domain.supports.supportType.SupportType;

import java.time.LocalDateTime;

public record SupportOverviewGetResponseDto(
        long id,
        SupportType supportType,
        String title,
        LocalDateTime createdAt,
        boolean answered
) {
}
