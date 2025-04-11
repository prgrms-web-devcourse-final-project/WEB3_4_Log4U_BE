/*
package com.example.log4u.domain.media.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.config.QueryDslConfig;
import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.fixture.MediaFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles({"dev", "dev-secret"})
class MediaRepositoryTest {

	@Autowired
	private MediaRepository mediaRepository;

	@PersistenceContext
	private EntityManager em;

	private final Long diaryId1 = 1L;
	private final Long diaryId2 = 2L;

	@BeforeEach
	void setUp() {
		mediaRepository.deleteAll();
		em.createNativeQuery("ALTER TABLE Media AUTO_INCREMENT = 1;").executeUpdate();

		Media media1 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.PERMANENT, 2);
		Media media2 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.PERMANENT, 0);
		Media media3 = MediaFixture.createMediaFixture(null, diaryId2, MediaStatus.PERMANENT, 1);
		Media media4 = MediaFixture.createMediaFixture(null, diaryId2, MediaStatus.TEMPORARY, 0);
		Media media5 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.DELETED, 1);

		mediaRepository.saveAll(List.of(media1, media2, media3, media4, media5));
	}

	@Test
	@DisplayName("다이어리 ID(1)로 미디어 조회")
	void findByDiaryId() {
		// when
		List<Media> result = mediaRepository.findByDiaryId(diaryId1);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting("diaryId").containsOnly(diaryId1);
	}

	@Test
	@DisplayName("다이어리 상태로 미디어 조회")
	void findByStatus() {
		// when
		List<Media> result = mediaRepository.findByStatus(MediaStatus.PERMANENT);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting("status").containsOnly(MediaStatus.PERMANENT);
	}

	@Test
	@DisplayName("미디어 저장(기존 5개)")
	void save() {
		// given
		Media newMedia = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.TEMPORARY, 3);

		// when
		mediaRepository.save(newMedia);

		// then
		assertThat(newMedia.getMediaId()).isNotNull();
		assertThat(newMedia.getMediaId()).isEqualTo(6L);
		assertThat(newMedia.getStatus()).isEqualTo(MediaStatus.TEMPORARY);
		assertThat(newMedia.getDiaryId()).isEqualTo(diaryId1);
		assertThat(newMedia.getOrderIndex()).isEqualTo(3);
	}

	@Test
	@DisplayName("미디어 삭제")
	void delete() {
		// given
		Long mediaId = 1L;
		Media media = mediaRepository.findById(mediaId).orElseThrow();

		// when
		mediaRepository.delete(media);

		// then
		Optional<Media> result = mediaRepository.findById(mediaId);
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("다이어리 ID 목록으로 미디어 조회")
	void findByDiaryIdIn() {
		// when
		List<Media> result = mediaRepository.findByDiaryIdIn(List.of(diaryId1, diaryId2));

		// then
		assertThat(result).hasSize(5);
		assertThat(result).extracting("diaryId").containsOnly(diaryId1, diaryId2);
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 삭제")
	void deleteByDiaryId() {
		// when
		mediaRepository.deleteByDiaryId(diaryId1);

		// then
		List<Media> remainingMedia = mediaRepository.findByDiaryId(diaryId1);
		assertThat(remainingMedia).isEmpty();

		List<Media> otherDiaryMedia = mediaRepository.findByDiaryId(diaryId2);
		assertThat(otherDiaryMedia).hasSize(2);
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 조회 - 순서대로 정렬")
	void findByDiaryIdOrderByOrderIndexAsc() {
		// when
		List<Media> result = mediaRepository.findByDiaryIdOrderByOrderIndexAsc(diaryId1);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting("diaryId").containsOnly(diaryId1);

		// 순서 확인 (0, 1, 2 순서로 정렬되어야 함)
		assertThat(result.get(0).getOrderIndex()).isEqualTo(0);
		assertThat(result.get(1).getOrderIndex()).isEqualTo(1);
		assertThat(result.get(2).getOrderIndex()).isEqualTo(2);
	}

	@Test
	@DisplayName("다이어리 ID 목록으로 미디어 조회 - 다이어리별로 순서대로 정렬")
	void findByDiaryIdInOrderByDiaryIdAscOrderIndexAsc() {
		// when
		List<Media> result = mediaRepository.findByDiaryIdInOrderByDiaryIdAscOrderIndexAsc(List.of(diaryId1, diaryId2));

		// then
		assertThat(result).hasSize(5);

		// 다이어리 ID별로 그룹화
		List<Media> diary1Media = result.stream()
			.filter(media -> media.getDiaryId().equals(diaryId1))
			.toList();

		List<Media> diary2Media = result.stream()
			.filter(media -> media.getDiaryId().equals(diaryId2))
			.toList();

		// 다이어리1 미디어 순서 확인
		assertThat(diary1Media).hasSize(3);
		assertThat(diary1Media.get(0).getOrderIndex()).isEqualTo(0);
		assertThat(diary1Media.get(1).getOrderIndex()).isEqualTo(1);
		assertThat(diary1Media.get(2).getOrderIndex()).isEqualTo(2);

		// 다이어리2 미디어 순서 확인
		assertThat(diary2Media).hasSize(2);
		assertThat(diary2Media.get(0).getOrderIndex()).isEqualTo(0);
		assertThat(diary2Media.get(1).getOrderIndex()).isEqualTo(1);
	}

	@Test
	@DisplayName("미디어 순서 업데이트")
	void updateMediaOrder() {
		// given
		Long mediaId = 1L;
		Media media = mediaRepository.findById(mediaId).orElseThrow();
		Integer originalOrder = media.getOrderIndex();

		// when
		media.setOrderIndex(5);
		mediaRepository.save(media);

		// then
		Media updatedMedia = mediaRepository.findById(mediaId).orElseThrow();
		assertThat(updatedMedia.getOrderIndex()).isEqualTo(5);
		assertThat(updatedMedia.getOrderIndex()).isNotEqualTo(originalOrder);
	}
}*/
