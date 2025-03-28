package com.example.log4u.domain.media.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

	private final MediaRepository mediaRepository;

	@Transactional
	public void saveMedia(Long diaryId, List<MediaRequestDto> mediaList) {
		if (mediaList == null || mediaList.isEmpty()) {
			return;
		}
		List<Media> media = mediaList.stream()
			.map(mediaDto -> Media.toEntity(diaryId, mediaDto))
			.toList();

		mediaRepository.saveAll(media);
	}

	@Transactional(readOnly = true)
	public List<Media> getMedia(Long diaryId) {
		return mediaRepository.findByDiaryId(diaryId);
	}

	@Transactional
	public void deleteMedia(Long diaryId) {
		mediaRepository.deleteByDiaryId(diaryId);
	}

	@Transactional
	public void updateMedia(Long diaryId, List<MediaRequestDto> mediaList) {
		deleteMedia(diaryId);
		saveMedia(diaryId, mediaList);
		// TODO: 기존꺼 다 삭제하는게 아닌 변경된 이미지만 반영되도록 수정해야함
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
}
