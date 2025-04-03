package com.example.log4u.fixture;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.media.dto.MediaRequestDto;

public class DiaryFixture {

	// 기본 다이어리 생성
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

	// 커스텀 다이어리 생성
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

	// 공개 다이어리 생성
	public static Diary createPublicDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "공개 일기", "누구나 볼 수 있는 내용", "https://example.com/public.jpg",
			VisibilityType.PUBLIC, 37.5665, 126.9780, WeatherInfo.SUNNY, 5L
		);
	}

	// 비공개 다이어리 생성
	public static Diary createPrivateDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "비공개 일기", "나만 볼 수 있는 내용", "https://example.com/private.jpg",
			VisibilityType.PRIVATE, 37.5665, 126.9780, WeatherInfo.CLOUDY, 0L
		);
	}

	// 팔로워 다이어리 생성
	public static Diary createFollowerDiaryFixture(Long diaryId, Long userId) {
		return createCustomDiaryFixture(
			diaryId, userId, "팔로워 일기", "팔로워만 볼 수 있는 내용", "https://example.com/follower.jpg",
			VisibilityType.FOLLOWER, 37.5665, 126.9780, WeatherInfo.RAINY, 3L
		);
	}

	// 순서가 있는 미디어 리스트를 포함한 DiaryRequestDto 생성
	public static DiaryRequestDto createDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto(1L, "image1.jpg", "stored1.jpg", "https://example.com/image1.jpg", "image/jpeg", 1000L,
				0),
			new MediaRequestDto(2L, "image2.jpg", "stored2.jpg", "https://example.com/image2.jpg", "image/jpeg", 2000L,
				1)
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

	// 공개 다이어리 요청 DTO 생성
	public static DiaryRequestDto createPublicDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto(3L, "public.jpg", "public_stored.jpg", "https://example.com/public.jpg", "image/jpeg",
				1000L, 0)
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

	// 비공개 다이어리 요청 DTO 생성
	public static DiaryRequestDto createPrivateDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto(4L, "private.jpg", "private_stored.jpg", "https://example.com/private.jpg",
				"image/jpeg", 1000L, 0)
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

	// 팔로워 다이어리 요청 DTO 생성
	public static DiaryRequestDto createFollowerDiaryRequestDtoFixture() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto(5L, "follower.jpg", "follower_stored.jpg", "https://example.com/follower.jpg",
				"image/jpeg", 1000L, 0)
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

	// 여러 미디어와 다양한 순서를 가진 다이어리 요청 DTO 생성
	public static DiaryRequestDto createDiaryRequestDtoWithMultipleMedia() {
		List<MediaRequestDto> mediaList = List.of(
			new MediaRequestDto(1L, "image1.jpg", "stored1.jpg", "https://example.com/image1.jpg", "image/jpeg", 1000L,
				2),
			new MediaRequestDto(2L, "image2.jpg", "stored2.jpg", "https://example.com/image2.jpg", "image/jpeg", 2000L,
				0),
			new MediaRequestDto(3L, "image3.jpg", "stored3.jpg", "https://example.com/image3.jpg", "image/jpeg", 3000L,
				1)
		);

		return new DiaryRequestDto(
			"여러 미디어 테스트",
			"순서가 다양한 미디어를 포함한 다이어리",
			37.5665,
			126.9780,
			WeatherInfo.SUNNY,
			VisibilityType.PUBLIC,
			mediaList
		);
	}

	public static List<Diary> createDiariesFixture() {
		List<Diary> diaries = new ArrayList<>();

		// 첫번째 일기 (userId1, PUBLIC, 좋아요 5개)
		Diary diary1 = Diary.builder()
			.userId(1L)
			.title("첫번째 일기")
			.content("오늘은 날씨가 좋았습니다.")
			.thumbnailUrl("https://example.com/thumbnail1.jpg")
			.visibility(VisibilityType.PUBLIC)
			.latitude(37.5665)
			.longitude(126.9780)
			.weatherInfo(WeatherInfo.SUNNY)
			.likeCount(5L)
			.build();

		// 두번째 일기 (userId1, PUBLIC, 좋아요 10개)
		Diary diary2 = Diary.builder()
			.userId(1L)
			.title("두번째 일기")
			.content("인기 있는 일기입니다.")
			.thumbnailUrl("https://example.com/thumbnail2.jpg")
			.visibility(VisibilityType.PUBLIC)
			.latitude(37.5665)
			.longitude(126.9780)
			.weatherInfo(WeatherInfo.CLOUDY)
			.likeCount(10L)
			.build();

		// 세번째 일기 (userId1, PRIVATE)
		Diary diary3 = Diary.builder()
			.userId(1L)
			.title("비공개 일기")
			.content("나만 볼 수 있는 내용입니다.")
			.thumbnailUrl("https://example.com/thumbnail3.jpg")
			.visibility(VisibilityType.PRIVATE)
			.latitude(37.5665)
			.longitude(126.9780)
			.weatherInfo(WeatherInfo.RAINY)
			.likeCount(0L)
			.build();

		// 네번째 일기 (userId1, FOLLOWER)
		Diary diary4 = Diary.builder()
			.userId(1L)
			.title("팔로워 일기")
			.content("팔로워만 볼 수 있는 내용입니다.")
			.thumbnailUrl("https://example.com/thumbnail4.jpg")
			.visibility(VisibilityType.FOLLOWER)
			.latitude(37.5665)
			.longitude(126.9780)
			.weatherInfo(WeatherInfo.SNOWY)
			.likeCount(3L)
			.build();

		// 다섯번째 일기 (userId2, PUBLIC, 좋아요 3개)
		Diary diary5 = Diary.builder()
			.userId(2L)
			.title("다른 사용자의 일기")
			.content("다른 사용자가 작성한 공개 일기입니다.")
			.thumbnailUrl("https://example.com/thumbnail5.jpg")
			.visibility(VisibilityType.PUBLIC)
			.latitude(35.1796)
			.longitude(129.0756)
			.weatherInfo(WeatherInfo.SUNNY)
			.likeCount(3L)
			.build();

		diaries.add(diary1);
		diaries.add(diary2);
		diaries.add(diary3);
		diaries.add(diary4);
		diaries.add(diary5);

		return diaries;
	}

	// ID가 지정된 다이어리 목록 생성
	public static List<Diary> createDiariesWithIdsFixture(int count) {
		List<Diary> diaries = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Diary diary = Diary.builder()
				.diaryId((long)(i + 1))
				.userId(1L)
				.title("테스트 다이어리 " + (i + 1))
				.content("테스트 내용 " + (i + 1))
				.thumbnailUrl("https://example.com/thumbnail" + (i + 1) + ".jpg")
				.visibility(VisibilityType.PUBLIC)
				.latitude(37.5665 + (i * 0.001))
				.longitude(126.9780 + (i * 0.001))
				.weatherInfo(WeatherInfo.SUNNY)
				.likeCount(5L + i)
				.build();

			diaries.add(diary);
		}

		return diaries;
	}

}