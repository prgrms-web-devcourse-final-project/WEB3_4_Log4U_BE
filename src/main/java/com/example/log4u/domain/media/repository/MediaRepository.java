package com.example.log4u.domain.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.log4u.domain.media.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {

	@Modifying
	void deleteByDiaryId(Long diaryId);

	List<Media> findByDiaryId(Long diaryId);

	List<Media> findByDiaryIdIn(List<Long> diaryIds);
}
