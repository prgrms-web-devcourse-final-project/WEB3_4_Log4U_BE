package com.example.log4u.domain.hashtag.service;

import java.util.List;
import java.util.Map;
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
	public List<String> saveHashtag(Long diaryId, List<String> hashtagNames) {
		// 1. 기존 해시태그 연결 삭제
		diaryHashtagRepository.deleteByDiaryId(diaryId);

		if (hashtagNames == null || hashtagNames.isEmpty()) {
			return List.of();
		}

		// 2. 해시태그 이름 정제 (# 제거, 중복 제거, 빈 문자열 제거)
		List<String> processedTags = hashtagNames.stream()
			.map(this::processHashtag)
			.filter(hashtag -> !hashtag.isEmpty())
			.toList();

		if (processedTags.isEmpty()) {
			return List.of();
		}

		// 3. 각 해시태그 처리
		for (String hashtagName : processedTags) {
			Hashtag hashtag = hashtagRepository.findByName(hashtagName)
				.orElseGet(() -> hashtagRepository.save(
					Hashtag.builder()
						.name(hashtagName)
						.build()
				));

			diaryHashtagRepository.save(
				DiaryHashtag.builder()
					.diaryId(diaryId)
					.hashtagId(hashtag.getHashtagId())
					.build()
			);
		}
		return processedTags;
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
