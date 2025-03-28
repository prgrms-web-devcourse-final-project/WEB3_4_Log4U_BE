package com.example.log4u.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.log4u.domain.comment.dto.response.CommentCreateResponseDto;
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

	public CommentCreateResponseDto addComment(Long userId, CommentCreateRequestDto requestDto) {
		checkDiaryExists(requestDto);
		Comment comment = requestDto.toEntity(userId);
		commentRepository.save(comment);
		return CommentCreateResponseDto.of(comment);
	}

	private void checkDiaryExists(CommentCreateRequestDto requestDto) {
		diaryService.checkDiaryExists(requestDto.diaryId());
	}
}
