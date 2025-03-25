package com.example.log4u.domain.supports.dto;

import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record SupportGetResponseDto (
        long id,
        SupportType supportType,
        String title,
        String content,
        LocalDateTime createdAt,

        @Nullable
        String answerContent,

        @Nullable
        LocalDateTime answeredAt
) {
}
