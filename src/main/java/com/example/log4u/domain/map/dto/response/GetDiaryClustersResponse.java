package com.example.log4u.domain.map.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetDiaryClustersResponse(

	@Schema(description = "다이어리 클러스터 목록")
	List<GetDiaryClusterResponse> clusters
) {

	public static GetDiaryClustersResponse of(List<GetDiaryClusterResponse> clusters) {
		return new GetDiaryClustersResponse(clusters);
	}
}
