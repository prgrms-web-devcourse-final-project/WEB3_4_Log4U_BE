package com.example.log4u.common.exception;

import org.springframework.http.HttpStatus;

import com.example.log4u.common.exception.base.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED,"로그인이 필요한 기능입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청 정보를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다. 관리자에게 문의하세요.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.message;
    }
}
