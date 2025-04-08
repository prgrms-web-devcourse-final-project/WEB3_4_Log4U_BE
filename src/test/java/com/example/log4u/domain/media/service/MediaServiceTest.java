package com.example.log4u.domain.media.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;
import com.example.log4u.fixture.MediaFixture;

import software.amazon.awssdk.services.s3.S3Client;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

	@Mock
	private MediaRepository mediaRepository;

	@Mock
	private S3Client s3Client;

	@InjectMocks
	private MediaService mediaService;

	@Captor
	private ArgumentCaptor<List<Media>> mediaListCaptor;

	@Test
	@DisplayName("미디어 저장 성공")
	void saveMedia() {
		// given
		Long diaryId = 1L;
		List<MediaRequestDto> mediaList = MediaFixture.createMediaRequestDtoList(3);
		List<Long> mediaIds = mediaList.stream().map(MediaRequestDto::mediaId).toList();

		List<Media> existingMedia = List.of(
			MediaFixture.createMediaFixture(1L, null),
			MediaFixture.createMediaFixture(2L, null),
			MediaFixture.createMediaFixture(3L, null)
		);

		given(mediaRepository.findAllById(mediaIds)).willReturn(existingMedia);

		// when
		mediaService.saveMedia(diaryId, mediaList);

		// then
		verify(mediaRepository).saveAll(anyList());
		assertThat(existingMedia).allMatch(media -> media.getDiaryId().equals(diaryId));
	}

	@Test
	@DisplayName("미디어 저장 - 빈 리스트")
	void saveMedia_emptyList() {
		// given
		Long diaryId = 1L;
		List<MediaRequestDto> mediaList = List.of();

		// when
		mediaService.saveMedia(diaryId, mediaList);

		// then
		verify(mediaRepository, never()).findAllById(anyList());
		verify(mediaRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 조회 성공")
	void getMediaByDiaryId() {
		// given
		Long diaryId = 1L;
		List<Media> mediaList = MediaFixture.createMediaListFixture(diaryId, 3);

		given(mediaRepository.findByDiaryIdOrderByOrderIndexAsc(diaryId)).willReturn(mediaList);

		// when
		List<Media> result = mediaService.getMediaByDiaryId(diaryId);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting("diaryId").containsOnly(diaryId);
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 삭제 성공")
	void deleteMediaByDiaryId() {
		// given
		Long diaryId = 1L;
		List<Media> mediaList = MediaFixture.createMediaListFixture(diaryId, 3);

		given(mediaRepository.findByDiaryId(diaryId)).willReturn(mediaList);

		// when
		mediaService.deleteMediaByDiaryId(diaryId);

		// then
		verify(mediaRepository).saveAll(anyList());
		assertThat(mediaList).allMatch(media -> media.getStatus() == MediaStatus.DELETED);
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 업데이트 성공")
	void updateMediaByDiaryId() {
		// given
		Long diaryId = 1L;
		List<Media> existingMedia = List.of(
			MediaFixture.createMediaFixture(1L, diaryId),
			MediaFixture.createMediaFixture(2L, diaryId),
			MediaFixture.createMediaFixture(3L, diaryId)
		);

		List<MediaRequestDto> newMediaList = List.of(
			MediaFixture.createMediaRequestDto(2L),
			MediaFixture.createMediaRequestDto(3L),
			MediaFixture.createMediaRequestDto(4L)
		);

		List<Long> newMediaIds = newMediaList.stream()
			.map(MediaRequestDto::mediaId)
			.toList();

		List<Media> newMedia = List.of(
			MediaFixture.createMediaFixture(2L, diaryId),
			MediaFixture.createMediaFixture(3L, diaryId),
			MediaFixture.createMediaFixture(4L, null)
		);

		given(mediaRepository.findByDiaryId(diaryId)).willReturn(existingMedia);
		given(mediaRepository.findAllById(newMediaIds)).willReturn(newMedia);

		// when
		mediaService.updateMediaByDiaryId(diaryId, newMediaList);

		// then
		verify(mediaRepository).saveAll(mediaListCaptor.capture());
		List<Media> savedMedia = mediaListCaptor.getValue();

		// 미디어 1은 삭제 상태로 변경되어야 함
		boolean hasDeletedMedia = savedMedia.stream()
			.anyMatch(m -> m.getMediaId().equals(1L) && m.getStatus() == MediaStatus.DELETED);
		assertThat(hasDeletedMedia).isTrue();

		// 미디어 4는 다이어리와 연결되어야 함
		boolean hasConnectedMedia = savedMedia.stream()
			.anyMatch(m -> m.getMediaId().equals(4L) && m.getDiaryId().equals(diaryId));
		assertThat(hasConnectedMedia).isTrue();
	}

	@Test
	@DisplayName("썸네일 URL 추출")
	void extractThumbnailUrl() {
		// given
		List<MediaRequestDto> mediaList = MediaFixture.createMediaRequestDtoList(3);

		// when
		String thumbnailUrl = mediaService.extractThumbnailUrl(mediaList);

		// then
		assertThat(thumbnailUrl).isEqualTo(mediaList.get(0).url());
	}

	@Test
	@DisplayName("썸네일 URL 추출 - 빈 리스트")
	void extractThumbnailUrl_emptyList() {
		// given
		List<MediaRequestDto> emptyList = List.of();

		// when
		String thumbnailUrl = mediaService.extractThumbnailUrl(emptyList);

		// then
		assertThat(thumbnailUrl).isNull();
	}

	@Test
	@DisplayName("다이어리 ID 목록으로 미디어 맵 조회")
	void getMediaMapByDiaryIds() {
		// given
		Long diaryId1 = 1L;
		Long diaryId2 = 2L;
		List<Long> diaryIds = List.of(diaryId1, diaryId2);

		List<Media> allMedia = List.of(
			MediaFixture.createMediaFixture(1L, diaryId1),
			MediaFixture.createMediaFixture(2L, diaryId1),
			MediaFixture.createMediaFixture(3L, diaryId2)
		);

		given(mediaRepository.findByDiaryIdInOrderByDiaryIdAscOrderIndexAsc(diaryIds)).willReturn(allMedia);

		// when
		Map<Long, List<Media>> result = mediaService.getMediaMapByDiaryIds(diaryIds);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(diaryId1)).hasSize(2);
		assertThat(result.get(diaryId2)).hasSize(1);
	}

	@Test
	@DisplayName("미디어 순서만 변경")
	void updateMediaOrder_onlyChangeOrder() {
		// given
		Long diaryId = 1L;

		// 기존 미디어 (순서: 0, 1, 2)
		List<Media> existingMedia = List.of(
			MediaFixture.createMediaFixture(1L, diaryId, MediaStatus.PERMANENT, 0),
			MediaFixture.createMediaFixture(2L, diaryId, MediaStatus.PERMANENT, 1),
			MediaFixture.createMediaFixture(3L, diaryId, MediaStatus.PERMANENT, 2)
		);

		// 순서만 변경된 요청 (2, 0, 1)
		List<MediaRequestDto> newOrderList = List.of(
			new MediaRequestDto(1L, "test1.jpg", "stored1.jpg", "url1", "image/jpeg", 100L, 2),
			new MediaRequestDto(2L, "test2.jpg", "stored2.jpg", "url2", "image/jpeg", 100L, 0),
			new MediaRequestDto(3L, "test3.jpg", "stored3.jpg", "url3", "image/jpeg", 100L, 1)
		);

		given(mediaRepository.findByDiaryId(diaryId)).willReturn(existingMedia);
		given(mediaRepository.findAllById(anyList())).willReturn(existingMedia);

		// when
		mediaService.updateMediaByDiaryId(diaryId, newOrderList);

		// then
		verify(mediaRepository).saveAll(mediaListCaptor.capture());
		List<Media> savedMedia = mediaListCaptor.getValue();

		// 순서가 변경되었는지 확인
		assertThat(savedMedia).hasSize(3);

		// ID별로 순서 확인
		Map<Long, Integer> orderMap = savedMedia.stream()
			.collect(Collectors.toMap(Media::getMediaId, Media::getOrderIndex));

		assertThat(orderMap.get(1L)).isEqualTo(2);
		assertThat(orderMap.get(2L)).isEqualTo(0);
		assertThat(orderMap.get(3L)).isEqualTo(1);
	}

}
