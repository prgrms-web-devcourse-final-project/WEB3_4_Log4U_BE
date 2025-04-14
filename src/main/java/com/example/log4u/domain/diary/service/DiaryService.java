package com.example.log4u.domain.diary.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.dto.DiaryWithAuthorDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.exception.OwnerAccessDeniedException;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.hashtag.service.HashtagService;
import com.example.log4u.domain.like.repository.LikeRepository;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final FollowRepository followRepository;
	private final MediaService mediaService;
	private final LikeRepository likeRepository;
	private final HashtagService hashtagService;

	// 다이어리 생성
	@Transactional
	public Diary saveDiary(Long userId, DiaryRequestDto request, String thumbnailUrl) {
		return diaryRepository.save(DiaryRequestDto.toEntity(userId, request, thumbnailUrl));
	}

	// 다이어리 검색
	@Transactional(readOnly = true)
	public Slice<DiaryResponseDto> searchDiariesByCursor(
		String keyword,
		SortType sort,
		Long cursorId,
		int size
	) {
		Slice<DiaryWithAuthorDto> diaries = diaryRepository.searchDiariesByCursor(
			keyword,
			List.of(VisibilityType.PUBLIC),
			sort,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);
		return this.mapDiaryWithAuthorToDtoSlice(diaries);
	}

	// 다이어리 목록 (프로필 페이지)
	@Transactional(readOnly = true)
	public Slice<DiaryResponseDto> getDiaryResponseDtoSlice(Long userId, Long targetUserId, Long cursorId, int size) {
		List<VisibilityType> visibilities = determineAccessibleVisibilities(userId, targetUserId);
		Slice<DiaryWithAuthorDto> diaries = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			targetUserId,
			visibilities,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);
		return this.mapDiaryWithAuthorToDtoSlice(diaries);
	}

	// 다이어리 수정
	@Transactional
	public void updateDiary(Diary diary, DiaryRequestDto request, String newThumbnailUrl) {
		diary.update(request, newThumbnailUrl);
		diaryRepository.save(diary);
	}

	// 다이어리 삭제
	@Transactional
	public void deleteDiary(Diary diary) {
		diaryRepository.delete(diary);
	}

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getMyDiariesByCursor(Long userId, VisibilityType visibilityType,
		Long cursorId, int size) {
		List<VisibilityType> visibilities =
			visibilityType == null ? List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE, VisibilityType.FOLLOWER) :
				List.of(visibilityType);

		Slice<DiaryWithAuthorDto> diaries = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId,
			visibilities,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);

		Slice<DiaryResponseDto> dtoSlice = this.mapDiaryWithAuthorToDtoSlice(diaries);

		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;

		return PageResponse.of(dtoSlice, nextCursor);
	}

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getLikeDiariesByCursor(Long userId, Long targetUserId, Long cursorId,
		int size) {
		List<VisibilityType> visibilities = determineAccessibleVisibilities(userId, targetUserId);

		Slice<Diary> diaries = diaryRepository.getLikeDiarySliceByUserId(
			targetUserId,
			visibilities,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);

		Slice<DiaryResponseDto> dtoSlice = this.mapDiarySliceToDtoSlice(diaries);

		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;

		return PageResponse.of(dtoSlice, nextCursor);
	}

	private Diary findDiaryOrThrow(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(NotFoundDiaryException::new);
	}

	// Page용 매핑 메서드
	private Page<DiaryResponseDto> mapToDtoPage(Page<Diary> page) {
		List<DiaryResponseDto> content = getDiaryResponse(page.getContent());
		return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
	}

	// Slice용 매핑 메서드
	private Slice<DiaryResponseDto> mapDiarySliceToDtoSlice(Slice<Diary> slice) {
		List<DiaryResponseDto> content = getDiaryResponse(slice.getContent());
		return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
	}

	// DiaryWithAuthor Slice 매핑 메서드
	private Slice<DiaryResponseDto> mapDiaryWithAuthorToDtoSlice(Slice<DiaryWithAuthorDto> slice) {
		List<DiaryResponseDto> content = getDiaryResponseWithAuthor(slice.getContent());
		return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
	}

	// 다이어리 + 작성자 + 미디어 + 해시태그 같이 반환
	private List<DiaryResponseDto> getDiaryResponseWithAuthor(List<DiaryWithAuthorDto> diaryWithAuthorDtoList) {
		if (diaryWithAuthorDtoList.isEmpty()) {
			return List.of();
		}

		List<Long> diaryIds = diaryWithAuthorDtoList.stream()
			.map(dto -> dto.diary().getDiaryId())
			.toList();

		Map<Long, List<Media>> mediaMap = mediaService.getMediaMapByDiaryIds(diaryIds);
		Map<Long, List<String>> hashtagMap = hashtagService.getHashtagMapByDiaryIds(diaryIds);

		return diaryWithAuthorDtoList.stream()
			.map(dto -> DiaryResponseDto.of(
				dto,
				mediaMap.getOrDefault(dto.diary().getDiaryId(), List.of()),
				hashtagMap.getOrDefault(dto.diary().getDiaryId(), List.of())
			))
			.toList();
	}

	// 다이어리 + 미디어 + 해시태그 같이 반환
	private List<DiaryResponseDto> getDiaryResponse(List<Diary> diaries) {
		if (diaries.isEmpty()) {
			return List.of();
		}

		List<Long> diaryIds = diaries.stream()
			.map(Diary::getDiaryId)
			.toList();

		Map<Long, List<Media>> mediaMap = mediaService.getMediaMapByDiaryIds(diaryIds);
		Map<Long, List<String>> hashtagMap = hashtagService.getHashtagMapByDiaryIds(diaryIds);

		return diaries.stream()
			.map(diary -> DiaryResponseDto.of(
				diary,
				mediaMap.getOrDefault(diary.getDiaryId(), List.of()),
				hashtagMap.getOrDefault(diary.getDiaryId(), List.of())
			))
			.toList();
	}

	// 다이어리 작성자 본인 체크
	private void validateOwner(Diary diary, Long userId) {
		if (!diary.isOwner(userId)) {
			throw new OwnerAccessDeniedException();
		}
	}

	// 다이어리 목록 조회 시 권한 체크(공개 정책)
	private List<VisibilityType> determineAccessibleVisibilities(Long userId, Long targetUserId) {
		if (userId.equals(targetUserId)) {
			return List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE, VisibilityType.FOLLOWER);
		}

		if (followRepository.existsByInitiatorIdAndTargetId(userId, targetUserId)) {
			return List.of(VisibilityType.PUBLIC, VisibilityType.FOLLOWER);
		}

		return List.of(VisibilityType.PUBLIC);
	}

	// 파사드 패턴에서 사용할 검증 로직(소유 검증)
	public Diary getDiaryAfterValidateOwnership(Long diaryId, Long userId) {
		Diary diary = findDiaryOrThrow(diaryId);
		validateOwner(diary, userId);
		return diary;
	}

	// 파사드 패턴에서 사용할 검증 로직(공개 범위 검증)
	public Diary getDiaryAfterValidateAccess(Long diaryId, Long userId) {
		Diary diary = findDiaryOrThrow(diaryId);
		validateDiaryAccess(diary, userId);
		return diary;
	}

	// 다이어리 상세 조회 시 권한 체크
	private void validateDiaryAccess(Diary diary, Long userId) {
		if (diary.getVisibility() == VisibilityType.PRIVATE) {
			if (!diary.getUserId().equals(userId)) {
				throw new NotFoundDiaryException();
			}
		}

		if (diary.getVisibility() == VisibilityType.FOLLOWER) {
			if (!diary.getUserId().equals(userId)
				&& !followRepository.existsByInitiatorIdAndTargetId(userId, diary.getUserId())) {
				throw new NotFoundDiaryException();
			}
		}
	}

	public Diary getDiary(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(NotFoundDiaryException::new);
	}

	public Long incrementLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.incrementLikeCount();
	}

	public Long decreaseLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.decreaseLikeCount();
	}

	public Long getLikeCount(Long diaryId) {
		Diary diary = getDiary(diaryId);
		return diary.getLikeCount();
	}

	public void checkDiaryExists(Long diaryId) {
		if (!diaryRepository.existsById(diaryId)) {
			throw new NotFoundDiaryException();
		}
	}
}