package com.example.log4u.domain.diary.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

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
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.hashtag.service.HashtagService;
import com.example.log4u.domain.like.service.LikeService;
import com.example.log4u.domain.map.service.MapService;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.service.UserService;
import com.example.log4u.fixture.DiaryFixture;
import com.example.log4u.fixture.MediaFixture;
import com.example.log4u.fixture.UserFixture;

@ExtendWith(MockitoExtension.class)
class DiaryFacadeTest {

	@Mock
	private DiaryService diaryService;

	@Mock
	private MediaService mediaService;

	@Mock
	private MapService mapService;

	@Mock
	private LikeService likeService;

	@Mock
	private UserService userService;

	@Mock
	private HashtagService hashtagService;

	@InjectMocks
	private DiaryFacade diaryFacade;

	@Test
	@DisplayName("다이어리 생성 성공")
	void createDiary() {
		// given
		Long userId = 1L;
		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();
		Diary diary = DiaryFixture.createDiaryFixture();
		String thumbnailUrl = "https://example.com/thumbnail.jpg";

		given(mediaService.extractThumbnailUrl(request.mediaList())).willReturn(thumbnailUrl);
		given(diaryService.saveDiary(userId, request, thumbnailUrl)).willReturn(diary);

		// when
		diaryFacade.createDiary(userId, request);

		// then
		verify(mediaService).extractThumbnailUrl(request.mediaList());
		verify(diaryService).saveDiary(userId, request, thumbnailUrl);
		verify(mediaService).saveMedia(diary.getDiaryId(), request.mediaList());
		verify(hashtagService).saveOrUpdateHashtag(diary.getDiaryId(), request.hashtagList());
		verify(mapService).increaseRegionDiaryCount(request.location().latitude(), request.location().longitude());
	}

	@Test
	@DisplayName("다이어리 삭제 성공")
	void deleteDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;
		Diary diary = DiaryFixture.createDiaryFixture();

		given(diaryService.getDiaryAfterValidateOwnership(diaryId, userId)).willReturn(diary);

		// when
		diaryFacade.deleteDiary(userId, diaryId);

