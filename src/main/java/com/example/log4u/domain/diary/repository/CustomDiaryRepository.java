package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryWithAuthorDto;
import com.example.log4u.domain.diary.entity.Diary;

public interface CustomDiaryRepository {

	Slice<DiaryWithAuthorDto> findByUserIdAndVisibilityInAndCursorId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable
	);

	Slice<DiaryWithAuthorDto> searchDiariesByCursor(
		String keyword,
		List<VisibilityType> visibilities,
		SortType sort,
		Long cursorId,
		Pageable pageable
	);

	Slice<Diary> getLikeDiarySliceByUserId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable);

}
