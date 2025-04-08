package com.example.log4u.domain.media.exception;

public class NotFoundMediaException extends MediaException {
	public NotFoundMediaException() {
		super(MediaErrorCode.NOT_FOUND_MEDIA);
	}
}
