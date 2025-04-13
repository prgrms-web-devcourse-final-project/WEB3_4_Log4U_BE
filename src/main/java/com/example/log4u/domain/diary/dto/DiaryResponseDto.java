package com.example.log4u.domain.diary.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.dto.LocationDto;
import com.example.log4u.domain.media.dto.MediaResponseDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.domain.user.entity.User;

import lombok.Builder;

@Builder
public record DiaryResponseDto(
	Long diaryId,
	UserThumbnailResponseDto user,
	LocationDto location,
	String title,
	String content,
	String weatherInfo,
	String visibility,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	String thumbnailUrl,
	Long likeCount,
	List<MediaResponseDto> mediaList,
	List<String> hashtagList,
	boolean isLiked
) {
	// 단건 조회용 (isLiked + User)
	public static DiaryResponseDto of(
		Diary diary,
		List<Media> media,
		List<String> hashtagList,
		boolean isLiked,
		User user
	) {
		return DiaryResponseDto.builder()
			.diaryId(diary.getDiaryId())
			.user(UserThumbnailResponseDto.of(user))
			.location(LocationDto.of(diary.getLocation()))
			.title(diary.getTitle())
			.content(diary.getContent())
			.weatherInfo(diary.getWeatherInfo().name())
			.visibility(diary.getVisibility().name())
			.createdAt(diary.getCreatedAt())
			.updatedAt(diary.getUpdatedAt())
			.thumbnailUrl(diary.getThumbnailUrl())
			.likeCount(diary.getLikeCount())
			.mediaList(media.stream()
				.map(MediaResponseDto::of).toList())
			.hashtagList(hashtagList)
			.isLiked(isLiked)
			.build();
	}

	// 다이어리 목록 반환 시 사용 (isLiked false, User null 기본값)
	public static DiaryResponseDto of(
		Diary diary,
		List<Media> media,
		List<String> hashtagList
	) {
		return DiaryResponseDto.of(diary, media, hashtagList, false, null);
	}

}
