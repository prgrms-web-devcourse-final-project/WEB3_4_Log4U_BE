package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.log4u.domain.diary.entity.DiaryGeoHash;

import io.lettuce.core.dynamic.annotation.Param;

public interface DiaryGeoHashRepository extends JpaRepository<DiaryGeoHash, Long> {

	@Query("SELECT d.diaryId FROM DiaryGeoHash d WHERE d.geohash = :geohash")
	List<Long> findDiaryIdByGeohash(@Param("geohash") String geohash);

	DiaryGeoHash findByDiaryId(Long diaryId);

	@Query(value = """
		SELECT g.*
		FROM diarygeohash g
		JOIN diary d ON d.diaryId = g.diaryId
		WHERE d.latitude = :lat
		  AND d.longitude = :lon
		""",
		nativeQuery = true)
	DiaryGeoHash findByLatLon(@Param("lat") double lat, @Param("lon") double lon);
}
