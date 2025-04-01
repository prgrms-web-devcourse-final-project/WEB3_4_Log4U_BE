package com.example.log4u.domain.diary.dto;

import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.media.dto.MediaRequestDto;

import jakarta.validation.constraints.NotBlank;

public record DiaryRequestDto(
	@NotBlank(message = "제목은 필수입니다.")
	String title,
	@NotBlank(message = "내용은 필수입니다.")
	String content,
	Double latitude,
	Double longitude,
	WeatherInfo weatherInfo,
	@NotBlank(message = "공개 범위는 필수입니다.")
	VisibilityType visibility,
	List<MediaRequestDto> mediaList
) {
	public static Diary toEntity(Long userId, DiaryRequestDto diaryRequestDto, String thumbnailUrl) {
		return Diary.builder()
			.userId(userId)
			.title(diaryRequestDto.title)
			.content(diaryRequestDto.content)
			.latitude(diaryRequestDto.latitude)
			.longitude(diaryRequestDto.longitude)
			.weatherInfo(diaryRequestDto.weatherInfo)
			.visibility(diaryRequestDto.visibility)
			.thumbnailUrl(thumbnailUrl)
			.build();
	}
}
