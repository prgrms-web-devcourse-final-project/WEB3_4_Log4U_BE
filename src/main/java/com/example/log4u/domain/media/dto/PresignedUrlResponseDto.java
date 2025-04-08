package com.example.log4u.domain.media.dto;

public record PresignedUrlResponseDto(
	Long mediaId,
	String presignedUrl,
	String accessUrl
) {
}
