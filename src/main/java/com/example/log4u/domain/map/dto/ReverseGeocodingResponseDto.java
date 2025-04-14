package com.example.log4u.domain.map.dto;

import java.util.List;

public record ReverseGeocodingResponseDto(
	Status status,
	List<Result> results
) {
	// 상태 정보 DTO
	public record Status(
		int code,
		String name,
		String message
	) {
	}

	// 결과 항목 DTO
	public record Result(
		String name,
		Code code,
		Region region,
		Land land  // land는 addr 결과에만 존재
	) {
	}

	// 코드 정보 DTO
	public record Code(
		String id,
		String type,
		String mappingId
	) {
	}

	// 지역 정보 DTO
	public record Region(
		Area area0,
		Area area1,
		Area area2,
		Area area3,
		Area area4
	) {
	}

	// 지역 세부 정보 DTO
	public record Area(
		String name,
		Coords coords,
		String alias  // area1에만 존재
	) {
	}

	// 좌표 정보 DTO
	public record Coords(
		Center center
	) {
	}

	// 중심 좌표 DTO
	public record Center(
		String crs,
		double x,
		double y
	) {
	}

	// 토지 정보 DTO (addr 결과에만 존재)
	public record Land(
		String type,
		String number1,
		String number2,
		Addition addition0,
		Addition addition1,
		Addition addition2,
		Addition addition3,
		Addition addition4,
		Coords coords
	) {
	}

	// 추가 정보 DTO
	public record Addition(
		String type,
		String value
	) {
	}
}