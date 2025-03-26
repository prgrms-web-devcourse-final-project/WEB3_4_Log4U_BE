package com.example.log4u.domain.diary.exception;

public class NotFoundDiaryException extends DiaryException {
	public NotFoundDiaryException() {
		super(DiaryErrorCode.NOT_FOUND_DIARY);
	}
}
