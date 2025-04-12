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

	@Value("${AWS_REGION}")
	private String s3Region;

	@Transactional
	public PresignedUrlResponseDto generatePresignedUrl(PresignedUrlRequestDto request) {
		// 파일명 생성
		String originalFilename = request.filename();
		String fileExtension = getFileExtension(originalFilename);

		//String storedFilename = "images/" + UUID.randomUUID() + fileExtension;
		String storedFilename = UUID.randomUUID() + fileExtension;

		// S3 접근 URL 생성
		String accessUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, s3Region, storedFilename);

		// Presigned URL 생성 (5분 유효)
		Duration signatureDuration = Duration.ofMinutes(5);

		String contentType = request.contentType() != null ? request.contentType() : "application/octet-stream";

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(storedFilename)
			.contentType(contentType)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner
			.presignPutObject(builder -> builder
				.signatureDuration(signatureDuration)
				.putObjectRequest(objectRequest)
				.build());

		String presignedUrl = presignedRequest.url().toString();
		log.info("Generated presigned URL: {}", presignedUrl);

		// 임시 미디어 엔티티 저장
		Media media = Media.builder()
			.originalName(originalFilename)
			.storedName(storedFilename)
			.url(accessUrl)
			.contentType(contentType)
			.size(request.size())
			.status(MediaStatus.TEMPORARY)
			.orderIndex(0)
			.build();

		Media savedMedia = mediaRepository.save(media);

		return new PresignedUrlResponseDto(
			savedMedia.getMediaId(),
			presignedUrl,
			accessUrl
		);
	}

	private String getFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
	}
}
