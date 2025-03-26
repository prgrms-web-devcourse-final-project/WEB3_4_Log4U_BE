package com.example.log4u.fixture;

import com.example.log4u.domain.diary.entity.Diary;

public class DiaryFixture {

	public static Diary createDiaryFixture() {
		return Diary.builder()
			.diaryId(1L)
			.userId(1L)
			.title("테스트 다이어리")
			.thumbnailUrl("thumbnail.jpg")
			.content("다이어리 내용입니다.")
			.latitude(37.1234)
			.longitude(127.5678)
			.likeCount(11L)
			.build();
	}
}
