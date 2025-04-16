package com.example.log4u.fixture;

import java.time.LocalDateTime;
import java.util.List;

import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;

public class DiaryMarkerFixture {

	public static DiaryMarkerResponseDto createDiaryMarker(Long id, String title, String thumbnailUrl, Long likeCount, Double lat, Double lon, LocalDateTime createdAt) {
		return new DiaryMarkerResponseDto(id, title, thumbnailUrl, likeCount, lat, lon, createdAt);
	}

	public static List<DiaryMarkerResponseDto> createDiaryMarkers() {
		return List.of(
			createDiaryMarker(1L, "첫번째 다이어리", "https://example.com/thumb1.jpg", 12L, 37.5665, 126.9780, LocalDateTime.now().minusDays(1)),
			createDiaryMarker(2L, "두번째 다이어리", "https://example.com/thumb2.jpg", 7L, 37.5678, 126.9900, LocalDateTime.now().minusDays(2))
		);
	}
}
