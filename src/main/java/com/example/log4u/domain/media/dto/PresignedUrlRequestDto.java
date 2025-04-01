package com.example.log4u.domain.media.dto;

public record PresignedUrlRequestDto(
	String filename,
	String contentType,
	Long size
) {
}
