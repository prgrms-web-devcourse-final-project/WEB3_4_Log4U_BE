package com.example.log4u.domain.comment.dto.response;

import java.time.LocalDateTime;

import com.example.log4u.domain.comment.entity.Comment;
import com.example.log4u.domain.user.entity.User;

public record CommentResponseDto(
	Long commentId,
	Long userId,
	String userName,
	String userProfileImage,
	String content,
	LocalDateTime createdAt
) {
	public static CommentResponseDto of(Comment comment, User user) {
		return new CommentResponseDto(
			comment.getCommentId(),
			user.getUserId(),
			user.getName(),
			user.getProfileImage(),
			comment.getContent(),
			comment.getCreatedAt()
		);
	}
}

