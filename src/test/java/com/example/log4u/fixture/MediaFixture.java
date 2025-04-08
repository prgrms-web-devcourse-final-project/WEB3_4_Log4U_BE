package com.example.log4u.fixture;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.dto.PresignedUrlRequestDto;
import com.example.log4u.domain.media.dto.PresignedUrlResponseDto;
import com.example.log4u.domain.media.entity.Media;

public class MediaFixture {

	// 기본 미디어 생성 (orderIndex 기본값 0)
	public static Media createMediaFixture(Long mediaId, Long diaryId) {
		return createMediaFixture(mediaId, diaryId, MediaStatus.PERMANENT, 0);
	}

	// 상태를 지정하는 미디어 생성 (orderIndex 기본값 0)
	public static Media createMediaFixture(Long mediaId, Long diaryId, MediaStatus status) {
		return createMediaFixture(mediaId, diaryId, status, 0);
	}

	// 순서를 지정하는 미디어 생성
	public static Media createMediaFixture(Long mediaId, Long diaryId, Integer orderIndex) {
		return createMediaFixture(mediaId, diaryId, MediaStatus.PERMANENT, orderIndex);
	}

	// 상태와 순서를 모두 지정하는 미디어 생성 (기본 메서드)
	public static Media createMediaFixture(Long mediaId, Long diaryId, MediaStatus status, Integer orderIndex) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName("image.jpg")
			.storedName("stored.jpg")
			.url("url.jpg")
			.contentType("image/jpeg")
			.size(1000L)
			.status(status)
			.orderIndex(orderIndex)
			.build();
	}

	// 파일명과 컨텐츠 타입을 지정하는 미디어 생성
	public static Media createMediaFixture(Long mediaId, Long diaryId, String fileName, String contentType,
		MediaStatus status, Integer orderIndex) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName(fileName)
			.storedName("images/" + UUID.randomUUID() + getExtension(fileName))
			.url("https://test-bucket.s3.ap-northeast-2.amazonaws.com/images/" + fileName)
			.contentType(contentType)
			.size(1000L)
			.status(status)
			.orderIndex(orderIndex)
			.build();
	}

	// 순서대로 정렬된 미디어 리스트 생성
	public static List<Media> createMediaListFixture(Long diaryId, int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaFixture((long)(i + 1), diaryId, i))  // orderIndex를 i로 설정
			.toList();
	}

	// 상태를 지정하고 순서대로 정렬된 미디어 리스트 생성
	public static List<Media> createMediaListFixture(Long diaryId, int count, MediaStatus status) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaFixture((long)(i + 1), diaryId, status, i))  // orderIndex를 i로 설정
			.toList();
	}

	// 순서를 섞은 미디어 리스트 생성 (테스트용)
	public static List<Media> createShuffledMediaListFixture(Long diaryId, int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaFixture((long)(i + 1), diaryId, count - i - 1))  // 역순으로 orderIndex 설정
			.toList();
	}

	// 기본 MediaRequestDto 생성 (orderIndex 기본값 0)
	public static MediaRequestDto createMediaRequestDto(Long mediaId) {
		return createMediaRequestDto(mediaId, 0);
	}

	// 순서를 지정하는 MediaRequestDto 생성
	public static MediaRequestDto createMediaRequestDto(Long mediaId, Integer orderIndex) {
		return new MediaRequestDto(
			mediaId,
			"image.jpg",
			"stored.jpg",
			"url.jpg",
			"image/jpeg",
			1000L,
			orderIndex
		);
	}

	// 순서대로 정렬된 MediaRequestDto 리스트 생성
	public static List<MediaRequestDto> createMediaRequestDtoList(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaRequestDto((long)i + 1, i))  // orderIndex를 i로 설정
			.toList();
	}

	// 순서를 섞은 MediaRequestDto 리스트 생성 (테스트용)
	public static List<MediaRequestDto> createShuffledMediaRequestDtoList(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaRequestDto((long)i + 1, count - i - 1))  // 역순으로 orderIndex 설정
			.toList();
	}

	public static PresignedUrlRequestDto createPresignedUrlRequestDto() {
		return new PresignedUrlRequestDto(
			"test.jpg",
			"image/jpeg",
			1000L
		);
	}

	public static PresignedUrlResponseDto createPresignedUrlResponseDto(Long mediaId) {
		return new PresignedUrlResponseDto(
			mediaId,
			"https://test-bucket.s3.ap-northeast-2.amazonaws.com/images/test.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=...",
			"https://test-bucket.s3.ap-northeast-2.amazonaws.com/images/test.jpg"
		);
	}

	private static String getExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
	}
}