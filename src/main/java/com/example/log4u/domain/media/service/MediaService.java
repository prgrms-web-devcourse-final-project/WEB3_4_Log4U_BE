package com.example.log4u.domain.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.exception.NotFoundMediaException;
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

		// 다이어리와 연결 및 순서 설정
		for (MediaRequestDto request : mediaList) {
			existingMedia.stream()
				.filter(media -> media.getMediaId().equals(request.mediaId()))
				.findFirst()
				.ifPresent(media -> {
					media.connectToDiary(diaryId);
					media.setOrderIndex(request.orderIndex());
				});
		}
		mediaRepository.saveAll(existingMedia);
	}

	// 사진 순서대로 조회
	@Transactional(readOnly = true)
	public List<Media> getMediaByDiaryId(Long diaryId) {
		return mediaRepository.findByDiaryIdOrderByOrderIndexAsc(diaryId);
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
		// 모든 변경사항을 저장할 리스트
		List<Media> allMediaToSave = new ArrayList<>();

		// 기존 미디어 조회
		List<Media> existingMedia = mediaRepository.findByDiaryId(diaryId);

		// 새 미디어 ID 목록
		List<Long> newMediaIds = newMediaList.stream()
			.map(MediaRequestDto::mediaId)
			.toList();

		// 삭제할 미디어(기존에 있지만 새 목록에 없는 것)
		for (Media media : existingMedia) {
			if (!newMediaIds.contains(media.getMediaId())) {
				media.markAsDeleted();
				allMediaToSave.add(media);
			}
		}

		// 새 미디어 연결
		if (!newMediaList.isEmpty()) {
			List<Media> newMedia = mediaRepository.findAllById(newMediaIds);
			for (MediaRequestDto request : newMediaList) {
				newMedia.stream()
					.filter(media -> media.getMediaId().equals(request.mediaId()))
					.findFirst()
					.ifPresent(media -> {
						if (media.getDiaryId() == null || !media.getDiaryId().equals(diaryId)) {
							media.connectToDiary(diaryId);
						}
						media.setOrderIndex(request.orderIndex());
						allMediaToSave.add(media);
					});
			}
		}

		if (!allMediaToSave.isEmpty()) {
			mediaRepository.saveAll(allMediaToSave);
		}
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
			return mediaRepository.findByDiaryIdInOrderByDiaryIdAscOrderIndexAsc(diaryIds)
				.stream()
				.collect(Collectors.groupingBy(Media::getDiaryId));
		} catch (Exception e) {
			return Map.of();
		}
	}

	public Media getMediaById(Long mediaId) {
		return mediaRepository.findById(mediaId)
			.orElseThrow(NotFoundMediaException::new);
	}

	@Transactional
	public void deleteMediaById(Long mediaId) {
		Media media = mediaRepository.findById(mediaId)
			.orElseThrow(NotFoundMediaException::new);
		media.markAsDeleted();
		mediaRepository.save(media);
	}
}
