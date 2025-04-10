package com.example.log4u.domain.diary.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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

import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.exception.OwnerAccessDeniedException;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
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

	@InjectMocks
	private DiaryService diaryService;

	@Test
	@DisplayName("다이어리 생성 - 성공")
	void saveDiary_success() {
		// given
		Long userId = 1L;
		String thumbnailUrl = "https://example.com/thumb.jpg";

		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();

		given(diaryRepository.save(any(Diary.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		Diary result = diaryService.saveDiary(userId, request, thumbnailUrl);

		// then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getTitle()).isEqualTo(request.title());
		assertThat(result.getContent()).isEqualTo(request.content());
		assertThat(result.getThumbnailUrl()).isEqualTo(thumbnailUrl);

		verify(diaryRepository).save(any(Diary.class));
	}

	@Test
	@DisplayName("키워드로 다이어리 검색 성공")
	void searchDiaries() {
		// given
		String keyword = "테스트";
		SortType sort = SortType.LATEST;
		Long cursorId = null; // 커서 ID를 null로 설정 (첫 페이지 조회)
		int size = 6;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), false);

		given(diaryRepository.searchDiariesByCursor(
			eq(keyword),
			eq(List.of(VisibilityType.PUBLIC)),
			eq(sort),
			anyLong(),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		// when
		Slice<DiaryResponseDto> result = diaryService.searchDiariesByCursor(keyword, sort, cursorId, size);

		// then
		assertEquals(6, result.getSize());
		assertFalse(result.hasNext());

		verify(diaryRepository).searchDiariesByCursor(
			eq(keyword),
			eq(List.of(VisibilityType.PUBLIC)),
			eq(sort),
			anyLong(),
			any(PageRequest.class)
		);
	}

	@Test
	@DisplayName("로그인한 사용자가 공개 다이어리 상세 조회 성공")
	void getDiaryDetail_public() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, 2L); // 다른 사용자의 공개 다이어리
		List<Media> mediaList = List.of(MediaFixture.createMediaFixture(10L, diaryId));

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(mediaService.getMediaByDiaryId(diaryId)).willReturn(mediaList);

		// when
		DiaryResponseDto result = diaryService.getDiary(userId, diaryId);

		// then
		assertThat(result.diaryId()).isEqualTo(diaryId);
		assertThat(result.userId()).isEqualTo(2L);
		assertThat(result.visibility()).isEqualTo(VisibilityType.PUBLIC.name());
	}

	@Test
	@DisplayName("로그인한 사용자가 팔로워 다이어리 상세 조회 성공 - 팔로워인 경우")
	void getDiaryDetail_follower_success() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;
		Long authorId = 2L;

		Diary diary = DiaryFixture.createFollowerDiaryFixture(diaryId, authorId); // 다른 사용자의 팔로워 다이어리
		List<Media> mediaList = List.of(MediaFixture.createMediaFixture(10L, diaryId));

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(followRepository.existsByInitiatorIdAndTargetId(userId, authorId)).willReturn(true);
		given(mediaService.getMediaByDiaryId(diaryId)).willReturn(mediaList);

		// when
		DiaryResponseDto result = diaryService.getDiary(userId, diaryId);

		// then
		assertThat(result.diaryId()).isEqualTo(diaryId);
		assertThat(result.userId()).isEqualTo(authorId);
		assertThat(result.visibility()).isEqualTo(VisibilityType.FOLLOWER.name());
	}

	@Test
	@DisplayName("로그인한 사용자가 팔로워 다이어리 상세 조회 실패 - 팔로워가 아닌 경우")
	void getDiaryDetail_follower_fail() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;
		Long authorId = 2L;

		Diary diary = DiaryFixture.createFollowerDiaryFixture(diaryId, authorId); // 다른 사용자의 팔로워 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(followRepository.existsByInitiatorIdAndTargetId(userId, authorId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> diaryService.getDiary(userId, diaryId))
			.isInstanceOf(NotFoundDiaryException.class);
	}

	@Test
	@DisplayName("로그인한 사용자가 비공개 다이어리 상세 조회 성공 - 작성자인 경우")
	void getDiaryDetail_private_success() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;

		Diary diary = DiaryFixture.createPrivateDiaryFixture(diaryId, userId); // 자신의 비공개 다이어리
		List<Media> mediaList = List.of(MediaFixture.createMediaFixture(10L, diaryId));

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(mediaService.getMediaByDiaryId(diaryId)).willReturn(mediaList);

		// when
		DiaryResponseDto result = diaryService.getDiary(userId, diaryId);

		// then
		assertThat(result.diaryId()).isEqualTo(diaryId);
		assertThat(result.userId()).isEqualTo(userId);
		assertThat(result.visibility()).isEqualTo(VisibilityType.PRIVATE.name());
	}

	@Test
	@DisplayName("로그인한 사용자가 비공개 다이어리 상세 조회 실패 - 작성자가 아닌 경우")
	void getDiaryDetail_private_fail() {
		// given
		Long diaryId = 1L;
		Long userId = 1L;
		Long authorId = 2L;

		Diary diary = DiaryFixture.createPrivateDiaryFixture(diaryId, authorId); // 다른 사용자의 비공개 다이어리

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.getDiary(userId, diaryId))
			.isInstanceOf(NotFoundDiaryException.class);
	}

	@Test
	@DisplayName("커서 기반 다이어리 목록 조회 성공")
	void getDiariesByCursor() {
		// given
		Long userId = 1L;
		Long targetUserId = 2L;
		Long cursorId = 5L;
		int size = 12;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, size), true);

		// mocking
		given(followRepository.existsByInitiatorIdAndTargetId(userId, targetUserId)).willReturn(true);
		given(diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			eq(targetUserId),
			anyList(),
			eq(cursorId),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		// when
		Slice<DiaryResponseDto> result = diaryService.getDiaryResponseDtoSlice(userId, targetUserId, cursorId, size);

		// then
		assertThat(result).hasSize(3);
		assertThat(result.hasNext()).isTrue();
		verify(diaryRepository).findByUserIdAndVisibilityInAndCursorId(
			eq(targetUserId),
			anyList(),
			eq(cursorId),
			any(PageRequest.class)
		);
	}

	@Test
	@DisplayName("다이어리 수정 성공")
	void updateDiary() {
		// given
		Diary diary = mock(Diary.class);
		DiaryRequestDto request = DiaryFixture.createPublicDiaryRequestDtoFixture();

		String newThumbnailUrl = "https://example.com/public.jpg";

		// when
		diaryService.updateDiary(diary, request, newThumbnailUrl);

		// then
		verify(diary).update(request, newThumbnailUrl); // update 메서드 호출 확인
		verify(diaryRepository).save(diary); // save 호출 확인
	}

	@Test
	@DisplayName("다이어리 수정 실패(검증 로직 실패) - 작성자가 아닌 경우")
	void updateDiary_notOwner() {
		// given
		Long userId = 1L;
		Long authorId = 2L;
		Long diaryId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, authorId);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.getDiaryAfterValidateOwnership(diaryId, userId))
			.isInstanceOf(OwnerAccessDeniedException.class);
	}

	@Test
	@DisplayName("다이어리 삭제 성공")
	void deleteDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, userId);

		// when
		diaryService.deleteDiary(diary);

		// then
		verify(diaryRepository).delete(diary);
	}

	@Test
	@DisplayName("다이어리 삭제전 소유자 검증  - 작성자가 아닌 경우")
	void deleteDiary_notOwner() {
		// given
		Long userId = 1L;
		Long authorId = 2L;
		Long diaryId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, authorId);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then  (서비스 검증로직)
		assertThatThrownBy(() -> diaryService.getDiaryAfterValidateOwnership(userId, diaryId))
			.isInstanceOf(OwnerAccessDeniedException.class);
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
}
