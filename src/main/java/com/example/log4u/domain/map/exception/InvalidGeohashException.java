package com.example.log4u.domain.map.exception;

public class InvalidGeohashException extends MapException {
	public InvalidGeohashException() {
		super(MapErrorCode.INVALID_GEOHASH);
	}
}
