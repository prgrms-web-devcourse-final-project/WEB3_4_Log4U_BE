package com.example.log4u.domain.media.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.log4u.domain.media.MediaStatus;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.fixture.MediaFixture;

@DataJpaTest
@ActiveProfiles("test")
public class MediaRepositoryTest {

	@Autowired
	private MediaRepository mediaRepository;

	private Long diaryId1;
	private Long diaryId2;
	private Long diaryId3;
	private Media media1;
	private Media media2;
	private Media media3;
	private Media media4;
	private Media media5;

	@BeforeEach
	void setUp() {
		mediaRepository.deleteAll();

		diaryId1 = 1L;
		diaryId2 = 2L;
		diaryId3 = 3L;

		media1 = MediaFixture.createMediaFixture(1L, diaryId1, MediaStatus.TEMPORARY);
		media2 = MediaFixture.createMediaFixture(2L, diaryId1, MediaStatus.PERMANENT);
		media3 = MediaFixture.createMediaFixture(3L, diaryId2, MediaStatus.TEMPORARY);
		media4 = MediaFixture.createMediaFixture(4L, diaryId3, MediaStatus.DELETED);
		media5 = MediaFixture.createMediaFixture(5L, diaryId2, MediaStatus.DELETED);

		mediaRepository.saveAll(List.of(media1, media2, media3, media4, media5));
	}

	@Test
	@DisplayName("다이어리 ID로 미디어 조회")
	void findByDiaryId() {
		// when
		List<Media> result = mediaRepository.findByDiaryId(diaryId1);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting("diaryId").contains(diaryId1);
	}
}
