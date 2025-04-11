package com.example.log4u.domain.hashtag.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.hashtag.entity.DiaryHashtag;
import com.example.log4u.domain.hashtag.entity.Hashtag;
import com.example.log4u.domain.hashtag.repository.DiaryHashtagRepository;
import com.example.log4u.domain.hashtag.repository.HashtagRepository;
import com.example.log4u.fixture.DiaryHashtagFixture;
import com.example.log4u.fixture.HashtagFixture;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

	@Mock
	private HashtagRepository hashtagRepository;

	@Mock
	private DiaryHashtagRepository diaryHashtagRepository;

	@InjectMocks
	private HashtagService hashtagService;

	@Test
	@DisplayName("해시태그 저장 - 해시태그 형식 입력")
	void saveHashtagsWithFormat() {
		// given
		Long diaryId = 1L;
		List<String> hashtagNames = HashtagFixture.createHashtagNames("여행", "맛집", "일상");

		// 기존 연결 없음
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(List.of());

		// 해시태그 조회 설정
		when(hashtagRepository.findByName("여행")).thenReturn(Optional.empty());
		when(hashtagRepository.findByName("맛집")).thenReturn(Optional.empty());
		when(hashtagRepository.findByName("일상")).thenReturn(Optional.empty());

		// 해시태그 저장 설정
		when(hashtagRepository.save(any(Hashtag.class)))
			.thenAnswer(invocation -> {
				Hashtag hashtag = invocation.getArgument(0);
				if (hashtag.getName().equals("여행")) {
					return HashtagFixture.TRAVEL;
				}
				if (hashtag.getName().equals("맛집")) {
					return HashtagFixture.FOOD;
				}
				return HashtagFixture.DAILY;
			});

		// when
		List<String> result = hashtagService.saveOrUpdateHashtag(diaryId, hashtagNames);

		// then
		assertThat(result).containsExactlyInAnyOrder("#여행", "#맛집", "#일상");

		// 검증 방식 변경
		verify(diaryHashtagRepository).findByDiaryId(diaryId);
		verify(hashtagRepository, times(3)).save(any(Hashtag.class)); // 3개 해시태그 저장
		verify(diaryHashtagRepository).saveAll(anyList()); // 3개 연결 벌크 저장
		verify(diaryHashtagRepository, never()).deleteAll(anyList()); // 기존 연결 없으므로 삭제 없음
	}

	@Test
	@DisplayName("해시태그 저장 - 기존 해시태그 포함")
	void saveExistingHashtags() {
		// given
		Long diaryId = 1L;
		List<String> hashtagNames = HashtagFixture.createHashtagNames("여행", "맛집");

		// 기존 연결 없음
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(List.of());

		// 기존 해시태그 설정
		when(hashtagRepository.findByName("여행")).thenReturn(Optional.of(HashtagFixture.TRAVEL));
		when(hashtagRepository.findByName("맛집")).thenReturn(Optional.empty());

		// 새 해시태그 저장 설정
		when(hashtagRepository.save(any(Hashtag.class))).thenReturn(HashtagFixture.FOOD);

		// when
		List<String> result = hashtagService.saveOrUpdateHashtag(diaryId, hashtagNames);

		// then
		assertThat(result).containsExactlyInAnyOrder("#여행", "#맛집");

		// 검증 방식 변경
		verify(diaryHashtagRepository).findByDiaryId(diaryId);
		verify(hashtagRepository, times(1)).save(any(Hashtag.class)); // 맛집 해시태그 저장
		verify(diaryHashtagRepository).saveAll(anyList()); // 여행, 맛집 연결 저장
		verify(diaryHashtagRepository, never()).deleteAll(anyList()); // 기존 연결 없으므로 삭제 없음
	}

	@Test
	@DisplayName("해시태그 업데이트 - 일부 유지, 일부 추가, 일부 삭제")
	void updateHashtags() {
		// given
		Long diaryId = 1L;
		List<String> newHashtagNames = HashtagFixture.createHashtagNames("여행", "카페");

		// 기존 해시태그 연결 설정 (여행, 맛집)
		List<DiaryHashtag> existingLinks = DiaryHashtagFixture.createDiaryHashtagsForDiary(
			diaryId, HashtagFixture.TRAVEL.getHashtagId(), HashtagFixture.FOOD.getHashtagId()
		);
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(existingLinks);

		// 해시태그 조회 설정
		when(hashtagRepository.findByName("여행")).thenReturn(Optional.of(HashtagFixture.TRAVEL));
		when(hashtagRepository.findByName("카페")).thenReturn(Optional.of(HashtagFixture.CAFE));

		// when
		List<String> result = hashtagService.saveOrUpdateHashtag(diaryId, newHashtagNames);

		// then
		assertThat(result).containsExactlyInAnyOrder("#여행", "#카페");
		verify(diaryHashtagRepository).findByDiaryId(diaryId);
		verify(diaryHashtagRepository).saveAll(anyList());
		verify(diaryHashtagRepository).deleteAll(anyList()); // 맛집 연결 삭제
	}

	@Test
	@DisplayName("해시태그 저장 - 빈 리스트")
	void saveEmptyHashtags() {
		// given
		Long diaryId = 1L;
		List<String> hashtagNames = List.of();

		// 기존 연결 없음
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(List.of());

		// when
		List<String> result = hashtagService.saveOrUpdateHashtag(diaryId, hashtagNames);

		// then
		assertThat(result).isEmpty();
		verify(diaryHashtagRepository).findByDiaryId(diaryId);
		verify(hashtagRepository, never()).save(any(Hashtag.class));
		verify(diaryHashtagRepository, never()).save(any(DiaryHashtag.class));
	}

	@Test
	@DisplayName("다이어리 ID로 해시태그 조회")
	void getHashtagsByDiaryId() {
		// given
		Long diaryId = 1L;

		// 다이어리-해시태그 연결 설정
		List<DiaryHashtag> diaryHashtags = DiaryHashtagFixture.createDiaryHashtagsForDiary(
			diaryId, HashtagFixture.TRAVEL.getHashtagId(), HashtagFixture.FOOD.getHashtagId()
		);
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(diaryHashtags);

		// 해시태그 조회 설정
		when(hashtagRepository.findAllById(List.of(1L, 2L)))
			.thenReturn(List.of(HashtagFixture.TRAVEL, HashtagFixture.FOOD));

		// when
		List<String> result = hashtagService.getHashtagsByDiaryId(diaryId);

		// then
		assertThat(result).containsExactlyInAnyOrder("#여행", "#맛집");
	}

	@Test
	@DisplayName("다이어리 ID 목록으로 해시태그 맵 조회")
	void getHashtagMapByDiaryIds() {
		// given
		List<Long> diaryIds = List.of(1L, 2L);

		// 다이어리-해시태그 연결 설정
		List<DiaryHashtag> diaryHashtags = DiaryHashtagFixture.createMultipleDiaryHashtags(
			diaryIds, List.of(HashtagFixture.TRAVEL, HashtagFixture.FOOD, HashtagFixture.CAFE)
		);
		when(diaryHashtagRepository.findByDiaryIdIn(diaryIds)).thenReturn(diaryHashtags);

		// 해시태그 조회 설정
		when(hashtagRepository.findAllById(anyList()))
			.thenReturn(List.of(HashtagFixture.TRAVEL, HashtagFixture.FOOD, HashtagFixture.CAFE));

		// when
		Map<Long, List<String>> result = hashtagService.getHashtagMapByDiaryIds(diaryIds);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(1L)).contains("여행", "맛집", "카페");
		assertThat(result.get(2L)).contains("여행", "맛집", "카페");
	}

	@Test
	@DisplayName("해시태그 이름 처리 - # 제거 후 저장, 응답 시 # 추가")
	void processHashtag() {
		// given
		Long diaryId = 1L;

		// 기존 연결 없음
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(List.of());

		// 해시태그 저장 모의 설정
		when(hashtagRepository.findByName(anyString())).thenReturn(Optional.empty());
		when(hashtagRepository.save(any(Hashtag.class)))
			.thenAnswer(invocation -> {
				Hashtag hashtag = invocation.getArgument(0);
				return HashtagFixture.createHashtag(1L, hashtag.getName());
			});

		// when & then
		assertThat(hashtagService.saveOrUpdateHashtag(diaryId, List.of("#여행")))
			.containsExactly("#여행");

		assertThat(hashtagService.saveOrUpdateHashtag(diaryId, List.of("맛집")))
			.containsExactly("#맛집");

		assertThat(hashtagService.saveOrUpdateHashtag(diaryId, List.of("")))
			.isEmpty();

		List<String> nullList = new ArrayList<>();
		nullList.add(null);
		assertThat(hashtagService.saveOrUpdateHashtag(diaryId, nullList))
			.isEmpty();
	}

	@Test
	@DisplayName("해시태그 벌크 저장 및 삭제")
	void bulkSaveAndDelete() {
		// given
		Long diaryId = 1L;
		List<String> newHashtagNames = HashtagFixture.createHashtagNames("태그1", "태그2", "태그3");

		// 기존 해시태그 연결 설정
		List<DiaryHashtag> existingLinks = DiaryHashtagFixture.createDiaryHashtagsForDiary(
			diaryId, 10L, 20L // 기존 태그A, 태그B
		);
		when(diaryHashtagRepository.findByDiaryId(diaryId)).thenReturn(existingLinks);

		// 새 해시태그 설정
		when(hashtagRepository.findByName("태그1")).thenReturn(Optional.empty());
		when(hashtagRepository.findByName("태그2")).thenReturn(Optional.empty());
		when(hashtagRepository.findByName("태그3")).thenReturn(Optional.empty());

		when(hashtagRepository.save(any(Hashtag.class)))
			.thenAnswer(invocation -> {
				Hashtag hashtag = invocation.getArgument(0);
				return HashtagFixture.createHashtag(
					Long.valueOf(hashtag.getName().charAt(2)), // 태그1 -> ID 1
					hashtag.getName()
				);
			});

		// when
		List<String> result = hashtagService.saveOrUpdateHashtag(diaryId, newHashtagNames);

		// then
		assertThat(result).containsExactlyInAnyOrder("#태그1", "#태그2", "#태그3");

		// 새 연결 벌크 저장 확인
		verify(diaryHashtagRepository).saveAll(anyList());

		// 기존 연결 벌크 삭제 확인
		verify(diaryHashtagRepository).deleteAll(anyList());
	}
}