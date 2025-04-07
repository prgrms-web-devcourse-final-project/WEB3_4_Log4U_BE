package com.example.log4u.domain.map.dto;

import com.example.log4u.domain.map.entitiy.Location;

import lombok.Builder;

@Builder
public record LocationDto(
	Double latitude,
	Double longitude,
	String sido,
	String sigungu,
	String eupmyeondong
) {
	public static Location toEntity(LocationDto locationDto) {
		return Location.builder()
			.latitude(locationDto.latitude)
			.longitude(locationDto.longitude)
			.sido(locationDto.sido)
			.sigungu(locationDto.sigungu)
			.eupmyeondong(locationDto.eupmyeondong)
			.build();
	}

	public static LocationDto of(Location location) {
		return LocationDto.builder()
			.latitude(location.getLatitude())
			.longitude(location.getLongitude())
			.sido(location.getSido())
			.sigungu(location.getSigungu())
			.eupmyeondong(location.getEupmyeondong())
			.build();
	}
}
