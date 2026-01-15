package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long>, CustomDiaryRepository {
	List<Diary> findTop10ByVisibilityOrderByLikeCountDesc(VisibilityType visibility);

	Integer countByUserId(Long userId);

	@Query(value = """
		SELECT d.*
		FROM Diary d
		JOIN DiaryGeoHash g ON d.diaryId = g.diaryId
		WHERE g.geohash = :geohash
		AND d.visibility = 'PUBLIC';
		""",
		nativeQuery = true)
	List<Diary> findDiariesByGeohash(@Param("geohash") String geohash);

	@Query(value = """
		SELECT /*+ INDEX(d idx_geohash_diary) */ d.*
		FROM Diary d
		JOIN DiaryGeoHash g ON d.diaryId = g.diaryId
		WHERE g.geohash = :geohash
		AND d.visibility = 'PUBLIC';
		""",
		nativeQuery = true)
	List<Diary> findDiariesByGeohashByIndexScan(@Param("geohash") String geohash);

}
