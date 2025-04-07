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
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.exception.NotFoundDiaryException;
import com.example.log4u.domain.diary.exception.OwnerAccessDeniedException;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final MediaService mediaService;

	// 다이어리 생성
	@Transactional
	public void saveDiary(Long userId, DiaryRequestDto request) {
		String thumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		Diary diary = diaryRepository.save(
			DiaryRequestDto.toEntity(userId, request, thumbnailUrl)
		);
		mediaService.saveMedia(diary.getDiaryId(), request.mediaList());
	}

	// 다이어리 검색
	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> searchDiaries(
		String keyword,
		SortType sort,
		int page,
		int size
	) {
		Page<Diary> diaryPage = diaryRepository.searchDiaries(
			keyword,
			List.of(VisibilityType.PUBLIC),
			sort,
			PageRequest.of(page, size)
		);

		return PageResponse.of(mapToDtoPage(diaryPage));
	}

	// 다이어리 상세 조회
	@Transactional(readOnly = true)
	public DiaryResponseDto getDiary(Long userId, Long diaryId) {
		Diary diary = findDiaryOrThrow(diaryId);

		validateDiaryAccess(diary, userId);

		List<Media> media = mediaService.getMediaByDiaryId(diary.getDiaryId());
		return DiaryResponseDto.of(diary, media);
	}

	// 다이어리 목록 (프로필 페이지)
	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getDiariesByCursor(Long userId, Long targetUserId, Long cursorId, int size) {
		List<VisibilityType> visibilities = determineAccessibleVisibilities(userId, targetUserId);

		Slice<Diary> diaries = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			targetUserId,
			visibilities,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);

		Slice<DiaryResponseDto> dtoSlice = mapToDtoSlice(diaries);

		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;

		return PageResponse.of(dtoSlice, nextCursor);
	}

	// 다이어리 수정
	@Transactional
	public void updateDiary(Long userId, Long diaryId, DiaryRequestDto request) {
		Diary diary = findDiaryOrThrow(diaryId);
		validateOwner(diary, userId);

		if (request.mediaList() != null) {
			mediaService.updateMediaByDiaryId(diary.getDiaryId(), request.mediaList());
		}

		String newThumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		diary.update(request, newThumbnailUrl);
	}

	// 다이어리 삭제
	@Transactional
	public void deleteDiary(Long userId, Long diaryId) {
		Diary diary = findDiaryOrThrow(diaryId);
		validateOwner(diary, userId);
		mediaService.deleteMediaByDiaryId(diaryId);
		diaryRepository.delete(diary);
	}

	private Diary findDiaryOrThrow(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(NotFoundDiaryException::new);
	}

	// Page용 매핑 메서드
	private Page<DiaryResponseDto> mapToDtoPage(Page<Diary> page) {
		List<DiaryResponseDto> content = getDiaryResponsesWithMedia(page.getContent());
		return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
	}

	// Slice용 매핑 메서드
	private Slice<DiaryResponseDto> mapToDtoSlice(Slice<Diary> slice) {
		List<DiaryResponseDto> content = getDiaryResponsesWithMedia(slice.getContent());
		return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
	}

	// 다이어리 + 미디어 같이 반환
	private List<DiaryResponseDto> getDiaryResponsesWithMedia(List<Diary> diaries) {
		if (diaries.isEmpty()) {
			return List.of();
		}

		List<Long> diaryIds = diaries.stream()
			.map(Diary::getDiaryId)
			.toList();

		Map<Long, List<Media>> mediaMap = mediaService.getMediaMapByDiaryIds(diaryIds);

		return diaries.stream()
			.map(diary -> DiaryResponseDto.of(
				diary,
				mediaMap.getOrDefault(diary.getDiaryId(), List.of())
			))
			.toList();
	}

	// 다이어리 작성자 본인 체크
	private void validateOwner(Diary diary, Long userId) {
		if (!diary.isOwner(userId)) {
			throw new OwnerAccessDeniedException();
		}
	}

	// 다이어리 목록 조회 시 권한 체크
	private List<VisibilityType> determineAccessibleVisibilities(Long userId, Long targetUserId) {
		if (userId.equals(targetUserId)) {
			return List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE, VisibilityType.FOLLOWER);
		}

		if (followRepository.existsByFollowerIdAndFollowingId(userId, targetUserId)) {
			return List.of(VisibilityType.PUBLIC, VisibilityType.FOLLOWER);
		}

		return List.of(VisibilityType.PUBLIC);
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
				&& !followRepository.existsByFollowerIdAndFollowingId(userId, diary.getUserId())) {
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

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getMyDiariesByCursor(Long userId, VisibilityType visibilityType,
		Long cursorId, int size) {
		List<VisibilityType> visibilities =
			visibilityType == null ? List.of(VisibilityType.PUBLIC, VisibilityType.PRIVATE, VisibilityType.FOLLOWER) :
				List.of(visibilityType);

		Slice<Diary> diaries = diaryRepository.findByUserIdAndVisibilityInAndCursorId(
			userId,
			visibilities,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);

		Slice<DiaryResponseDto> dtoSlice = mapToDtoSlice(diaries);

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

		Slice<DiaryResponseDto> dtoSlice = mapToDtoSlice(diaries);

		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;

		return PageResponse.of(dtoSlice, nextCursor);
	}
}
