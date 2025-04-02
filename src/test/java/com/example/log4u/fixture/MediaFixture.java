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

	public static Media createMediaFixture(Long mediaId, Long diaryId) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName("image.jpg")
			.storedName("stored.jpg")
			.url("url.jpg")
			.contentType("image/jpeg")
			.size(1000L)
			.build();
	}

	public static Media createMediaFixture(Long mediaId, Long diaryId, MediaStatus status) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName("image.jpg")
			.storedName("stored.jpg")
			.url("url.jpg")
			.contentType("image/jpeg")
			.size(1000L)
			.status(status)
			.build();
	}

	public static Media createMediaFixture(Long mediaId, Long diaryId, String fileName, String contentType,
		MediaStatus status) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName(fileName)
			.storedName("images/" + UUID.randomUUID() + getExtension(fileName))
			.url("https://test-bucket.s3.ap-northeast-2.amazonaws.com/images/" + fileName)
			.contentType(contentType)
			.size(1000L)
			.status(status)
			.build();
	}

	public static List<Media> createMediaListFixture(Long diaryId, int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaFixture((long)(i + 1), diaryId))
			.toList();
	}

	public static List<Media> createMediaListFixture(Long diaryId, int count, MediaStatus status) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaFixture((long)(i + 1), diaryId, status))
			.toList();
	}

	public static MediaRequestDto createMediaRequestDto(Long mediaId) {
		return new MediaRequestDto(
			mediaId,
			"image.jpg",
			"stored.jpg",
			"url.jpg",
			"image/jpeg",
			1000L
		);
	}

	public static List<MediaRequestDto> createMediaRequestDtoList(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> createMediaRequestDto((long)i + 1))
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
