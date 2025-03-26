package com.example.log4u.domain.like.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements ErrorCode {

	NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, "좋아요 정보를 찾을 수 없습니다."),
	DUPLICATE_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다.");


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

