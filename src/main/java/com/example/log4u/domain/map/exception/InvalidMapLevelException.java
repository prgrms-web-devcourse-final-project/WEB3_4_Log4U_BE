package com.example.log4u.domain.map.exception;

public class InvalidMapLevelException extends MapException {
	public InvalidMapLevelException() {
		super(MapErrorCode.INVALID_MAP_LEVEL);
	}
}
