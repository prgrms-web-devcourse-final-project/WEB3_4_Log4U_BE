package com.example.log4u.domain.media.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequestDto(

	@NotBlank(message = "파일명은 필수입니다.")
	String filename,

	@NotBlank(message = "파일 타입은 필수입니다.")
	String contentType,

	@NotNull(message = "파일 크기는 필수입니다.")
	Long size
) {
}
