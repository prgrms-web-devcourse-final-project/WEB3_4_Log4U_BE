package com.example.log4u.domain.comment.dto.response;

import com.example.log4u.domain.comment.entity.Comment;

public record CommentResponseDto(
	Long commentId,
	String content
) {
	public static CommentResponseDto of(Comment comment) {
		return new CommentResponseDto(
			comment.getCommentId(),
			comment.getContent()
		);
	}
}
