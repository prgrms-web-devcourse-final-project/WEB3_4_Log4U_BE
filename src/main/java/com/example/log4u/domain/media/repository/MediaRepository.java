package com.example.log4u.domain.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.log4u.domain.media.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {

	@Modifying
	@Query("DELETE FROM Media m WHERE m.diaryId = :diaryId")
	void deleteByDiaryId(@Param("diaryId") Long diaryId);
}
