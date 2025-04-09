package com.example.log4u.domain.media.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaErrorCode implements ErrorCode {
	NOT_FOUND_MEDIA(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."),
	MEDIA_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "미디어는 최대 10개까지만 업로드 가능합니다.");

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
