package com.example.log4u.domain.diary.dto;

import java.util.List;

public record DiaryModifyRequestDto(
	String content,
	String weatherInfo,
	String visibility,
	List<String> fileUrls
) {
}
