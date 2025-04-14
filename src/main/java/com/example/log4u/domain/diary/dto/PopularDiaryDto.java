package com.example.log4u.domain.diary.dto;

import com.example.log4u.domain.diary.entity.Diary;

import lombok.Builder;

@Builder
public record PopularDiaryDto(
	Long diaryId,
	String title
) {
	public static PopularDiaryDto of(Diary diary) {
		return PopularDiaryDto.builder()
			.diaryId(diary.getDiaryId())
			.title(diary.getTitle())
			.build();
	}
}
