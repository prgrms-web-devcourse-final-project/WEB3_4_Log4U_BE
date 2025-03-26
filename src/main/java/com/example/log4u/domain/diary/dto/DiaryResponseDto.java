package com.example.log4u.domain.diary.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.log4u.domain.media.dto.MediaResponseDto;

public record DiaryResponseDto(
	Long diaryId,
	Long userId,
	Double latitude,
	Double longitude,
	String title,
	String content,
	String weatherInfo,
	String visibility,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	String thumbnailUrl,
	List<MediaResponseDto> mediaList
) {
}
