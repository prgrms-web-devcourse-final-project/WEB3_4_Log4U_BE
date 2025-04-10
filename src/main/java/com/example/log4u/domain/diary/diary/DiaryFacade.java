package com.example.log4u.domain.diary.diary;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.like.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiaryFacade {

	private final DiaryRepository diaryRepository;
	private final LikeRepository likeRepository;

	@Transactional
	public Long incrementLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.incrementLikeCount();
	}

	@Transactional
	public Long decrementLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.decreaseLikeCount();
	}

	@Transactional
	public Long getLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.getLikeCount();
	}

	@Transactional(readOnly = true)
	public boolean existsLike(Long userId, Long diaryId) {
		return likeRepository.existsByUserIdAndDiaryId(userId, diaryId);
	}

	private Diary getDiary(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(NotFoundDiaryException::new);
	}
}
