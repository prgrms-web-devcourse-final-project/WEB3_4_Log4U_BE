package com.example.log4u.domain.media.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {

	@Modifying
	void deleteByDiaryId(Long diaryId);

	List<Media> findByDiaryId(Long diaryId);

	List<Media> findByDiaryIdIn(List<Long> diaryIds);

	// 임시 상태이면서 특정 시간 이전에 생성된 미디어 조회
	List<Media> findByStatusAndCreatedAtBefore(MediaStatus status, LocalDateTime dateTime);

	List<Media> findByStatus(MediaStatus status);

	List<Media> findByDiaryIdOrderByOrderIndexAsc(Long diaryId);

	List<Media> findByDiaryIdInOrderByDiaryIdAscOrderIndexAsc(List<Long> diaryIds);
}
