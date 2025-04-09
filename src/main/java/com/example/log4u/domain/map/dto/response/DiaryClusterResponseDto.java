package com.example.log4u.domain.map.dto.response;

import com.example.log4u.domain.map.entity.Areas;
import com.querydsl.core.annotations.QueryProjection;

public record DiaryClusterResponseDto(
	String areaName,
	Long areaId,
	Double lat,
	Double lon,
	Long diaryCount
) {

	@QueryProjection
	public DiaryClusterResponseDto {
	}

	public static <T extends Areas> DiaryClusterResponseDto of(T region, Long diaryCount) {
		return new DiaryClusterResponseDto(
			region.getName(),
			region.getId(),
			region.getLat(),
			region.getLon(),
			diaryCount
		);
	}
}
