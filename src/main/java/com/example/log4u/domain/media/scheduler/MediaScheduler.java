package com.example.log4u.domain.media.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaScheduler {

	private final MediaRepository mediaRepository;
	private final S3Client s3Client;

	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	// 임시 미디어 정리 (24시간 이상 지난 것)
	@Scheduled(cron = "0 0 * * * *") // 매시간 실행
	@Transactional
	public void cleanupTemporaryMedia() {
		LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
		List<Media> temporaryMedia = mediaRepository.findByStatusAndCreatedAtBefore(
			MediaStatus.TEMPORARY,
			cutoffTime
		);

		cleanupMedia(temporaryMedia);
	}

	// DELETED 상태 미디어 정리
	@Scheduled(cron = "0 30 * * * *") // 매시간 30분에 실행
	@Transactional
	public void cleanupDeletedMedia() {
		List<Media> deletedMedia = mediaRepository.findByStatus(MediaStatus.DELETED);
		cleanupMedia(deletedMedia);
	}

	private void cleanupMedia(List<Media> mediaList) {
		for (Media media : mediaList) {
			try {
				// S3에서 파일 삭제
				DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(media.getStoredName())
					.build();

				s3Client.deleteObject(request);

				mediaRepository.delete(media);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
