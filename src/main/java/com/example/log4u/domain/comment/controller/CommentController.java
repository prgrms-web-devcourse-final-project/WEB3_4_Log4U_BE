package com.example.log4u.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.log4u.domain.comment.dto.response.CommentCreateResponseDto;
import com.example.log4u.domain.comment.dto.response.CommentResponseDto;
import com.example.log4u.domain.comment.service.CommentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentCreateResponseDto> addComment(@RequestBody @Valid CommentCreateRequestDto requestDto) {
		Long userId = 1L; //임시 처리

		CommentCreateResponseDto response = commentService.addComment(userId, requestDto);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
		Long userId = 1L; //임시 처리

		commentService.deleteComment(userId, commentId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{diaryId}")
	public ResponseEntity<PageResponse<CommentResponseDto>> getCommentListByDiary(
		@PathVariable Long diaryId,
		@RequestParam(required = false) Long cursorCommentId,
		@RequestParam(defaultValue = "5") int size
	) {
		PageResponse<CommentResponseDto> response =
			commentService.getCommentListByDiary(diaryId, cursorCommentId, size);
		return ResponseEntity.ok(response);
	}
}
