package com.example.log4u.domain.hashtag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.log4u.domain.hashtag.entity.DiaryHashtag;

public interface DiaryHashtagRepository extends JpaRepository<DiaryHashtag, Long> {

	List<DiaryHashtag> findByDiaryId(Long diaryId);

	@Modifying
	void deleteByDiaryId(Long diaryId);

	@Query("SELECT dh.diaryId FROM DiaryHashtag dh WHERE dh.hashtagId = :hashtagId")
	List<Long> findDiaryIdsByHashtagId(Long hashtagId);
}
