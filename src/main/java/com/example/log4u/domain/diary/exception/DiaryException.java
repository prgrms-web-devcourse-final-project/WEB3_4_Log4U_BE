package com.example.log4u.domain.diary.exception;

import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

public class DiaryException extends ServiceException {
	public DiaryException(ErrorCode errorCode) {
		super(errorCode);
	}
}

