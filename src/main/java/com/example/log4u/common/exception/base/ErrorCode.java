package com.example.log4u.common.exception.base;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String name();

	HttpStatus getHttpStatus();

	String getErrorMessage();
}
