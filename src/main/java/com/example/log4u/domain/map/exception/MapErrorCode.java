package com.example.log4u.domain.map.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapErrorCode implements ErrorCode {

	NOT_FOUND_REGION(HttpStatus.NOT_FOUND, "해당 지역(시/군/구)을 찾을 수 없습니다."),
	UNAUTHORIZED_MAP_ACCESS(HttpStatus.FORBIDDEN, "지도 리소스에 대한 권한이 없습니다."),
	INVALID_MAP_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않은 지도 level 값입니다."),
	INVALID_GEOHASH(HttpStatus.BAD_REQUEST,"geohash 길이가 유효하지 않습니다");

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
