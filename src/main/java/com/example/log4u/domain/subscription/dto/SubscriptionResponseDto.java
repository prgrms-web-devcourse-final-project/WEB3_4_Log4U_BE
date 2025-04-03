package com.example.log4u.domain.subscription.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record SubscriptionResponseDto(
	boolean isSubscriptionActive,

	@Nullable
	LocalDateTime startDate,

	@Nullable
	LocalDateTime endDate
) {
}
