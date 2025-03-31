package com.example.log4u.domain.diary.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {

	NOT_FOUND_DIARY(HttpStatus.NOT_FOUND, "다이어리를 찾을 수 없습니다."),
	OWNER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "작성자만 수정/삭제할 수 있습니다.");

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

