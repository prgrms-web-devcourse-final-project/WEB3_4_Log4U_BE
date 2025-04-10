package com.example.log4u.domain.hashtag.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.hashtag.entity.DiaryHashtag;
import com.example.log4u.domain.hashtag.entity.Hashtag;
import com.example.log4u.domain.hashtag.repository.DiaryHashtagRepository;
import com.example.log4u.domain.hashtag.repository.HashtagRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagService {

	private final HashtagRepository hashtagRepository;
	private final DiaryHashtagRepository diaryHashtagRepository;

	@Transactional
	public List<String> saveOrUpdateHashtag(Long diaryId, List<String> newHashtagNames) {
		// 1. 새 해시태그 이름 처리 (# 제거, 중복 제거)
		List<String> processedTags = newHashtagNames.stream()
			.map(this::processHashtag)
			.filter(tag -> !tag.isEmpty())
			.distinct()
			.toList();

		// 2. 기존 다이어리-해시태그 연결 조회
		List<DiaryHashtag> existingLinks = diaryHashtagRepository.findByDiaryId(diaryId);

		// 3. 기존 해시태그 ID 목록
		Set<Long> existingHashtagIds = existingLinks.stream()
			.map(DiaryHashtag::getHashtagId)
			.collect(Collectors.toSet());

		// 4. 새 해시태그 처리 및 ID 목록 생성
		Set<Long> newHashtagIds = new HashSet<>();
		List<DiaryHashtag> newLinks = new ArrayList<>();

		for (String tagName : processedTags) {
			// 해시태그 조회 또는 생성
			Hashtag hashtag = hashtagRepository.findByName(tagName)
				.orElseGet(() -> hashtagRepository.save(
					Hashtag.builder().name(tagName).build()
				));

			Long hashtagId = hashtag.getHashtagId();
			newHashtagIds.add(hashtagId);

			// 기존에 없는 연결만 추가
			if (!existingHashtagIds.contains(hashtagId)) {
				newLinks.add(DiaryHashtag.builder()
					.diaryId(diaryId)
					.hashtagId(hashtagId)
					.build());
			}
		}

		// 새 연결 저장
		if (!newLinks.isEmpty()) {
			diaryHashtagRepository.saveAll(newLinks);
		}

		// 5. 삭제할 연결 처리
		List<DiaryHashtag> linksToDelete = existingLinks.stream()
			.filter(link -> !newHashtagIds.contains(link.getHashtagId()))
			.toList();

		if (!linksToDelete.isEmpty()) {
			diaryHashtagRepository.deleteAll(linksToDelete);
		}

		// 6. 응답용 해시태그 목록 생성 (# 추가)
		return processedTags.stream()
			.map(tag -> "#" + tag)
			.toList();
	}

	// 다이어리의 해시태그 조회
	@Transactional(readOnly = true)
	public List<String> getHashtagsByDiaryId(Long diaryId) {
		List<DiaryHashtag> diaryHashtags = diaryHashtagRepository.findByDiaryId(diaryId);
		if (diaryHashtags.isEmpty()) {
			return List.of();
		}

		List<Long> hashtagIds = diaryHashtags.stream()
			.map(DiaryHashtag::getHashtagId)
			.toList();

		Map<Long, String> hashtagMap = hashtagRepository.findAllById(hashtagIds).stream()
			.collect(Collectors.toMap(Hashtag::getHashtagId, Hashtag::getName));

		return diaryHashtags.stream()
			.map(diaryHashtag -> hashtagMap.get(diaryHashtag.getHashtagId()))
			.filter(name -> name != null && !name.isEmpty())
			.toList();
	}

	@Transactional(readOnly = true)
	public Map<Long, List<String>> getHashtagMapByDiaryIds(List<Long> diaryIds) {
		if (diaryIds == null || diaryIds.isEmpty()) {
			return Map.of();
		}

		// 다이어리 ID로 DiaryHashtag 조회
		List<DiaryHashtag> diaryHashtags = diaryHashtagRepository.findByDiaryIdIn(diaryIds);

		if (diaryHashtags.isEmpty()) {
			return Map.of();
		}

		// 해시태그 ID 목록 추출
		List<Long> hashtagIds = diaryHashtags.stream()
			.map(DiaryHashtag::getHashtagId)
			.distinct()
			.toList();

		// 해시태그 조회 및 (ID, HashtagName) 맵 생성
		Map<Long, String> hashtagNameMap = hashtagRepository.findAllById(hashtagIds).stream()
			.collect(Collectors.toMap(Hashtag::getHashtagId, Hashtag::getName));

		// 다이어리별 해시태그 이름 목록 생성
		return diaryHashtags.stream()
			.collect(Collectors.groupingBy(
				DiaryHashtag::getDiaryId,
				Collectors.mapping(
					diaryHashtag -> hashtagNameMap.get(diaryHashtag.getHashtagId()),
					Collectors.filtering(Objects::nonNull, Collectors.toList())
				)
			));
	}

	// 해시태그 이름 처리 (# 제거)
	private String processHashtag(String hashtag) {
		if (hashtag == null || hashtag.isEmpty()) {
			return "";
		}

		// # 제거하고 공백 제거
		String processed = hashtag.trim();
		if (processed.startsWith("#")) {
			processed = processed.substring(1);
		}

		return processed.trim();
	}
}
