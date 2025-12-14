package com.example.log4u.domain.map.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record GetDiaryClusterResponse(
	String areaName,
	Long areaId,
	Double lat,
	Double lon,
	Long diaryCount
) {

	@QueryProjection
	public GetDiaryClusterResponse {
	}
}
