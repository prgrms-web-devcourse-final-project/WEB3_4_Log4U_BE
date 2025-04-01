package com.example.log4u.domain.media.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

	private final MediaRepository mediaRepository;
	private final S3Client s3Client;

	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	@Transactional
	public void saveMedia(Long diaryId, List<MediaRequestDto> mediaList) {
		if (mediaList == null || mediaList.isEmpty()) {
			return;
		}

		// 미디어 ID 목록 추출
		List<Long> mediaIds = mediaList.stream()
			.map(MediaRequestDto::mediaId)
			.toList();

		List<Media> existingMedia = mediaRepository.findAllById(mediaIds);

		// 다이어리와 연결
		for (Media media : existingMedia) {
			media.connectToDiary(diaryId);
		}

		mediaRepository.saveAll(existingMedia);
	}

	@Transactional(readOnly = true)
	public List<Media> getMediaByDiaryId(Long diaryId) {
		return mediaRepository.findByDiaryId(diaryId);
	}

	@Transactional
	public void deleteMediaByDiaryId(Long diaryId) {
		List<Media> mediaList = mediaRepository.findByDiaryId(diaryId);

		// 미디어 삭제 상태로 변경
		for (Media media : mediaList) {
			media.markAsDeleted();
		}

		mediaRepository.saveAll(mediaList);
	}

	@Transactional
	public void updateMediaByDiaryId(Long diaryId, List<MediaRequestDto> newMediaList) {
		// 기존 미디어 조회
		List<Media> existingMedia = mediaRepository.findByDiaryId(diaryId);

		// 새 미디어 ID 목록
		List<Long> newMediaIds = newMediaList.stream()
			.map(MediaRequestDto::mediaId)
			.toList();

		// 삭제할 미디어(기존에 있지만 새 목록에 없는 것)
		List<Media> mediaToDelete = existingMedia.stream()
			.filter(media -> !newMediaIds.contains(media.getMediaId()))
			.toList();

		// 미디어 삭제 상태로 변경
		for (Media media : mediaToDelete) {
			media.markAsDeleted();
		}

		mediaRepository.saveAll(mediaToDelete);

		saveMedia(diaryId, newMediaList);
	}

	public String extractThumbnailUrl(List<MediaRequestDto> mediaList) {
		if (mediaList == null || mediaList.isEmpty()) {
			return null;
		}
		return mediaList.getFirst().url();
	}

	public Map<Long, List<Media>> getMediaMapByDiaryIds(List<Long> diaryIds) {
		if (diaryIds.isEmpty()) {
			return Map.of();
		}
		try {
			return mediaRepository.findByDiaryIdIn(diaryIds)
				.stream()
				.collect(Collectors.groupingBy(Media::getDiaryId));
		} catch (Exception e) {
			return Map.of();
		}
	}

	public Media getMediaById(Long mediaId) {
		return mediaRepository.findById(mediaId)
			.orElseThrow();
	}

	public void deleteMediaById(Long mediaId) {
		Media media = mediaRepository.findById(mediaId)
			.orElseThrow();
		media.markAsDeleted();
		mediaRepository.save(media);
	}
}
