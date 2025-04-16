package com.example.log4u.domain.media.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.repository.MediaRepository;
import com.example.log4u.domain.media.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaScheduler {

	private final MediaRepository mediaRepository;
	private final S3Service s3Service;

	/**
	 * 임시 미디어 정리 (24시간 이상 지난 것)
	 * 매시간 실행
	 */
	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void cleanupTemporaryMedia() {
		LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
		List<Media> temporaryMedia = mediaRepository.findByStatusAndCreatedAtBefore(
			MediaStatus.TEMPORARY,
			cutoffTime
		);

		if (temporaryMedia.isEmpty()) {
			return;
		}

		log.info("Found {} temporary media files to clean up", temporaryMedia.size());

		// 비동기 삭제 처리
		s3Service.deleteFilesFromS3(temporaryMedia);
	}

	/**
	 * 삭제 실패 미디어 재시도
	 * 15분마다 실행
	 */
	@Scheduled(cron = "0 */15 * * * *")
	@Transactional
	public void retryDelete() {
		// 삭제 실패 상태인 미디어 조회
		List<Media> failedMedia = mediaRepository.findByStatus(MediaStatus.FAILED_DELETE);

		if (failedMedia.isEmpty()) {
			return;
		}

		log.info("Retrying deletion for {} failed media files", failedMedia.size());

		// 비동기 삭제 처리
		s3Service.deleteFilesFromS3(failedMedia);
	}
}