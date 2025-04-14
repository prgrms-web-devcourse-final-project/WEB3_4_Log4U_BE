package com.example.log4u.domain.map.dto;

import java.util.List;

public record ReverseGeocodingResponseDto(
	List<RegionInfo> results
) {
	public record RegionInfo(
		Area area0,
		Area area1,
		Area area2,
		Area area3,
		Area area4
	) {
	}

	public record Area(
		String name,
		Coords coords
	) {
	}

	public record Coords(
		Center center
	) {
	}

	public record Center(
		double x,
		double y
	) {
	}
}

