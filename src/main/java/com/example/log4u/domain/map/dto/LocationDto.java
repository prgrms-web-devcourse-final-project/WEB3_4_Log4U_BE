package com.example.log4u.domain.map.dto;

import com.example.log4u.domain.map.entitiy.Location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LocationDto(

	@NotNull(message = "위도는 필수입니다.")
	Double latitude,

	@NotNull(message = "경도는 필수입니다.")
	Double longitude,

	@NotBlank(message = "시/도 정보는 필수입니다.")
	String sido,

	@NotBlank(message = "시/군/구 정보는 필수입니다.")
	String sigungu,

	@NotBlank(message = "읍/면/동 정보는 필수입니다.")
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
