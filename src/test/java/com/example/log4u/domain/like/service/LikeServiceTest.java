package com.example.log4u.domain.like.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.like.dto.request.LikeAddRequestDto;
import com.example.log4u.domain.like.dto.response.LikeAddResponseDto;
import com.example.log4u.domain.like.entity.Like;
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
	@DisplayName("성공 테스트: 사용자가 게시물에 좋아요를 누르면 좋아요가 저장된다")
	void likeSuccess() {
		// given
		User user = UserFixture.createUserFixture();
		Diary diary = DiaryFixture.createDiaryFixture();
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diary.getDiaryId());

		Like like = LikeFixture.createLikeFixture(123243L, user.getUserId(), diary.getDiaryId());
		Long updatedLikeCount = 11L;

		given(likeRepository.existsByUserIdAndDiaryId(user.getUserId(), diary.getDiaryId())).willReturn(false);
		given(likeRepository.save(any(Like.class))).willReturn(like);
		given(diaryService.incrementLikeCount(diary.getDiaryId())).willReturn(updatedLikeCount);

		// when
		LikeAddResponseDto response = likeService.addLike(user.getUserId(), requestDto);

		// then
		verify(likeRepository).save(any(Like.class));
		assertThat(response.liked()).isTrue();
		assertThat(response.likeCount()).isEqualTo(updatedLikeCount);
	}

	@Test
	@DisplayName("예외 테스트: 존재하지 않는 다이어리에 좋아요 요청 시 예외가 발생한다")
	void likeFail_whenDiaryNotFound() {
		// given
		Long userId = 1L;
		Long diaryId = 100L;
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diaryId);

		given(likeRepository.existsByUserIdAndDiaryId(userId, diaryId)).willReturn(false);
		given(diaryService.incrementLikeCount(diaryId)).willThrow(new IllegalArgumentException());

		// when & then
		assertThrows(IllegalArgumentException.class, () -> {
			likeService.addLike(userId, requestDto);
		});

		verify(likeRepository).save(any(Like.class));
	}

	@Test
	@DisplayName("예외 테스트: 사용자가 이미 좋아요를 누른 다이어리에 또 요청하면 예외가 발생한다")
	void likeFail_whenAlreadyLiked() {
		// given
		Long userId = 1L;
		Long diaryId = 100L;
		LikeAddRequestDto requestDto = new LikeAddRequestDto(diaryId);

		given(likeRepository.existsByUserIdAndDiaryId(userId, diaryId)).willReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> {
			likeService.addLike(userId, requestDto);
		});

		verify(likeRepository, never()).save(any(Like.class));
	}
}
