package com.example.log4u.domain.map.dto.response;

public record DiaryClusterResponseDto(
	String areaName,
	Long areaId,
	Double lat,
	Double lon,
	Long diaryCount
) {
	public static DiaryClusterResponseDto of(AreaClusterProjection proj) {
		return new DiaryClusterResponseDto(
			proj.getName(),
			proj.getId(),
			proj.getLat(),
			proj.getLon(),
			proj.getDiaryCount()
		);
	}
}
