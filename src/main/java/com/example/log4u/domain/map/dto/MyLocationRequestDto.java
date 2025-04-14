package com.example.log4u.domain.map.dto;

public record MyLocationRequestDto(
	String coords,
	String output
) {

	public MyLocationRequestDto {
		if (output == null || output.isBlank()) {
			output = "json";
		}
	}

	public double parseLongitude() {
		return Double.parseDouble(coords.split(",")[0]);
	}

	public double parseLatitude() {
		return Double.parseDouble(coords.split(",")[1]);
	}
}
