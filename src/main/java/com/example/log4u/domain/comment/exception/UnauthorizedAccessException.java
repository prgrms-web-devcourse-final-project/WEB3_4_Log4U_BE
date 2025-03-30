package com.example.log4u.domain.comment.exception;

public class UnauthorizedAccessException extends CommentException {
	public UnauthorizedAccessException() {
		super(CommentErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
	}
}
