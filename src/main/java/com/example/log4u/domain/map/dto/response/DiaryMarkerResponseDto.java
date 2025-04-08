package com.example.log4u.domain.map.dto.response;

import java.time.LocalDateTime;

public record DiaryMarkerResponseDto(
	Long diaryId,
	String title,
	String thumbnailUrl,
	Long likeCount,
	Double lat,
	Double lon,
	LocalDateTime createdAt

) {
}
