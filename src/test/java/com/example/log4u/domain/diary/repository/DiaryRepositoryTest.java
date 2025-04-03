package com.example.log4u.domain.diary.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import com.example.log4u.common.config.QueryDslConfig;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.fixture.DiaryFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
public class DiaryRepositoryTest {

	@Autowired
	private DiaryRepository diaryRepository;

	@PersistenceContext
	private EntityManager em;

	private final Long userId1 = 1L;
	private final Long userId2 = 2L;

	@BeforeEach
	void setUp() {
		diaryRepository.deleteAll();
		em.createNativeQuery("ALTER TABLE diary ALTER COLUMN diary_id RESTART WITH 1").executeUpdate();
		List<Diary> diaries = DiaryFixture.createDiariesFixture();
		diaryRepository.saveAll(diaries);
	}

	@Test
	@DisplayName("키워드로 공개 다이어리 검색")
	void searchDiariesByKeyword() {
		// given
		String keyword = "날씨";
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<Diary> result = diaryRepository.searchDiaries(keyword, visibilities, SortType.LATEST, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("첫번째 일기");
		assertThat(result.getContent().get(0).getContent()).contains("날씨");
	}

	@Test
	@DisplayName("인기순으로 다이어리 정렬")
	void searchDiariesSortByPopular() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<Diary> result = diaryRepository.searchDiaries(null, visibilities, SortType.POPULAR, pageable);

		// then
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().get(0).getLikeCount()).isGreaterThanOrEqualTo(
			result.getContent().get(1).getLikeCount());
		assertThat(result.getContent().get(1).getLikeCount()).isGreaterThanOrEqualTo(
			result.getContent().get(2).getLikeCount());
	}

	@Test
	@DisplayName("최신순으로 다이어리 정렬")
	void searchDiariesSortByLatest() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<Diary> result = diaryRepository.searchDiaries(null, visibilities, SortType.LATEST, pageable);

		// then
		assertThat(result.getContent()).hasSize(3);

		// 실제로는 createdAt 필드를 비교해야 하지만 테스트에선 데이터 생성 순서로 대체
		if (result.getContent().size() >= 2) {
			assertThat(result.getContent().get(0).getCreatedAt())
				.isAfterOrEqualTo(result.getContent().get(1).getCreatedAt());
		}
	}

	@Test
	@DisplayName("사용자 ID와 공개 범위로 다이어리 조회")
	void findByUserIdAndVisibilityIn() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE,
			VisibilityType.FOLLOWER);
		PageRequest pageable = PageRequest.of(0, 12);

		// when
		Slice<Diary> result = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId1, visibilities, Long.MAX_VALUE, pageable);

		// then
		assertThat(result.getContent()).hasSize(4);
		assertThat(result.getContent().stream().allMatch(d -> d.getUserId().equals(userId1))).isTrue();
	}

	@Test
	@DisplayName("팔로워 범위로만 다이어리 조회")
	void findByVisibilityTypeFollower() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.FOLLOWER);
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Slice<Diary> result = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId1, visibilities, Long.MAX_VALUE, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getVisibility()).isEqualTo(VisibilityType.FOLLOWER);
	}

	@Test
	@DisplayName("커서 기반 페이징으로 다이어리 조회")
	void findByUserIdAndVisibilityInWithCursor() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 1);
		Long cursorId = 5L; // 인기 있는 일기의 ID

		// when
		Slice<Diary> result = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			null, visibilities, cursorId, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getDiaryId()).isLessThan(cursorId);

		System.out.println(result.getContent().get(0).getDiaryId());
	}

	@Test
	@DisplayName("빈 키워드로 검색시 모든 공개 다이어리 반환")
	void searchDiariesWithEmptyKeyword() {
		// given
		String keyword = "";
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		Page<Diary> result = diaryRepository.searchDiaries(keyword, visibilities, SortType.LATEST, pageable);

		// then
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().stream()
			.allMatch(d -> d.getVisibility() == VisibilityType.PUBLIC)).isTrue();
	}

	@Test
	@DisplayName("페이지 크기보다 작은 결과 조회시 hasNext는 false")
	void sliceHasNextIsFalseWhenResultSizeIsLessThanPageSize() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);
		PageRequest pageable = PageRequest.of(0, 5); // 페이지 크기가 5

		// when
		Slice<Diary> result = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId1, visibilities, Long.MAX_VALUE, pageable);

		// then
		assertThat(result.getContent().size()).isLessThan(pageable.getPageSize());
		assertThat(result.hasNext()).isFalse();
	}

	@Test
	@DisplayName("페이지 크기와 같은 결과 조회시 hasNext 확인")
	void checkHasNextWhenResultSizeEqualsPageSize() {
		// given
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE,
			VisibilityType.FOLLOWER);
		PageRequest pageable = PageRequest.of(0, 4); // 페이지 크기가 4, 결과도 4개

		// when
		Slice<Diary> result = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId1, visibilities, Long.MAX_VALUE, pageable);

		// then
		assertThat(result.getContent().size()).isEqualTo(pageable.getPageSize());
		assertThat(result.hasNext()).isFalse();
	}
}
