package com.example.log4u.domain.comment.exception;

public class NotFoundCommentException extends CommentException {
	public NotFoundCommentException() {
		super(CommentErrorCode.NOT_FOUND_COMMENT);
	}
}
