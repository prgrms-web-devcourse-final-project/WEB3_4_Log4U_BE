package com.example.log4u.domain.diary.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.dto.LocationDto;
import com.example.log4u.domain.media.dto.MediaRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DiaryRequestDto(
	@NotBlank(message = "제목은 필수입니다.")
	String title,

	@NotBlank(message = "내용은 필수입니다.")
	String content,

	@NotNull(message = "다이어리 날짜는 필수입니다.")
	LocalDate diaryDate,

	@NotNull(message = "위치 정보는 필수입니다.")
	LocationDto location,

	@NotNull(message = "날씨 정보는 필수입니다.")
	WeatherInfo weatherInfo,

	@NotNull(message = "공개 범위는 필수입니다.")
	VisibilityType visibility,

	@NotNull(message = "미디어 첨부는 필수입니다.")
	@Size(min = 1, max = 10, message = "미디어는 최소 1개, 최대 10개까지만 업로드 가능합니다.")
	@Valid
	List<MediaRequestDto> mediaList,

	List<String> hashtagList
) {
	public static Diary toEntity(Long userId, DiaryRequestDto diaryRequestDto, String thumbnailUrl) {
		return Diary.builder()
			.userId(userId)
			.title(diaryRequestDto.title)
			.content(diaryRequestDto.content)
			.diaryDate(diaryRequestDto.diaryDate)
			.location(LocationDto.toEntity(diaryRequestDto.location))
			.weatherInfo(diaryRequestDto.weatherInfo)
			.visibility(diaryRequestDto.visibility)
			.thumbnailUrl(thumbnailUrl)
			.build();
	}
}
