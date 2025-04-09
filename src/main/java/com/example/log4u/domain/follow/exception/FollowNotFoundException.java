package com.example.log4u.domain.follow.exception;

import com.example.log4u.common.exception.base.ServiceException;

public class FollowNotFoundException extends ServiceException {
	public FollowNotFoundException() {
		super(FollowErrorCode.FOLLOW_NOT_FOUND);
	}
}
