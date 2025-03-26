package com.example.log4u.domain.like.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.like.dto.request.LikeAddRequestDto;
import com.example.log4u.domain.like.dto.response.LikeAddResponseDto;
import com.example.log4u.domain.like.dto.response.LikeCancelResponseDto;
import com.example.log4u.domain.like.entity.Like;
import com.example.log4u.domain.like.exception.DuplicateLikeException;
import com.example.log4u.domain.like.repository.LikeRepository;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.LikeFixture;
import com.example.log4u.fixture.UserFixture;

@DisplayName("좋아요 API 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

	@InjectMocks
	private LikeService likeService;

	@Mock
	private LikeRepository likeRepository;

	@Mock
	private DiaryService diaryService;

	@Test
	@DisplayName("성공 테스트: 좋아요 추가 ")
	void likeSuccess() {
		// given
		Long userId = 1L;
		Long diaryId = 123L;
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diaryId);

		Like like = LikeFixture.createLikeFixture(123243L, userId, diaryId);
		Long updatedLikeCount = 11L;

		given(likeRepository.existsByUserIdAndDiaryId(userId, diaryId)).willReturn(false);
		given(likeRepository.save(any(Like.class))).willReturn(like);
		given(diaryService.incrementLikeCount(diaryId)).willReturn(updatedLikeCount);

		// when
		LikeAddResponseDto response = likeService.addLike(userId, requestDto);

		// then
		verify(likeRepository).save(any(Like.class));
		assertThat(response.liked()).isTrue();
		assertThat(response.likeCount()).isEqualTo(updatedLikeCount);
	}

	@Test
	@DisplayName("예외 테스트: 좋아요 추가 - 존재하지 않는 다이어리에 좋아요 요청")
	void likeFail_whenDiaryNotFound() {
		// given
		Long userId = 1L;
		Long diaryId = 100L;
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diaryId);

		given(likeRepository.existsByUserIdAndDiaryId(userId, diaryId)).willReturn(false);
		given(diaryService.incrementLikeCount(diaryId)).willThrow(new NotFoundDiaryException());

		// when & then
		assertThrows(NotFoundDiaryException.class, () -> {
			likeService.addLike(userId, requestDto);
		});

		verify(likeRepository).save(any(Like.class));
	}

	@Test
	@DisplayName("예외 테스트: 좋아요 추가 - 이미 누른 좋아요 또 요청")
	void likeFail_whenAlreadyLiked() {
		// given
		Long userId = 1L;
		Long diaryId = 100L;
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diaryId);

		given(likeRepository.existsByUserIdAndDiaryId(userId, diaryId)).willReturn(true);

		// when & then
		assertThrows(DuplicateLikeException.class, () -> {
			likeService.addLike(userId, requestDto);
		});

		verify(likeRepository, never()).save(any(Like.class));
	}
}
