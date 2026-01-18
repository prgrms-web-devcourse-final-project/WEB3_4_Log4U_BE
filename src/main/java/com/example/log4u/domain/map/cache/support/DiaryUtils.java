package com.example.log4u.domain.map.cache.support;

import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;

import java.util.Comparator;

public class DiaryUtils {

	private static final int MAX_DIARIES_LIMIT = 300;

	public static List<Diary> topByLikes(List<Diary> diaries) {
		if (diaries == null || diaries.isEmpty()) {
			return List.of();
		}

		return diaries.stream()
			.sorted(Comparator.comparingLong(Diary::getLikeCount).reversed())
			.limit(MAX_DIARIES_LIMIT)
			.toList();
	}
}
