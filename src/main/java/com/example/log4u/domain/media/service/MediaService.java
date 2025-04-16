package com.example.log4u.domain.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.exception.MediaLimitExceededException;
import com.example.log4u.domain.media.exception.NotFoundMediaException;
import com.example.log4u.domain.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

	private final MediaRepository mediaRepository;
	private final S3Service s3Service;

	@Transactional
	public void saveMedia(Long diaryId, List<MediaRequestDto> mediaList) {
		if (mediaList == null || mediaList.isEmpty()) {
			return;
		}
		// 미디어 개수 제한 검증
		validateMediaLimit(mediaList);

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
		// 1. 미디어 목록 조회
		List<Media> mediaList = mediaRepository.findByDiaryId(diaryId);

		if (mediaList.isEmpty()) {
			return;
		}

		// 2. DB에서 미디어 정보 삭제 (트랜잭션 내에서)
		mediaRepository.deleteByDiaryId(diaryId);

		// 3. S3에서 파일 비동기 삭제 (별도 트랜잭션에서)
		s3Service.deleteFilesFromS3(mediaList);
	}

	@Transactional
	public void updateMediaByDiaryId(Long diaryId, List<MediaRequestDto> newMediaList) {
		// 미디어 개수 제한 검증
		validateMediaLimit(newMediaList);
		// 모든 변경사항을 저장할 리스트
		List<Media> allMediaToSave = new ArrayList<>();

		// 기존 미디어 조회
		List<Media> existingMedia = mediaRepository.findByDiaryId(diaryId);

		// 새 미디어 ID 목록
		List<Long> newMediaIds = newMediaList.stream()
			.map(MediaRequestDto::mediaId)
			.toList();

		// 삭제할 미디어(기존에 있지만 새 목록에 없는 것)
		List<Media> mediaToDelete = new ArrayList<>();
		for (Media media : existingMedia) {
			if (!newMediaIds.contains(media.getMediaId())) {
				mediaToDelete.add(media);
			}
		}

		// 삭제할 미디어가 있으면 비동기로 S3에서 삭제
		if (!mediaToDelete.isEmpty()) {
			// DB에서 연결 해제
			mediaRepository.deleteAll(mediaToDelete);

			// S3에서 비동기 삭제
			s3Service.deleteFilesFromS3(mediaToDelete);
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

	@Transactional(readOnly = true)
	public Media getMediaById(Long mediaId) {
		return mediaRepository.findById(mediaId)
			.orElseThrow(NotFoundMediaException::new);
	}

	@Transactional
	public void deleteMediaById(Long mediaId) {
		Media media = mediaRepository.findById(mediaId)
			.orElseThrow(NotFoundMediaException::new);

		// DB에서 삭제
		mediaRepository.delete(media);

		// S3에서 비동기 삭제
		s3Service.deleteFilesFromS3(List.of(media));
	}

	// 미디어 개수 검증 로직
	private void validateMediaLimit(List<MediaRequestDto> mediaList) {
		if (mediaList.size() > 10) {
			throw new MediaLimitExceededException();
		}
	}

	private boolean isMediaListUnchanged(List<Media> existingMediaList, List<MediaRequestDto> newMediaList) {
		// 개수가 다르면 변경된 것
		if (existingMediaList.size() != newMediaList.size()) {
			return false;
		}

		Map<Long, Integer> existingMediaMap = existingMediaList.stream()
			.collect(Collectors.toMap(Media::getMediaId, Media::getOrderIndex));

		// 모든 새 미이더가 기존 미디어와 ID 및 순서가 동일한지 확인
		for (MediaRequestDto newMedia : newMediaList) {
			Integer existingOrder = existingMediaMap.get(newMedia.mediaId());

			// ID가 없거나 순서가 다르면 변경된 것
			if (existingOrder == null || !existingOrder.equals(newMedia.orderIndex())) {
				return false;
			}
		}
		return true;
	}
}