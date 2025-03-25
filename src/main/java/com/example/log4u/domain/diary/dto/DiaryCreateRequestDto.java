package com.example.log4u.domain.diary.dto;

public record DiaryCreateRequestDto(
	String title,
	String content,
	float latitude,
	float longitude,
	String weatherInfo,
	String visibility
) {
}
