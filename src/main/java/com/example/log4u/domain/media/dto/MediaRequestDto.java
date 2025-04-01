package com.example.log4u.domain.media.dto;

import com.example.log4u.domain.media.entity.Media;

public record MediaRequestDto(
	Long mediaId,
	String originalName,
	String storedName,
	String url,
	String contentType,
	Long size
) {
	public static Media toEntity(Long diaryId, MediaRequestDto request) {
		return Media.builder()
			.diaryId(diaryId)
			.originalName(request.originalName())
			.storedName(request.storedName())
			.url(request.url())
			.contentType(request.contentType())
			.size(request.size())
			.build();
	}
}
