package com.example.log4u.domain.diary.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.media.dto.MediaResponseDto;
import com.example.log4u.domain.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

	private final DiaryService diaryService;

	@PostMapping
	public ResponseEntity<Void> createDiary(
		@Valid @RequestBody DiaryRequestDto request
	) {
		User user = mockUser();
		diaryService.saveDiary(user.getId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<?> getDiaryList(
		@RequestParam(required = false) String author,
		@RequestParam(defaultValue = "PUBLIC") String visibility,
		@RequestParam(defaultValue = "LATEST") String sort
	) {
		List<DiaryResponseDto> diaries = List.of(
			new DiaryResponseDto(
				149L,
				5L,
				37.5665,
				126.9780,
				"서울 날씨",
				"서울 날씨 좋아요!",
				"SUNNY",
				"PUBLIC",
				LocalDateTime.now(),
				LocalDateTime.now(),
				"https://s3.amazonaws.com/example/thumb1.jpg",
				List.of(new MediaResponseDto(1L, "https://s3.amazonaws.com/example/image1.jpg", "jpeg"))
			),
			new DiaryResponseDto(
				148L,
				3L,
				35.1796,
				129.0756,
				"부산 날씨",
				"부산은 흐려요.",
				"CLOUDY",
				"PUBLIC",
				LocalDateTime.now().minusHours(1),
				LocalDateTime.now().minusHours(1),
				"https://s3.amazonaws.com/example/thumb2.jpg",
				List.of(new MediaResponseDto(2L, "https://s3.amazonaws.com/example/image2.jpg", "jpeg"))
			)
		);

		return ResponseEntity.ok(diaries);
	}

	@GetMapping("/{diaryId}")
	public ResponseEntity<?> getDiary(
		@PathVariable Long diaryId
	) {
		DiaryResponseDto diary = new DiaryResponseDto(
			diaryId,
			1L,
			37.5665,
			126.9780,
			"오늘의 일기",
			"오늘 서울은 흐려요.",
			"CLOUDY",
			"PUBLIC",
			LocalDateTime.now(),
			LocalDateTime.now(),
			"https://s3.amazonaws.com/example/thumb.jpg",
			List.of(
				new MediaResponseDto(1L, "https://s3.amazonaws.com/example/image1.jpg", "jpeg"),
				new MediaResponseDto(2L, "https://s3.amazonaws.com/example/image2.jpg", "jpeg")
			)
		);

		return ResponseEntity.ok(diary);
	}

	@PatchMapping("/{diaryId}")
	public ResponseEntity<?> modifyDiary(
		@PathVariable Long diaryId,
		@RequestBody DiaryRequestDto request
	) {
		DiaryResponseDto updated = new DiaryResponseDto(
			diaryId,
			5L,
			request.latitude(),
			request.longitude(),
			request.title(),
			request.content(),
			request.weatherInfo(),
			request.visibility(),
			LocalDateTime.now().minusHours(1),
			LocalDateTime.now(),
			"https://s3.amazonaws.com/example/thumb.jpg",
			List.of(
				new MediaResponseDto(1L, "https://s3.amazonaws.com/example/image1.jpg", "jpeg"),
				new MediaResponseDto(2L, "https://s3.amazonaws.com/example/image2.jpg", "jpeg")
			)
		);

		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{diaryId}")
	public ResponseEntity<?> deleteDiary(
		@PathVariable Long diaryId
	) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private User mockUser() {
		return User.builder()
			.id(1L)
			.nickname("목유저")
			.providerId(12345L)
			.provider("MOCK")
			.email("mock@mock.com")
			.status_message("목유저입니다.")
			.build();
	}
}