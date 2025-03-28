package com.example.log4u.domain.comment.dto.response;

import com.example.log4u.domain.comment.entity.Comment;

public record CommentCreateResponseDto(
	Long commentId,
	String content
) {
	public static CommentCreateResponseDto of(Comment comment) {
		return new CommentCreateResponseDto(
			comment.getCommentId(),
			comment.getContent());
	}
}
