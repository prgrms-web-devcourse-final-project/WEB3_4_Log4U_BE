package com.example.log4u.domain.diary.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.example.log4u.domain.map.service.MapService;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.domain.user.repository.UserRepository;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.MediaFixture;

@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

	@Mock
	private DiaryRepository diaryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FollowRepository followRepository;

	@Mock
	private MediaService mediaService;

	@InjectMocks
	private DiaryService diaryService;

	@Mock
	private MapService mapService;

	private static final int CURSOR_PAGE_SIZE = 12;

	private static final int SEARCH_PAGE_SIZE = 6;

	@Test
	@DisplayName("다이어리 생성 성공")
	void saveDiary() {
		// given
		Long userId = 1L;
		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();

		String thumbnailUrl = "https://example.com/image1.jpg";
		Diary diary = DiaryFixture.createPublicDiaryFixture(1L, userId);

		given(mediaService.extractThumbnailUrl(request.mediaList())).willReturn(thumbnailUrl);
		given(diaryRepository.save(any(Diary.class))).willReturn(diary);

		// when
		diaryService.saveDiary(userId, request);

		// then
		verify(mediaService).saveMedia(eq(diary.getDiaryId()), eq(request.mediaList()));
		verify(mapService).increaseRegionDiaryCount(request.latitude(), request.longitude());
	}

	@Test
	@DisplayName("키워드로 다이어리 검색 성공")
	void searchDiaries() {
		// given
		String keyword = "테스트";
		SortType sort = SortType.LATEST;
		int page = 0;
		int size = 6;

		List<Diary> diaries = DiaryFixture.createDiariesWithIdsFixture(3);
		Page<Diary> diaryPage = new PageImpl<>(diaries, PageRequest.of(0, SEARCH_PAGE_SIZE), 3);

		given(diaryRepository.searchDiaries(
			eq(keyword),
			eq(List.of(VisibilityType.PUBLIC)),
			eq(sort),
			any(PageRequest.class)
		)).willReturn(diaryPage);

		Map<Long, List<Media>> mediaMap = new HashMap<>();
		for (Diary diary : diaries) {
			mediaMap.put(diary.getDiaryId(), List.of(
				MediaFixture.createMediaFixture(diary.getDiaryId() * 10, diary.getDiaryId())
			));
		}

		given(mediaService.getMediaMapByDiaryIds(anyList())).willReturn(mediaMap);

		// when
		PageResponse<DiaryResponseDto> result = diaryService.searchDiaries(keyword, sort, page, size);

		// then
		assertThat(result.content()).hasSize(3);
		assertThat(result.pageInfo().totalPages()).isEqualTo(1);
		assertThat(result.pageInfo().totalElements()).isEqualTo(3);

		assertThat(result.content()).allSatisfy(diary -> {
			assertThat(diary.title().contains(keyword) || diary.content().contains(keyword))
				.as("다이어리 제목 또는 내용에 키워드 '%s'가 포함되어야 합니다.", keyword)
				.isTrue();
		});

		DiaryResponseDto firstDiary = result.content().get(0);
		assertThat(firstDiary.diaryId()).isEqualTo(diaries.get(0).getDiaryId());
		assertThat(firstDiary.title()).isEqualTo(diaries.get(0).getTitle());
		assertThat(firstDiary.content()).isEqualTo(diaries.get(0).getContent());
		assertThat(firstDiary.userId()).isEqualTo(diaries.get(0).getUserId());
		assertThat(firstDiary.visibility()).isEqualTo(diaries.get(0).getVisibility().name());
		assertThat(firstDiary.weatherInfo()).isEqualTo(diaries.get(0).getWeatherInfo().name());
		assertThat(firstDiary.mediaList()).hasSize(1);
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
		Slice<Diary> diarySlice = new SliceImpl<>(diaries, PageRequest.of(0, CURSOR_PAGE_SIZE), false);

		given(diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			eq(targetUserId),
			eq(List.of(VisibilityType.PUBLIC)),
			eq(cursorId),
			any(PageRequest.class)
		)).willReturn(diarySlice);

		Map<Long, List<Media>> mediaMap = new HashMap<>();
		for (Diary diary : diaries) {
			mediaMap.put(diary.getDiaryId(), List.of(
				MediaFixture.createMediaFixture(diary.getDiaryId() * 10, diary.getDiaryId())
			));
		}

		given(mediaService.getMediaMapByDiaryIds(anyList())).willReturn(mediaMap);

		// when
		PageResponse<DiaryResponseDto> result = diaryService.getDiariesByCursor(userId, targetUserId, cursorId, size);

		// then
		assertThat(result.content()).hasSize(3);
		assertThat(result.pageInfo().hasNext()).isFalse();
	}

	@Test
	@DisplayName("다이어리 수정 성공")
	void updateDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;
		DiaryRequestDto request = DiaryFixture.createPublicDiaryRequestDtoFixture();

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, userId);
		String newThumbnailUrl = "https://example.com/public.jpg";

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));
		given(mediaService.extractThumbnailUrl(request.mediaList())).willReturn(newThumbnailUrl);

		// when
		diaryService.updateDiary(userId, diaryId, request);

		// then
		verify(mediaService).updateMediaByDiaryId(eq(diaryId), eq(request.mediaList()));
		assertThat(diary.getTitle()).isEqualTo(request.title());
		assertThat(diary.getContent()).isEqualTo(request.content());
		assertThat(diary.getThumbnailUrl()).isEqualTo(newThumbnailUrl);
	}

	@Test
	@DisplayName("다이어리 수정 실패 - 작성자가 아닌 경우")
	void updateDiary_notOwner() {
		// given
		Long userId = 1L;
		Long authorId = 2L;
		Long diaryId = 1L;
		DiaryRequestDto request = DiaryFixture.createPublicDiaryRequestDtoFixture();

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, authorId);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.updateDiary(userId, diaryId, request))
			.isInstanceOf(OwnerAccessDeniedException.class);
	}

	@Test
	@DisplayName("다이어리 삭제 성공")
	void deleteDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, userId);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when
		diaryService.deleteDiary(userId, diaryId);

		// then
		verify(mediaService).deleteMediaByDiaryId(diaryId);
		verify(diaryRepository).delete(diary);
	}

	@Test
	@DisplayName("다이어리 삭제 실패 - 작성자가 아닌 경우")
	void deleteDiary_notOwner() {
		// given
		Long userId = 1L;
		Long authorId = 2L;
		Long diaryId = 1L;

		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, authorId);

		given(diaryRepository.findById(diaryId)).willReturn(Optional.of(diary));

		// when & then
		assertThatThrownBy(() -> diaryService.deleteDiary(userId, diaryId))
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
