package com.example.log4u.domain.diary.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.exception.OwnerAccessDeniedException;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.hashtag.service.HashtagService;
import com.example.log4u.domain.like.repository.LikeRepository;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.MediaFixture;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

	@Mock
	private DiaryRepository diaryRepository;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private MediaService mediaService;

	@Mock
	private LikeRepository likeRepository;

	@Mock
	private HashtagService hashtagService;

	@InjectMocks
	private DiaryService diaryService;

	@Test
	@DisplayName("다이어리 저장 성공")
	void saveDiary() {
		// given
		Long userId = 1L;
		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();
		String thumbnailUrl = "https://example.com/thumbnail.jpg";
		Diary diary = DiaryFixture.createDiaryFixture();

		given(diaryRepository.save(any(Diary.class))).willReturn(diary);

		// when
		Diary savedDiary = diaryService.saveDiary(userId, request, thumbnailUrl);

		// then
		assertThat(savedDiary).isNotNull();
		assertThat(savedDiary.getDiaryId()).isEqualTo(diary.getDiaryId());
		assertThat(savedDiary.getUserId()).isEqualTo(diary.getUserId());
	}

	@Test
	@DisplayName("다이어리 검색 성공")
	void searchDiariesByCursor() {
		// given
		String keyword = "테스트";
		SortType sort = SortType.LATEST;
		Long cursorId = null;
		int size = 10;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), false);

		given(diaryRepository.searchDiariesByCursor(
			eq(keyword),
			eq(List.of(VisibilityType.PUBLIC)),
			eq(sort),
			eq(Long.MAX_VALUE),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		List<Long> diaryIds = diaries.stream().map(Diary::getDiaryId).toList();
		Map<Long, List<Media>> mediaMap = Map.of(
			1L, List.of(MediaFixture.createMediaFixture(1L, 1L)),
			2L, List.of(MediaFixture.createMediaFixture(2L, 2L)),
			3L, List.of(MediaFixture.createMediaFixture(3L, 3L))
		);
		Map<Long, List<String>> hashtagMap = Map.of(
			1L, List.of("여행", "맛집"),
			2L, List.of("일상"),
			3L, List.of("제주도", "여행", "사진")
		);

		given(mediaService.getMediaMapByDiaryIds(diaryIds)).willReturn(mediaMap);
		given(hashtagService.getHashtagMapByDiaryIds(diaryIds)).willReturn(hashtagMap);

		// when
		Slice<DiaryResponseDto> result = diaryService.searchDiariesByCursor(keyword, sort, cursorId, size);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(3);

		// 미디어와 해시태그 맵 조회 검증
		verify(mediaService).getMediaMapByDiaryIds(diaryIds);
		verify(hashtagService).getHashtagMapByDiaryIds(diaryIds);
	}

	@Test
	@DisplayName("다이어리 목록 조회 성공 (프로필 페이지)")
	void getDiaryResponseDtoSlice() {
		// given
		Long userId = 1L;
		Long targetUserId = 1L;
		Long cursorId = null;
		int size = 10;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), false);

		given(diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			eq(targetUserId),
			anyList(),
			eq(Long.MAX_VALUE),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		List<Long> diaryIds = diaries.stream().map(Diary::getDiaryId).toList();
		Map<Long, List<Media>> mediaMap = Map.of(
			1L, List.of(MediaFixture.createMediaFixture(1L, 1L)),
			2L, List.of(MediaFixture.createMediaFixture(2L, 2L)),
			3L, List.of(MediaFixture.createMediaFixture(3L, 3L))
		);
		Map<Long, List<String>> hashtagMap = Map.of(
			1L, List.of("여행", "맛집"),
			2L, List.of("일상"),
			3L, List.of("제주도", "여행", "사진")
		);

		given(mediaService.getMediaMapByDiaryIds(diaryIds)).willReturn(mediaMap);
		given(hashtagService.getHashtagMapByDiaryIds(diaryIds)).willReturn(hashtagMap);

		// when
		Slice<DiaryResponseDto> result = diaryService.getDiaryResponseDtoSlice(userId, targetUserId, cursorId, size);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(3);

		// 미디어와 해시태그 맵 조회 검증
		verify(mediaService).getMediaMapByDiaryIds(diaryIds);
		verify(hashtagService).getHashtagMapByDiaryIds(diaryIds);
	}

	@Test
	@DisplayName("다이어리 수정 성공")
	void updateDiary() {
		// given
		Diary diary = DiaryFixture.createDiaryFixture();
		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();
		String newThumbnailUrl = "https://example.com/new-thumbnail.jpg";

		// when
		diaryService.updateDiary(diary, request, newThumbnailUrl);

		// then
		verify(diaryRepository).save(diary);
	}

	@Test
	@DisplayName("다이어리 삭제 성공")
	void deleteDiary() {
		// given
		Diary diary = DiaryFixture.createDiaryFixture();

		// when
		diaryService.deleteDiary(diary);

		// then
		verify(diaryRepository).delete(diary);
	}

	@Test
	@DisplayName("다이어리 소유자 검증 성공")
	void getDiaryAfterValidateOwnership_success() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;
		Diary diary = DiaryFixture.createDiaryFixture();

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Diary result = diaryService.getDiaryAfterValidateOwnership(diaryId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getDiaryId()).isEqualTo(diaryId);
	}

	@Test
	@DisplayName("다이어리 소유자 검증 실패")
	void getDiaryAfterValidateOwnership_fail() {
		// given
		Long diaryId = 1L;
		Long userId = 2L; // 다른 사용자
		Diary diary = DiaryFixture.createDiaryFixture(); // userId가 1L인 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.getDiaryAfterValidateOwnership(diaryId, userId))
			.isInstanceOf(OwnerAccessDeniedException.class);
	}

	@Test
	@DisplayName("공개 다이어리 접근 검증 성공")
	void getDiaryAfterValidateAccess_public() {
		// given
		Long diaryId = 1L;
		Long userId = 2L; // 다른 사용자
		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, 1L); // 공개 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Diary result = diaryService.getDiaryAfterValidateAccess(diaryId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getDiaryId()).isEqualTo(diaryId);
	}

	@Test
	@DisplayName("비공개 다이어리 접근 검증 성공 (본인)")
	void getDiaryAfterValidateAccess_private_owner() {
		// given
		Long diaryId = 1L;
		Long userId = 1L; // 본인
		Diary diary = DiaryFixture.createPrivateDiaryFixture(diaryId, userId); // 비공개 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Diary result = diaryService.getDiaryAfterValidateAccess(diaryId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getDiaryId()).isEqualTo(diaryId);
	}

	@Test
	@DisplayName("비공개 다이어리 접근 검증 실패 (타인)")
	void getDiaryAfterValidateAccess_private_other() {
		// given
		Long diaryId = 1L;
		Long userId = 2L; // 다른 사용자
		Diary diary = DiaryFixture.createPrivateDiaryFixture(diaryId, 1L); // 비공개 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.getDiaryAfterValidateAccess(diaryId, userId))
			.isInstanceOf(NotFoundDiaryException.class);
	}

	@Test
	@DisplayName("팔로워 다이어리 접근 검증 성공 (본인)")
	void getDiaryAfterValidateAccess_follower_owner() {
		// given
		Long diaryId = 1L;
		Long userId = 1L; // 본인
		Diary diary = DiaryFixture.createFollowerDiaryFixture(diaryId, userId); // 팔로워 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Diary result = diaryService.getDiaryAfterValidateAccess(diaryId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getDiaryId()).isEqualTo(diaryId);
	}

	@Test
	@DisplayName("팔로워 다이어리 접근 검증 성공 (팔로워)")
	void getDiaryAfterValidateAccess_follower_follower() {
		// given
		Long diaryId = 1L;
		Long authorId = 1L;
		Long userId = 2L; // 팔로워
		Diary diary = DiaryFixture.createFollowerDiaryFixture(diaryId, authorId); // 팔로워 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(followRepository.existsByInitiatorIdAndTargetId(userId, authorId)).willReturn(true);

		// when
		Diary result = diaryService.getDiaryAfterValidateAccess(diaryId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getDiaryId()).isEqualTo(diaryId);
	}

	@Test
	@DisplayName("팔로워 다이어리 접근 검증 실패 (비팔로워)")
	void getDiaryAfterValidateAccess_follower_non_follower() {
		// given
		Long diaryId = 1L;
		Long authorId = 1L;
		Long userId = 2L; // 비팔로워
		Diary diary = DiaryFixture.createFollowerDiaryFixture(diaryId, authorId); // 팔로워 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(followRepository.existsByInitiatorIdAndTargetId(userId, authorId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> diaryService.getDiaryAfterValidateAccess(diaryId, userId))
			.isInstanceOf(NotFoundDiaryException.class);
	}

	@Test
	@DisplayName("좋아요 수 증가 성공")
	void incrementLikeCount() {
		// given
		Long diaryId = 1L;
		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, 1L);
		Long initialLikeCount = diary.getLikeCount();

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Long newLikeCount = diaryService.incrementLikeCount(diaryId);

		// then
		assertThat(newLikeCount).isEqualTo(initialLikeCount + 1);
		assertThat(diary.getLikeCount()).isEqualTo(initialLikeCount + 1);
	}

	@Test
	@DisplayName("좋아요 수 감소 성공")
	void decreaseLikeCount() {
		// given
		Long diaryId = 1L;
		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, 1L);
		Long initialLikeCount = diary.getLikeCount();

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Long newLikeCount = diaryService.decreaseLikeCount(diaryId);

		// then
		assertThat(newLikeCount).isEqualTo(initialLikeCount - 1);
		assertThat(diary.getLikeCount()).isEqualTo(initialLikeCount - 1);
	}

	@Test
	@DisplayName("좋아요 수 조회 성공")
	void getLikeCount() {
		// given
		Long diaryId = 1L;
		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, 1L);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		Long likeCount = diaryService.getLikeCount(diaryId);

		// then
		assertThat(likeCount).isEqualTo(diary.getLikeCount());
	}

	@Test
	@DisplayName("다이어리 존재 여부 확인 성공")
	void checkDiaryExists() {
		// given
		Long diaryId = 1L;

		given(diaryRepository.existsById(diaryId)).willReturn(true);

		// when & then
		assertThatCode(() -> diaryService.checkDiaryExists(diaryId))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("다이어리 존재 여부 확인 실패")
	void checkDiaryExists_notFound() {
		// given
		Long diaryId = 1L;

		given(diaryRepository.existsById(diaryId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> diaryService.checkDiaryExists(diaryId))
			.isInstanceOf(NotFoundDiaryException.class);
	}

	@Test
	@DisplayName("내 다이어리 목록 조회 성공")
	void getMyDiariesByCursor() {
		// given
		Long userId = 1L;
		VisibilityType visibilityType = null; // 모든 공개 범위
		Long cursorId = null;
		int size = 10;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), false);

		given(diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			eq(userId),
			anyList(),
			eq(Long.MAX_VALUE),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		List<Long> diaryIds = diaries.stream().map(Diary::getDiaryId).toList();
		Map<Long, List<Media>> mediaMap = Map.of(
			1L, List.of(MediaFixture.createMediaFixture(1L, 1L)),
			2L, List.of(MediaFixture.createMediaFixture(2L, 2L)),
			3L, List.of(MediaFixture.createMediaFixture(3L, 3L))
		);
		Map<Long, List<String>> hashtagMap = Map.of(
			1L, List.of("여행", "맛집"),
			2L, List.of("일상"),
			3L, List.of("제주도", "여행", "사진")
		);

		given(mediaService.getMediaMapByDiaryIds(diaryIds)).willReturn(mediaMap);
		given(hashtagService.getHashtagMapByDiaryIds(diaryIds)).willReturn(hashtagMap);

		// when
		PageResponse<DiaryResponseDto> result = diaryService.getMyDiariesByCursor(userId, visibilityType, cursorId,
			size);

		// then
		assertThat(result.list()).hasSize(3);
		assertThat(result.pageInfo().hasNext()).isNotNull();

		// 미디어와 해시태그 맵 조회 검증
		verify(mediaService).getMediaMapByDiaryIds(diaryIds);
		verify(hashtagService).getHashtagMapByDiaryIds(diaryIds);
	}

	@Test
	@DisplayName("좋아요한 다이어리 목록 조회 성공")
	void getLikeDiariesByCursor() {
		// given
		Long userId = 1L;
		Long targetUserId = 2L;
		Long cursorId = null;
		int size = 10;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), false);

		given(diaryRepository.getLikeDiarySliceByUserId(
			eq(targetUserId),
			anyList(),
			eq(Long.MAX_VALUE),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		List<Long> diaryIds = diaries.stream().map(Diary::getDiaryId).toList();
		Map<Long, List<Media>> mediaMap = Map.of(
			1L, List.of(MediaFixture.createMediaFixture(1L, 1L)),
			2L, List.of(MediaFixture.createMediaFixture(2L, 2L)),
			3L, List.of(MediaFixture.createMediaFixture(3L, 3L))
		);
		Map<Long, List<String>> hashtagMap = Map.of(
			1L, List.of("여행", "맛집"),
			2L, List.of("일상"),
			3L, List.of("제주도", "여행", "사진")
		);

		given(mediaService.getMediaMapByDiaryIds(diaryIds)).willReturn(mediaMap);
		given(hashtagService.getHashtagMapByDiaryIds(diaryIds)).willReturn(hashtagMap);

		// when
		PageResponse<DiaryResponseDto> result = diaryService.getLikeDiariesByCursor(userId, targetUserId, cursorId,
			size);

		// then
		assertThat(result.list()).hasSize(3);
		assertThat(result.pageInfo().hasNext()).isNotNull();

		// 미디어와 해시태그 맵 조회 검증
		verify(mediaService).getMediaMapByDiaryIds(diaryIds);
		verify(hashtagService).getHashtagMapByDiaryIds(diaryIds);
	}
}