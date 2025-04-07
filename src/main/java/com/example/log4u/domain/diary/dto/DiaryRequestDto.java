package com.example.log4u.domain.diary.dto;

import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.dto.LocationDto;
import com.example.log4u.domain.media.dto.MediaRequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DiaryRequestDto(
	@NotBlank(message = "제목은 필수입니다.")
	String title,

	@NotBlank(message = "내용은 필수입니다.")
	String content,

	LocationDto location,

	WeatherInfo weatherInfo,

	@NotNull(message = "공개 범위는 필수입니다.")
	VisibilityType visibility,

	@Size(max = 10, message = "미디어는 최대 10개까지만 업로드 가능합니다.")
	List<MediaRequestDto> mediaList
) {
	public static Diary toEntity(Long userId, DiaryRequestDto diaryRequestDto, String thumbnailUrl) {
		return Diary.builder()
			.userId(userId)
			.title(diaryRequestDto.title)
			.content(diaryRequestDto.content)
			.location(LocationDto.toEntity(diaryRequestDto.location))
			.weatherInfo(diaryRequestDto.weatherInfo)
			.visibility(diaryRequestDto.visibility)
			.thumbnailUrl(thumbnailUrl)
			.build();
	}
}
