package com.example.log4u.domain.media.dto;

public record MediaRequestDto(
	String originalName,
	String storedName,
	String url,
	String contentType,
	Long size
) {
}
