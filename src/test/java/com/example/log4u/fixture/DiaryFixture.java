package com.example.log4u.fixture;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.media.dto.MediaRequestDto;

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

	public static List<Diary> createDiariesWithIdsFixture(int count) {
		List<Diary> diaries = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			diaries.add(createCustomDiaryFixture(
				(long)i, 1L, "제목 테스트 " + i, "내용 테스트" + i, "https://example.com/thumb" + i + ".jpg",
				VisibilityType.PUBLIC, 37.5665, 126.9780, WeatherInfo.SUNNY, (long)i
			));
		}
		return diaries;
	}

	public static List<Diary> createUserDiariesFixture(Long userId, int count) {
		List<Diary> diaries = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			diaries.add(createCustomDiaryFixture(
				(long)i, userId, "사용자 " + userId + "의 일기 " + i, "내용 " + i,
				"https://example.com/user" + userId + "/thumb" + i + ".jpg",
				VisibilityType.PUBLIC, 37.5665, 126.9780, WeatherInfo.SUNNY, (long)i
			));
		}
		return diaries;
	}

	public static Diary createPublicDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "공개 일기", "누구나 볼 수 있는 내용", "https://example.com/public.jpg",
			VisibilityType.PUBLIC, 37.5665, 126.9780, WeatherInfo.SUNNY, 5L
		);
	}

	public static Diary createPrivateDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "비공개 일기", "나만 볼 수 있는 내용", "https://example.com/private.jpg",
			VisibilityType.PRIVATE, 37.5665, 126.9780, WeatherInfo.CLOUDY, 0L
		);
	}

	public static Diary createFollowerDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "팔로워 일기", "팔로워만 볼 수 있는 내용", "https://example.com/follower.jpg",
			VisibilityType.FOLLOWER, 37.5665, 126.9780, WeatherInfo.RAINY, 3L
		);
	}

	public static DiaryRequestDto createDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto("image1.jpg", "stored1.jpg", "https://example.com/image1.jpg", "image/jpeg", 1000L),
			new MediaRequestDto("image2.jpg", "stored2.jpg", "https://example.com/image2.jpg", "image/jpeg", 2000L)
		);

		return new DiaryRequestDto(
			"테스트 제목",
			"테스트 내용",
			37.5665,
			126.9780,
			WeatherInfo.SUNNY,
			VisibilityType.PUBLIC,
			mediaList
		);
	}

	public static DiaryRequestDto createPublicDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto("public.jpg", "public_stored.jpg", "https://example.com/public.jpg", "image/jpeg",
				1000L)
		);

		return new DiaryRequestDto(
			"공개 테스트 제목",
			"공개 테스트 내용",
			37.5665,
			126.9780,
			WeatherInfo.SUNNY,
			VisibilityType.PUBLIC,
			mediaList
		);
	}

	public static DiaryRequestDto createPrivateDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto("private.jpg", "private_stored.jpg", "https://example.com/private.jpg", "image/jpeg",
				1000L)
		);

		return new DiaryRequestDto(
			"비공개 테스트 제목",
			"비공개 테스트 내용",
			37.5665,
			126.9780,
			WeatherInfo.CLOUDY,
			VisibilityType.PRIVATE,
			mediaList
		);
	}

	public static DiaryRequestDto createFollowerDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto("follower.jpg", "follower_stored.jpg", "https://example.com/follower.jpg", "image/jpeg",
				1000L)
		);

		return new DiaryRequestDto(
			"팔로워 테스트 제목",
			"팔로워 테스트 내용",
			37.5665,
			126.9780,
			WeatherInfo.RAINY,
			VisibilityType.FOLLOWER,
			mediaList
		);
	}
}
