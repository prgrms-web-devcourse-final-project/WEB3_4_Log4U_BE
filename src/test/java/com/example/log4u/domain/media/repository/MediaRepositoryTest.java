package com.example.log4u.domain.media.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.log4u.common.config.QueryDslConfig;
import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.fixture.MediaFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
public class MediaRepositoryTest {

	@Autowired
	private MediaRepository mediaRepository;

	@PersistenceContext
	private EntityManager em;

	private final Long diaryId1 = 1L;
	private final Long diaryId2 = 2L;

	@BeforeEach
	void setUp() {
		mediaRepository.deleteAll();
		em.createNativeQuery("ALTER TABLE media ALTER COLUMN media_id RESTART WITH 1").executeUpdate();

		Media media1 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.PERMANENT);
		Media media2 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.PERMANENT);
		Media media3 = MediaFixture.createMediaFixture(null, diaryId2, MediaStatus.PERMANENT);
		Media media4 = MediaFixture.createMediaFixture(null, diaryId2, MediaStatus.TEMPORARY);
		Media media5 = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.DELETED);

		mediaRepository.saveAll(List.of(media1, media2, media3, media4, media5));
	}

	@Test
	@DisplayName("다이어리 ID(1)로 미디어 조회")
	void findByDiaryId() {
		// when
		List<Media> result = mediaRepository.findByDiaryId(diaryId1);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting("diaryId").contains(diaryId1);
	}

	@Test
	@DisplayName("다이어리 상태로 미디어 조회")
	void findByDiaryIdAndStatus() {
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
		Media newMedia = MediaFixture.createMediaFixture(null, diaryId1, MediaStatus.TEMPORARY);

		// when
		mediaRepository.save(newMedia);

		// then
		assertThat(newMedia.getMediaId()).isNotNull();
		assertThat(newMedia.getMediaId()).isEqualTo(6L);
		assertThat(newMedia.getStatus()).isEqualTo(MediaStatus.TEMPORARY);
		assertThat(newMedia.getDiaryId()).isEqualTo(diaryId1);
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
}
