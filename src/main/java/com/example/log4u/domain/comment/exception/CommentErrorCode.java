package com.example.log4u.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

	NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
	UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "댓글에 대한 삭제 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getErrorMessage() {
		return message;
	}
}
