package com.example.log4u.fixture;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
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

	public static Diary createCustomDiaryFixture(
		Long diaryId,
		Long userId,
		String title,
		String content,
		String thumbnailUrl,
		VisibilityType visibility,
		Double latitude,
		Double longitude,
		WeatherInfo weatherInfo,
		Long likeCount
	) {
		return Diary.builder()
			.diaryId(diaryId)
			.userId(userId)
			.title(title)
			.content(content)
			.thumbnailUrl(thumbnailUrl)
			.visibility(visibility)
			.latitude(latitude)
			.longitude(longitude)
			.weatherInfo(weatherInfo)
			.likeCount(likeCount)
			.build();
	}

	public static List<Diary> createDiariesFixture() {
		List<Diary> diaries = new ArrayList<>();

		diaries.add(createCustomDiaryFixture(
			null, 1L, "첫번째 일기", "오늘은 날씨가 좋았다", "https://example.com/thumb1.jpg",
			VisibilityType.PUBLIC, 37.5665, 126.9780, WeatherInfo.SUNNY, 5L
		));

		diaries.add(createCustomDiaryFixture(
			null, 1L, "두번째 일기", "비밀 내용입니다", "https://example.com/thumb2.jpg",
			VisibilityType.PRIVATE, 37.5665, 126.9780, WeatherInfo.CLOUDY, 0L
		));

		diaries.add(createCustomDiaryFixture(
			null, 1L, "세번째 일기", "팔로워만 볼 수 있는 내용", "https://example.com/thumb3.jpg",
			VisibilityType.FOLLOWER, 37.5665, 126.9780, WeatherInfo.RAINY, 3L
		));

		diaries.add(createCustomDiaryFixture(
			null, 2L, "다른 사용자의 일기", "공개 내용", "https://example.com/thumb4.jpg",
			VisibilityType.PUBLIC, 35.1796, 129.0756, WeatherInfo.SUNNY, 10L
		));

		diaries.add(createCustomDiaryFixture(
			null, 2L, "인기 있는 일기", "좋아요가 많은 내용", "https://example.com/thumb5.jpg",
			VisibilityType.PUBLIC, 35.1796, 129.0756, WeatherInfo.SNOWY, 20L
		));

		return diaries;
	}
}
