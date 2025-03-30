package com.example.log4u.domain.like.exception;

public class DuplicateLikeException extends LikeException {
	public DuplicateLikeException() {
		super(LikeErrorCode.DUPLICATE_LIKE);
	}
}
