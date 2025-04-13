package com.example.log4u.domain.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.service.DiaryService;
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
	private final DiaryService diaryService;

	@Transactional
	public LikeAddResponseDto addLike(Long userId, LikeAddRequestDto requestDto) {
		validateDuplicateLike(userId, requestDto.diaryId());

		Like like = requestDto.toEntity(userId);
		likeRepository.save(like);

		Long likeCount = diaryService.incrementLikeCount(requestDto.diaryId());
		return LikeAddResponseDto.of(true, likeCount);
	}

	@Transactional
	public LikeCancelResponseDto cancelLike(Long userId, Long diaryId) {
		return likeRepository.findByUserIdAndDiaryId(userId, diaryId)
			.map(like -> {
				likeRepository.delete(like);
				Long likeCount = diaryService.decreaseLikeCount(diaryId);
				return LikeCancelResponseDto.of(false, likeCount);
			})
			.orElseGet(() -> {
				Long currentCount = diaryService.getLikeCount(diaryId);
				return LikeCancelResponseDto.of(false, currentCount);
			});
	}

	private void validateDuplicateLike(Long userId, Long diaryId) {
		if (likeRepository.existsByUserIdAndDiaryId(userId, diaryId)) {
			throw new DuplicateLikeException();
		}
	}

	// 파사드 패턴에서 사용할 함수
	public boolean isLiked(Long userId, Long diaryId) {
		return likeRepository.existsByUserIdAndDiaryId(userId, diaryId);
	}
}