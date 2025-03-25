package com.example.log4u.domain.supports.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SupportCreateRequestDto(
        SupportType supportType,

        @NotBlank
        @Length(min = 2)
        String title,

        @NotBlank
        @Length(min = 2)
        String content
) {
    public enum SupportType {
        TECHNICAL_ISSUE,
        ACCOUNT_ISSUE,
        PAYMENT_ISSUE,
        FEATURE_REQUEST,
        BILLING_ISSUE,
        SECURITY_CONCERN,
        ETC
    }
}
