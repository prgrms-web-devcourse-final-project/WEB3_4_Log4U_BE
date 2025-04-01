package com.example.log4u.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.log4u.domain.comment.dto.response.CommentResponseDto;
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

	@DisplayName("성공 테스트: 댓글 삭제")
	@Test
	void commentDelete_Success() {
		// given
		Long userId = 1L;
		Long commentId = 100L;
		Long diaryId = 12L;

		Comment comment = CommentFixture.createCommentFixture(commentId, userId, diaryId);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when
		commentService.deleteComment(userId, commentId);

		// then
		verify(commentRepository).delete(comment);
	}

	@DisplayName("예외 테스트: 댓글 삭제 - 존재하지 않는 댓글")
	@Test
	void commentDelete_Fail_NotFound() {
		// given
		Long userId = 1L;
		Long commentId = 999L;

		given(commentRepository.findById(commentId)).willReturn(Optional.empty());

		// when & then
		assertThrows(NotFoundCommentException.class, () -> {
			commentService.deleteComment(userId, commentId);
		});

		verify(commentRepository, never()).delete(any());
	}

	@DisplayName("예외 테스트: 댓글 삭제 - 본인 댓글이 아닌 사용자의 삭제 요청")
	@Test
	void commentDelete_Fail_Unauthorized() {
		// given
		Long commentId = 100L;
		Long commentOwnerId = 1L;
		Long otherUserId = 2L;
		Long diaryId = 12L;

		Comment comment = CommentFixture.createCommentFixture(commentId, commentOwnerId, diaryId);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when & then
		assertThrows(UnauthorizedAccessException.class, () -> {
			commentService.deleteComment(otherUserId, commentId);
		});

		verify(commentRepository, never()).delete(any());
	}

	@DisplayName("성공 테스트: 특정 다이어리 댓글 전체 조회 (커서 기반)")
	@Test
	void getCommentsList_In_DiaryDetail_Success() {
		// given
		Long diaryId = 1L;
		int size = 5;
		Long cursorCommentId = null;

		List<Comment> commentList = CommentFixture.createCommentsListFixture(size + 1); // hasNext 판별 위해 +1
		Pageable pageable = PageRequest.of(0, size);
		boolean hasNext = commentList.size() > size;

		List<Comment> sliced = hasNext ? commentList.subList(0, size) : commentList;

		Slice<Comment> slice = new SliceImpl<>(sliced, pageable, hasNext);
		given(commentRepository.findByDiaryIdWithCursor(diaryId, cursorCommentId, pageable))
			.willReturn(slice);

		// when
		PageResponse<CommentResponseDto> response = commentService.getCommentListByDiary(diaryId, cursorCommentId, size);

		// then
		assertThat(response.content()).hasSize(sliced.size());
		assertThat(response.pageInfo().hasNext()).isEqualTo(hasNext);
		assertThat(response.pageInfo().nextCursor()).isEqualTo(hasNext ? sliced.getLast().getCommentId() : null);

		verify(commentRepository).findByDiaryIdWithCursor(diaryId, cursorCommentId, pageable);
	}


}
