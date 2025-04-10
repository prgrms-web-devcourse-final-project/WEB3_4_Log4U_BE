package com.example.log4u.domain.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.diary.DiaryFacade;
import com.example.log4u.domain.like.dto.request.LikeAddRequestDto;
import com.example.log4u.domain.like.dto.response.LikeAddResponseDto;
import com.example.log4u.domain.like.dto.response.LikeCancelResponseDto;
import com.example.log4u.domain.like.entity.Like;
import com.example.log4u.domain.like.exception.DuplicateLikeException;
import com.example.log4u.domain.like.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final DiaryFacade diaryFacade;

	@Transactional
	public LikeAddResponseDto addLike(Long userId, LikeAddRequestDto requestDto) {
		validateDuplicateLike(userId, requestDto.diaryId());

		Like like = requestDto.toEntity(userId);
		likeRepository.save(like);

		Long likeCount = diaryFacade.incrementLikeCount(requestDto.diaryId());
		return LikeAddResponseDto.of(true, likeCount);
	}

	@Transactional
	public LikeCancelResponseDto cancelLike(Long userId, Long diaryId) {
		return likeRepository.findByUserIdAndDiaryId(userId, diaryId)
			.map(like -> {
				likeRepository.delete(like);
				Long likeCount = diaryFacade.decrementLikeCount(diaryId);
				return LikeCancelResponseDto.of(false, likeCount);
			})
			.orElseGet(() -> {
				Long currentCount = diaryFacade.getLikeCount(diaryId);
				return LikeCancelResponseDto.of(false, currentCount);
			});
	}

	@Transactional(readOnly = true)
	public boolean existsLike(Long userId, Long diaryId) {
		return likeRepository.existsByUserIdAndDiaryId(userId, diaryId);
	}

	private void validateDuplicateLike(Long userId, Long diaryId) {

		if (likeRepository.existsByUserIdAndDiaryId(userId, diaryId)) {
			throw new DuplicateLikeException();
		}
	}
}
