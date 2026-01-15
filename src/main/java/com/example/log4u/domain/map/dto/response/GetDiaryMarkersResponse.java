package com.example.log4u.domain.map.dto.response;

import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetDiaryMarkersResponse(

	@Schema(description = "다이어리 마커 목록")
	List<GetDiaryMarkerResponse> markers
) {

	public static GetDiaryMarkersResponse of(List<Diary> diaries) {
		return new GetDiaryMarkersResponse(
			diaries.stream()
				.map(GetDiaryMarkerResponse::of)
				.toList()
		);
	}

	public static GetDiaryMarkersResponse ofMarkers(List<GetDiaryMarkerResponse> markers) {
		return new GetDiaryMarkersResponse(markers);
	}
}
