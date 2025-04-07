package com.example.log4u.domain.map.dto.response;

import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.entity.SiggAreas;
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

	public static DiaryClusterResponseDto of(SidoAreas region, Long diaryCount) {
		return new DiaryClusterResponseDto(
			region.getName(),
			region.getId(),
			region.getLat(),
			region.getLon(),
			diaryCount
		);
	}

	public static DiaryClusterResponseDto of(SiggAreas region, Long diaryCount) {
		return new DiaryClusterResponseDto(
			region.getSggName(),
			region.getGid(),
			region.getLat(),
			region.getLon(),
			diaryCount
		);
	}
}
