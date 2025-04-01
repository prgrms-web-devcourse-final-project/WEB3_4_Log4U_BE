package com.example.log4u.domain.media.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.dto.PresignedUrlRequestDto;
import com.example.log4u.domain.media.dto.PresignedUrlResponseDto;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresignedUrlService {

	private final S3Presigner s3Presigner;
	private final MediaRepository mediaRepository;

	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	@Transactional
	public PresignedUrlResponseDto generatePresignedUrl(PresignedUrlRequestDto request) {
		// 파일명 생성
		String originalFilename = request.filename();
		String fileExtension = getFileExtension(originalFilename);
		String storedFilename = UUID.randomUUID() + fileExtension;

		// S3 접근 URL 생성
		String accessUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, storedFilename);

		// Presigned URL 생성 (5분 유효)
		Duration signatureDuration = Duration.ofMinutes(5);

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(storedFilename)
			.contentType(request.contentType())
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner
			.presignPutObject(builder -> builder
				.signatureDuration(signatureDuration)
				.putObjectRequest(objectRequest)
				.build());

		String presignedUrl = presignedRequest.url().toString();

		// 임시 미디어 엔티티 저장
		Media media = Media.builder()
			.originalName(originalFilename)
			.storedName(storedFilename)
			.url(accessUrl)
			.contentType(request.contentType())
			.size(request.size())
			.status(MediaStatus.TEMPORARY)
			.build();

		Media savedMedia = mediaRepository.save(media);

		return new PresignedUrlResponseDto(
			savedMedia.getId(),
			presignedUrl,
			accessUrl
		);
	}

	private String getFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
	}
}
