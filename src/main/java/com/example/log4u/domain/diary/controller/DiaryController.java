package com.example.log4u.domain.diary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

	@PostMapping
	public ResponseEntity<?> createDiary() {
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<?> getDiaryList() {
		List<Map<String, Object>> diaries = List.of(
			Map.of(
				"diaryId", 149L,
				"userId", 5L,
				"latitude", 37.5665f,
				"longitude", 126.9780f,
				"content", "서울 날씨 좋아요!",
				"weatherInfo", "SUNNY",
				"visibility", "PUBLIC",
				"fileUrls", List.of("https://s3.amazonaws.com/example/image1.jpg"),
				"createdAt", "2025-03-24T12:00:00.000Z"
			),
			Map.of(
				"diaryId", 148L,
				"userId", 3L,
				"latitude", 35.1796f,
				"longitude", 129.0756f,
				"content", "부산은 흐려요.",
				"weatherInfo", "CLOUDY",
				"visibility", "PUBLIC",
				"fileUrls", List.of(),
				"createdAt", "2025-03-24T11:45:00.000Z"
			)
		);

		return ResponseEntity.ok()
			.body(Map.of("diaries", diaries));
	}

	@GetMapping("/{diaryId}")
	public ResponseEntity<?> getDiary() {
		Map<String, Object> diary = Map.of(
			"diaryId", 1L,
			"userId", 1L,
			"latitude", 37.5665f,
			"longitude", 126.9780f,
			"content", "오늘 서울은 흐려요.",
			"weatherInfo", "CLOUDY",
			"visibility", "PUBLIC",
			"fileUrls", List.of(
				"https://s3.amazonaws.com/example/image1.jpg",
				"https://s3.amazonaws.com/example/image2.jpg"
			),
			"createdAt", "2025-03-24T12:00:00.000Z",
			"updatedAt", "2025-03-24T12:30:00.000Z"
		);

		return ResponseEntity.ok().body(diary);
	}

	@GetMapping
	public ResponseEntity<?> getDiaryByAuthor(@RequestParam String author) {
		List<Map<String, Object>> diaries = List.of(
			Map.of(
				"diaryId", 149L,
				"userId", 3L,
				"latitude", 37.5665f,
				"longitude", 126.9780f,
				"content", "서울 날씨 좋아요!",
				"weatherInfo", "SUNNY",
				"visibility", "PUBLIC",
				"fileUrls", List.of("https://s3.amazonaws.com/example/image1.jpg"),
				"createdAt", "2025-03-24T12:00:00.000Z"
			),
			Map.of(
				"diaryId", 148L,
				"userId", 3L,
				"latitude", 35.1796f,
				"longitude", 129.0756f,
				"content", "부산은 흐려요.",
				"weatherInfo", "CLOUDY",
				"visibility", "PUBLIC",
				"fileUrls", List.of(),
				"createdAt", "2025-03-24T11:45:00.000Z"
			)
		);

		return ResponseEntity.ok()
			.body(Map.of("diaries", diaries));
	}

	@PatchMapping
	public ResponseEntity<?> modifyDiary() {
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteDiary() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}