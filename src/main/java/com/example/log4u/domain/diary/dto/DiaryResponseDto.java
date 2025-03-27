package com.example.log4u.domain.diary.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.media.dto.MediaResponseDto;

import lombok.Builder;

@Builder
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
	Integer likesCount,
	List<MediaResponseDto> mediaList
	// TODO: isLiked 현재 로그인한 사용자의 좋아요 여부
) {
	public static DiaryResponseDto of(Diary diary) {
		return DiaryResponseDto.builder()
			.diaryId(diary.getId())
			.userId(diary.getUser().getId())
			.latitude(diary.getLatitude())
			.longitude(diary.getLongitude())
			.title(diary.getTitle())
			.content(diary.getContent())
			.weatherInfo(diary.getWeatherInfo())
			.visibility(diary.getVisibility().name())
			.createdAt(diary.getCreatedAt())
			.updatedAt(diary.getUpdatedAt())
			.thumbnailUrl(diary.getThumbnailUrl())
			.likesCount(diary.getLikesCount())
			.mediaList(diary.getMedia().stream()
				.map(MediaResponseDto::of).toList())
			.build();
	}
}
