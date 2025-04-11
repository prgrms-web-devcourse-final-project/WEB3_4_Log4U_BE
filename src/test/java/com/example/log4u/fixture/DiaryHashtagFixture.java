package com.example.log4u.fixture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.log4u.domain.hashtag.entity.DiaryHashtag;
import com.example.log4u.domain.hashtag.entity.Hashtag;

public class DiaryHashtagFixture {

	// 기본 다이어리-해시태그 연결 생성
	public static DiaryHashtag createDefaultDiaryHashtag() {
		return DiaryHashtag.builder()
			.diaryHashtagId(1L)
			.diaryId(1L)
			.hashtagId(1L)
			.build();
	}

	// ID, 다이어리ID, 해시태그ID로 다이어리-해시태그 연결 생성
	public static DiaryHashtag createDiaryHashtag(Long id, Long diaryId, Long hashtagId) {
		return DiaryHashtag.builder()
			.diaryHashtagId(id)
			.diaryId(diaryId)
			.hashtagId(hashtagId)
			.build();
	}

	// 다이어리ID와 해시태그ID 목록으로 다이어리-해시태그 연결 목록 생성
	public static List<DiaryHashtag> createDiaryHashtags(Long diaryId, List<Long> hashtagIds) {
		List<DiaryHashtag> diaryHashtags = new ArrayList<>();
		for (int i = 0; i < hashtagIds.size(); i++) {
			diaryHashtags.add(createDiaryHashtag((long)(i + 1), diaryId, hashtagIds.get(i)));
		}
		return diaryHashtags;
	}

	// 다이어리ID와 해시태그 목록으로 다이어리-해시태그 연결 목록 생성
	public static List<DiaryHashtag> createDiaryHashtagsFromHashtags(Long diaryId, List<Hashtag> hashtags) {
		List<DiaryHashtag> diaryHashtags = new ArrayList<>();
		for (int i = 0; i < hashtags.size(); i++) {
			diaryHashtags.add(createDiaryHashtag((long)(i + 1), diaryId, hashtags.get(i).getHashtagId()));
		}
		return diaryHashtags;
	}

	// 여러 다이어리에 대한 해시태그 연결 생성
	public static List<DiaryHashtag> createMultipleDiaryHashtags(List<Long> diaryIds, List<Hashtag> hashtags) {
		List<DiaryHashtag> diaryHashtags = new ArrayList<>();
		int id = 1;

		for (Long diaryId : diaryIds) {
			for (Hashtag hashtag : hashtags) {
				diaryHashtags.add(createDiaryHashtag((long)id++, diaryId, hashtag.getHashtagId()));
			}
		}

		return diaryHashtags;
	}

	// 다이어리별 해시태그 맵 생성
	public static Map<Long, List<DiaryHashtag>> createDiaryHashtagMap(List<Long> diaryIds, List<Hashtag> hashtags) {
		List<DiaryHashtag> allLinks = createMultipleDiaryHashtags(diaryIds, hashtags);
		return allLinks.stream()
			.collect(Collectors.groupingBy(DiaryHashtag::getDiaryId));
	}

	// 특정 다이어리에 대한 해시태그 연결 생성 (가변 인자 버전)
	public static List<DiaryHashtag> createDiaryHashtagsForDiary(Long diaryId, Long... hashtagIds) {
		return IntStream.range(0, hashtagIds.length)
			.mapToObj(i -> createDiaryHashtag((long)(i + 1), diaryId, hashtagIds[i]))
			.collect(Collectors.toList());
	}
}