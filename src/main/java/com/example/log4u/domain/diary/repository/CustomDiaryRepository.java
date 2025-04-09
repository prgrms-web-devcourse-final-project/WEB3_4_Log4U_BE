package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;

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

	Slice<Diary> searchDiariesByCursor(
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

	List<DiaryMarkerResponseDto> findDiariesInBounds(double south, double north, double west, double east);

	List<Diary> findInBoundsByUserId(Long userId, double south, double north, double west, double east);

	List<DiaryMarkerResponseDto> findMyDiariesInBounds(Long userId, double south, double north, double west, double east);
}
