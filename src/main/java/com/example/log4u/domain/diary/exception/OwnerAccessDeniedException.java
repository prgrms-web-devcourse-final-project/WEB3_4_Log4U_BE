package com.example.log4u.domain.diary.exception;

public class OwnerAccessDeniedException extends DiaryException {
	public OwnerAccessDeniedException() {
		super(DiaryErrorCode.OWNER_ACCESS_DENIED);
	}
}
