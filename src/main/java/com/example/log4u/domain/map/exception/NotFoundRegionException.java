package com.example.log4u.domain.map.exception;

public class NotFoundRegionException extends MapException {
	public NotFoundRegionException() {
		super(MapErrorCode.NOT_FOUND_REGION);
	}
}
