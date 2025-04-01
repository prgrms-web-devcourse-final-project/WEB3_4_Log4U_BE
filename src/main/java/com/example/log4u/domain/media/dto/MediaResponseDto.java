package com.example.log4u.domain.media.dto;

import com.example.log4u.domain.media.entity.Media;

import lombok.Builder;

@Builder
public record MediaResponseDto(
	Long mediaId,
	String fileUrl,
	String contentType
) {
	public static MediaResponseDto of(Media media) {
		return MediaResponseDto.builder()
			.mediaId(media.getMediaId())
			.fileUrl(media.getUrl())
			.contentType(media.getContentType())
			.build();
	}
}
