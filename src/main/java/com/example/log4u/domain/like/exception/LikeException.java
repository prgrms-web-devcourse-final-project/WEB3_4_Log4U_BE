package com.example.log4u.domain.like.exception;

import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

public class LikeException extends ServiceException {
	public LikeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
