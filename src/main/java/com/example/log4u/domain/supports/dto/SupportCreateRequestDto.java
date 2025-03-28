package com.example.log4u.domain.supports.dto;

import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record SupportCreateRequestDto(
        @NotNull
        SupportType supportType,

        @NotBlank
        @Length(min = 2)
        String title,

        @NotBlank
        @Length(min = 2)
        String content
) {
        public Support toEntity(){
                return new Support(supportType, title, content);
        }
}
