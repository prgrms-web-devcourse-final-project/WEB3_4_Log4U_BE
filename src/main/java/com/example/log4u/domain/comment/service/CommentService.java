package com.example.log4u.domain.comment.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.log4u.domain.comment.dto.response.CommentCreateResponseDto;
import com.example.log4u.domain.comment.dto.response.CommentResponseDto;
import com.example.log4u.domain.comment.entity.Comment;
import com.example.log4u.domain.comment.exception.NotFoundCommentException;
import com.example.log4u.domain.comment.exception.UnauthorizedAccessException;
import com.example.log4u.domain.comment.repository.CommentRepository;
import com.example.log4u.domain.diary.service.DiaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final DiaryService diaryService;

	@Transactional
	public CommentCreateResponseDto addComment(Long userId, CommentCreateRequestDto requestDto) {
		checkDiaryExists(requestDto.diaryId());
		Comment comment = requestDto.toEntity(userId);
		commentRepository.save(comment);
		return CommentCreateResponseDto.of(comment);
	}

	@Transactional
	public void deleteComment(Long userId, Long commentId) {
		Comment comment = getComment(commentId);
		validateCommentOwner(userId, comment);
		commentRepository.delete(comment);
	}

	private void checkDiaryExists(Long diaryId) {
		diaryService.checkDiaryExists(diaryId);
	}

	private void validateCommentOwner(Long userId, Comment comment) {
		if (!comment.getUserId().equals(userId)) {
			throw new UnauthorizedAccessException();
		}
	}

	private Comment getComment(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(NotFoundCommentException::new);
	}

	@Transactional(readOnly = true)
	public PageResponse<CommentResponseDto> getCommentListByDiary(Long diaryId, Long cursorCommentId, int size) {
		checkDiaryExists(diaryId);
		Pageable pageable = PageRequest.of(0, size);
		Slice<Comment> slice = commentRepository.findByDiaryIdWithCursor(diaryId, cursorCommentId, pageable);

		List<CommentResponseDto> dtoList = slice.getContent().stream()
			.map(CommentResponseDto::of)
			.toList();

		Long nextCursor = slice.hasNext() ? dtoList.getLast().commentId() : null;
		return PageResponse.of(new SliceImpl<>(dtoList, pageable, slice.hasNext()), nextCursor);
	}
}
