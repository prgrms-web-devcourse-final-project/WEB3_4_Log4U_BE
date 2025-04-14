package com.example.log4u.domain.map.dto;

import java.util.List;

public record ReverseGeocodingResponseDto(
	List<Result> results
) {
	public record Result(
		String name,
		Code code,
		Region region,
		Land land
	) {
	}

	public record Code(
		String id,
		String type,
		String mappingId
	) {
	}

	public record Region(
		Area area0,
		Area area1,
		Area area2,
		Area area3,
		Area area4
	) {
	}

	public record Area(
		String name
	) {
	}

	public record Land(
		String type,
		String number1,
		String number2,
		String addition0
	) {
	}
}
