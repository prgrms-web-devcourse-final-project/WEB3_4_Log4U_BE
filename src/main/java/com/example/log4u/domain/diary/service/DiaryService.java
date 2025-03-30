package com.example.log4u.domain.diary.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DiaryService {

	private final DiaryRepository diaryRepository;

	public Diary getDiary(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(NotFoundDiaryException::new);
	}

	public Long incrementLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.incrementLikeCount();
	}

	public Long decreaseLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.decreaseLikeCount();
	}

	public Long getLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.getLikeCount();
	}

	public void checkDiaryExists(Long diaryId) {
		if (!diaryRepository.existsById(diaryId)) {
			throw new NotFoundDiaryException();
		}
	}

}
