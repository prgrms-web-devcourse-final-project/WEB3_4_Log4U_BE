package com.example.log4u.domain.media.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.log4u.domain.media.dto.MediaResponseDto;
import com.example.log4u.domain.media.service.MediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

	private final MediaService mediaService;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") List<MultipartFile> files) {
		List<MediaResponseDto> responses = List.of(
			new MediaResponseDto(1L, "https://s3.amazonaws.com/example/image1.jpg", "jpeg"),
			new MediaResponseDto(2L, "https://s3.amazonaws.com/example/image2.jpg", "jpeg")
		);

		return ResponseEntity.ok().body(responses);
	}

	@DeleteMapping("/{mediaId}")
	public ResponseEntity<?> delete(@PathVariable("mediaId") String mediaId) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PutMapping
	public ResponseEntity<?> update(
		@RequestParam("mediaIds") List<Long> mediaIds,
		@RequestParam("files") List<MultipartFile> files
	) {
		List<MediaResponseDto> responses = List.of(
			new MediaResponseDto(1L, "https://s3.amazonaws.com/example/image1.jpg", "jpeg"),
			new MediaResponseDto(2L, "https://s3.amazonaws.com/example/image2.jpg", "jpeg")
		);

		return ResponseEntity.ok().body(responses);
	}

}
