package com.example.log4u.domain.supports.dto;

import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SupportGetResponseDto (
        long id,
        long requesterId,
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
