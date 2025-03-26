package com.example.log4u.domain.diary.dto;

import java.util.List;

import com.example.log4u.domain.media.dto.MediaRequestDto;

public record DiaryRequestDto(
	String title,
	String content,
	Double latitude,
	Double longitude,
	String weatherInfo,
	String visibility,
	List<MediaRequestDto> mediaList
) {
}
