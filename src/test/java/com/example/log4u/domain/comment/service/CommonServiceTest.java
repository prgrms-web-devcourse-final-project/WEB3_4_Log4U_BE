package com.example.log4u.domain.comment.service;

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

import com.example.log4u.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.log4u.domain.comment.entity.Comment;
import com.example.log4u.domain.comment.exception.NotFoundCommentException;
import com.example.log4u.domain.comment.exception.UnauthorizedAccessException;
import com.example.log4u.domain.comment.repository.CommentRepository;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.comment.dto.response.CommentCreateResponseDto;
import com.example.log4u.fixture.CommentFixture;

@DisplayName("댓글 API 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CommonServiceTest {

	@InjectMocks
	private CommentService commentService;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private DiaryService diaryService;

	@DisplayName("성공 테스트: 댓글 생성")
	@Test
	void commentCreate_Success() {
		// given
		Long userId = 1L;
		Long diaryId = 12L;

		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(diaryId, "댓글 내용");
		Comment expectedComment = CommentFixture.createCommentFixture(100L, userId, diaryId);

		doNothing().when(diaryService).checkDiaryExists(diaryId);
		given(commentRepository.save(any(Comment.class))).willReturn(expectedComment);

		// when
		CommentCreateResponseDto response = commentService.addComment(userId, requestDto);

		// then
		assertThat(response.content()).isEqualTo(expectedComment.getContent());

		verify(commentRepository).save(any(Comment.class));
	}

	@DisplayName("예외 테스트: 댓글 생성 - 존재하지 않는 다이어리에 댓글 생성 시 예외")
	@Test
	void commentCreate_Fail_DiaryNotFound() {
		// given
		Long userId = 1L;
		Long diaryId = 999L;
		CommentCreateRequestDto requestDto = new CommentCreateRequestDto(diaryId, "댓글 내용");

		doThrow(new NotFoundDiaryException())
			.when(diaryService).checkDiaryExists(diaryId);

		// when & then
		assertThrows(NotFoundDiaryException.class, () -> {
			commentService.addComment(userId, requestDto);
		});

		verify(commentRepository, never()).save(any());
	}
}
