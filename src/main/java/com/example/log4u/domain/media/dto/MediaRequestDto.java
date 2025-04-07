package com.example.log4u.domain.media.dto;

import com.example.log4u.domain.media.entity.Media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaRequestDto(

	Long mediaId,

	@NotBlank(message = "원본 파일명은 필수입니다.")
	String originalName,

	@NotBlank(message = "저장된 파일명은 필수입니다.")
	String storedName,

	@NotBlank(message = "파일 URL은 필수입니다.")
	String url,

	@NotBlank(message = "파일 타입은 필수입니다.")
	String contentType,

	@NotNull(message = "파일 크기는 필수입니다.")
	Long size,

	@NotNull(message = "파일 순서는 필수입니다.")
	Integer orderIndex
) {
	public static Media toEntity(Long diaryId, MediaRequestDto request) {
		return Media.builder()
			.diaryId(diaryId)
			.originalName(request.originalName())
			.storedName(request.storedName())
			.url(request.url())
			.contentType(request.contentType())
			.size(request.size())
			.orderIndex(request.orderIndex())
			.build();
	}
}
