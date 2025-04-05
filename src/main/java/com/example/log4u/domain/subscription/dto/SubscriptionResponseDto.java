package com.example.log4u.domain.subscription.dto;

import java.time.LocalDateTime;

import com.example.log4u.domain.subscription.PaymentProvider;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record SubscriptionResponseDto(
	boolean isSubscriptionActive,

	@Nullable
	PaymentProvider paymentProvider,

	@Nullable
	LocalDateTime startDate
) {
}
