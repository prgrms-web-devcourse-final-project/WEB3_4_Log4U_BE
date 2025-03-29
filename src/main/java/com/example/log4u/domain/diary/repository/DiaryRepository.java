package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
	@Query("""
		SELECT d FROM Diary d 
		WHERE d.visibility IN :visibilities 
		AND (:keyword IS NULL OR 
		    LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
		    LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) 
		ORDER BY 
		    CASE WHEN :sort = 'POPULAR' THEN d.likeCount 
		    ELSE d.createdAt END DESC
		""")
	Page<Diary> searchDiaries(
		@Param("keyword") String keyword,
		@Param("visibilities") List<VisibilityType> visibilities,
		@Param("sort") String sort,
		Pageable pageable
	);

	@Query("""
		SELECT d FROM Diary d 
		WHERE d.userId = :userId 
		AND d.visibility IN :visibilities 
		AND (:cursorId IS NULL OR d.diaryId < :cursorId) 
		ORDER BY d.diaryId DESC
		""")
	Slice<Diary> findByUserIdAndVisibilityInAndCursorId(
		@Param("userId") Long userId,
		@Param("visibilities") List<VisibilityType> visibilities,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
}