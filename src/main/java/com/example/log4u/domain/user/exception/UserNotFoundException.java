package com.example.log4u.domain.user.exception;

import com.example.log4u.common.exception.base.ServiceException;

public class UserNotFoundException extends ServiceException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
