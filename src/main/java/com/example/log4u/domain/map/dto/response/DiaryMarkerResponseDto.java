package com.example.log4u.domain.map.dto.response;

import java.time.LocalDateTime;

import com.example.log4u.domain.diary.entity.Diary;

public record DiaryMarkerResponseDto(
	Long diaryId,
	String title,
	String thumbnailUrl,
	Long likeCount,
	Double lat,
	Double lon,
	LocalDateTime createdAt
) {
	public static DiaryMarkerResponseDto of(Diary diary) {
		return new DiaryMarkerResponseDto(
			diary.getDiaryId(),
			diary.getTitle(),
			diary.getThumbnailUrl(),
			diary.getLikeCount(),
			diary.getLocation().getLatitude(),
			diary.getLocation().getLongitude(),
			diary.getCreatedAt()
		);
	}
}
