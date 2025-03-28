package com.example.log4u.domain.diary.dto;

import java.util.List;

import com.example.log4u.domain.media.dto.MediaRequestDto;

import jakarta.validation.constraints.NotBlank;

public record DiaryRequestDto(
	@NotBlank(message = "제목은 필수입니다.")
	String title,
	@NotBlank(message = "내용은 필수입니다.")
	String content,
	Double latitude,
	Double longitude,
	String weatherInfo,
	@NotBlank(message = "공개 범위는 필수입니다.")
	String visibility,
	List<MediaRequestDto> mediaList
) {
}
