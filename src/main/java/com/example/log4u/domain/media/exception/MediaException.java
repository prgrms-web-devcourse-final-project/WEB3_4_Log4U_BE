package com.example.log4u.domain.media.exception;

import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

public class MediaException extends ServiceException {
	public MediaException(ErrorCode errorCode) {
		super(errorCode);
	}
}
