package com.example.log4u.domain.follow.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FollowErrorCode implements ErrorCode {
	FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 정보가 존재하지 않습니다."),
	FOLLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로워 정보가 존재하지 않습니다.");

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

