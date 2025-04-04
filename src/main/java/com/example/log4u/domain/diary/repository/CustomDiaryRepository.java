package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;

public interface CustomDiaryRepository {
	Page<Diary> searchDiaries(
		String keyword,
		List<VisibilityType> visibilities,
		SortType sort,
		Pageable pageable
	);

	Slice<Diary> findByUserIdAndVisibilityInAndCursorId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable
	);
}
