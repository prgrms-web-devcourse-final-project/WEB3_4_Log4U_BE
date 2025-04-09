package com.example.log4u.domain.map.exception;

import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

public class MapException extends ServiceException {
	public MapException(ErrorCode errorCode) {
		super(errorCode);
	}
}
