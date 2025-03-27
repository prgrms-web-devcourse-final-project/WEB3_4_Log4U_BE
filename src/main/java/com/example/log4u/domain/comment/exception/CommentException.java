package com.example.log4u.domain.comment.exception;

import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

public class CommentException extends ServiceException {
	public CommentException(ErrorCode errorCode) {
		super(errorCode);
	}
}