		// then
		verify(diaryService).getDiaryAfterValidateOwnership(diaryId, userId);
		verify(mediaService).deleteMediaByDiaryId(diaryId);
		verify(hashtagService).deleteHashtagsByDiaryId(diaryId);
		verify(diaryService).deleteDiary(diary);
	}

	@Test
	@DisplayName("다이어리 수정 성공")
	void updateDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;
		DiaryRequestDto request = DiaryFixture.createDiaryRequestDtoFixture();
		Diary diary = DiaryFixture.createDiaryFixture();
		String newThumbnailUrl = "https://example.com/new-thumbnail.jpg";

		given(diaryService.getDiaryAfterValidateOwnership(diaryId, userId)).willReturn(diary);
		given(mediaService.extractThumbnailUrl(request.mediaList())).willReturn(newThumbnailUrl);

		// when
		diaryFacade.updateDiary(userId, diaryId, request);

		// then
		verify(diaryService).getDiaryAfterValidateOwnership(diaryId, userId);
		verify(mediaService).updateMediaByDiaryId(diaryId, request.mediaList());
		verify(hashtagService).saveOrUpdateHashtag(diaryId, request.hashtagList());
		verify(mediaService).extractThumbnailUrl(request.mediaList());
		verify(diaryService).updateDiary(diary, request, newThumbnailUrl);
	}

	@Test
	@DisplayName("다이어리 단건 조회 성공")
	void getDiary() {
		// given
		Long userId = 1L;
		Long diaryId = 1L;
		Long authorId = 2L;
		Diary diary = DiaryFixture.createPublicDiaryFixture(diaryId, authorId); // 다른 사용자의 공개 다이어리
		List<Media> mediaList = List.of(MediaFixture.createMediaFixture(10L, diaryId));
		List<String> hashtagList = List.of("여행", "맛집");
		User author = UserFixture.createUserFixtureWithProfileImage(authorId);
		boolean isLiked = true;

		given(diaryService.getDiaryAfterValidateAccess(diaryId, userId)).willReturn(diary);
		given(likeService.isLiked(userId, diaryId)).willReturn(isLiked);
		given(mediaService.getMediaByDiaryId(diaryId)).willReturn(mediaList);
		given(hashtagService.getHashtagsByDiaryId(diaryId)).willReturn(hashtagList);
		given(userService.getUserById(diary.getUserId())).willReturn(author);

		// when
		DiaryResponseDto result = diaryFacade.getDiary(userId, diaryId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.diaryId()).isEqualTo(diaryId);
		assertThat(result.authorNickname()).isEqualTo(author.getNickname());
		assertThat(result.authorId()).isEqualTo(author.getUserId());
		assertThat(result.visibility()).isEqualTo(VisibilityType.PUBLIC.name());
		assertThat(result.mediaList()).hasSize(1);
		assertThat(result.hashtagList()).containsExactly("여행", "맛집");
		assertThat(result.isLiked()).isTrue();
	}

	@Test
	@DisplayName("다이어리 목록 조회 성공")
	void getDiariesByCursor() {
		// given
		Long userId = 1L;
		Long targetUserId = 2L;
		Long cursorId = null;
		int size = 10;

		Diary diary1 = DiaryFixture.createPublicDiaryFixture(1L, targetUserId);
		Diary diary2 = DiaryFixture.createPublicDiaryFixture(2L, targetUserId);
		Diary diary3 = DiaryFixture.createPublicDiaryFixture(3L, targetUserId);

		User author = UserFixture.createUserFixtureWithProfileImage(targetUserId);

		List<DiaryResponseDto> dtoList = List.of(
			DiaryResponseDto.of(
				diary1,
				List.of(MediaFixture.createMediaFixture(1L, 1L)),
				List.of("여행", "맛집"),
				false,
				author
			),
			DiaryResponseDto.of(
				diary2,
				List.of(MediaFixture.createMediaFixture(2L, 2L)),
				List.of("일상"),
				true,
				author
			),
			DiaryResponseDto.of(
				diary3,
				List.of(MediaFixture.createMediaFixture(3L, 3L)),
				List.of("제주도", "여행", "사진"),
				false,
				author
			)
		);

		Slice<DiaryResponseDto> dtoSlice = new SliceImpl<>(dtoList, PageRequest.of(0, size), true);
		PageResponse<DiaryResponseDto> pageResponse = PageResponse.of(dtoSlice, 3L);

		given(diaryService.getDiaryResponseDtoSlice(userId, targetUserId, cursorId, size)).willReturn(dtoSlice);

		// when
		PageResponse<DiaryResponseDto> result = diaryFacade.getDiariesByCursor(userId, targetUserId, cursorId, size);

		// then
		assertThat(result.list()).hasSize(3);
		assertThat(result.pageInfo().nextCursor()).isEqualTo(3L);
		assertThat(result.list().get(0).authorId()).isEqualTo(targetUserId);
	}

	@Test
	@DisplayName("다이어리 검색 성공")
	void searchDiariesByCursor() {
		// given
		String keyword = "여행";
		SortType sort = SortType.LATEST;
		Long cursorId = null;
		int size = 10;

		Long author1Id = 1L;
		Long author2Id = 2L;

		Diary diary1 = DiaryFixture.createPublicDiaryFixture(1L, author1Id);
		Diary diary2 = DiaryFixture.createPublicDiaryFixture(2L, author2Id);

		User user1 = UserFixture.createUserFixtureWithProfileImage(author1Id);
		User user2 = UserFixture.createUserFixtureWithProfileImage(author2Id);

		List<DiaryResponseDto> dtoList = List.of(
			DiaryResponseDto.of(
				diary1,
				List.of(MediaFixture.createMediaFixture(1L, 1L)),
				List.of("여행", "맛집"),
				false,
				user1
			),
			DiaryResponseDto.of(
				diary2,
				List.of(MediaFixture.createMediaFixture(2L, 2L)),
				List.of("여행", "제주도"),
				false,
				user2
			)
		);

		Slice<DiaryResponseDto> dtoSlice = new SliceImpl<>(dtoList, PageRequest.of(0, size), true);
		PageResponse<DiaryResponseDto> pageResponse = PageResponse.of(dtoSlice, 2L);

		given(diaryService.searchDiariesByCursor(keyword, sort, cursorId, size)).willReturn(dtoSlice);

		// when
		PageResponse<DiaryResponseDto> result = diaryFacade.searchDiariesByCursor(keyword, sort, cursorId, size);

		// then
		assertThat(result.list()).hasSize(2);
		assertThat(result.pageInfo().nextCursor()).isEqualTo(2L);
	}
}