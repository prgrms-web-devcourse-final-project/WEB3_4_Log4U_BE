package com.example.log4u.domain.media.exception;

public class MediaLimitExceededException extends MediaException {
	public MediaLimitExceededException() {
		super(MediaErrorCode.MEDIA_LIMIT_EXCEEDED);
	}
}
