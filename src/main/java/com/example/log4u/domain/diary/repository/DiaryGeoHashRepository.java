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
}
