package com.example.log4u.domain.diary.facade;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.hashtag.service.HashtagService;
import com.example.log4u.domain.like.service.LikeService;
import com.example.log4u.domain.map.service.MapService;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.media.service.MediaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiaryFacade {

	private final DiaryService diaryService;
	private final MediaService mediaService;
	private final MapService mapService;
	private final LikeService likeService;
	private final HashtagService hashtagService;

	/**
	 * 다이어리 생성 use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. mediaService: 섬네일 이미지 url 생성<br>
	 * 2. diaryService: 다이어리 생성<br>
	 * 2. mediaService: 해당 다이어리의 이미지 저장<br>
	 * 3. mapService: 해당 구역 카운트 증가
	 * */
	@Transactional
	public void createDiary(Long userId, DiaryRequestDto request) {
		String thumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		Diary diary = diaryService.saveDiary(userId, request, thumbnailUrl);
		mediaService.saveMedia(diary.getDiaryId(), request.mediaList());
		hashtagService.saveOrUpdateHashtag(diary.getDiaryId(), request.hashtagList());
		mapService.increaseRegionDiaryCount(request.location().latitude(), request.location().longitude());
	}

	/**
	 * 다이어리 삭제 use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. diaryService: 다이어리 검증
	 * 2. mediaService: 해당 다이어리 이미지 삭제<br>
	 * 3. diaryService: 다이어리 삭제<br>
	 * */
	@Transactional
	public void deleteDiary(Long userId, Long diaryId) {
		Diary diary = diaryService.getDiaryAfterValidateOwnership(diaryId, userId);
		mediaService.deleteMediaByDiaryId(diaryId);
		hashtagService.deleteHashtagsByDiaryId(diaryId);
		diaryService.deleteDiary(diary);
	}

	/**
	 * 다이어리 수정 use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. diaryService: 다이어리 검증<br>
	 * 2. mediaService: 해당 다이어리 이미지 삭제<br>
	 * 3. diaryService: 다이어리 수정
	 * */
	@Transactional
	public void updateDiary(Long userId, Long diaryId, DiaryRequestDto request) {
		Diary diary = diaryService.getDiaryAfterValidateOwnership(diaryId, userId);
		mediaService.updateMediaByDiaryId(diary.getDiaryId(), request.mediaList());
		hashtagService.saveOrUpdateHashtag(diary.getDiaryId(), request.hashtagList());

		String newThumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		diaryService.updateDiary(diary, request, newThumbnailUrl);
	}

	/**
	 * 다이어리 단건 조회 use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. diaryService: 공개 범위 검증 후 다이어리 조회<br>
	 * 2. likeService: 좋아요 기록 조회<br>
	 * 3. mediaService: 해당 다이어리의 이미지 조회<br>
	 * 4. 모든 정보 조합 후 dto 변환 해 반환
	 * */
	@Transactional(readOnly = true)
	public DiaryResponseDto getDiary(Long userId, Long diaryId) {
		Diary diary = diaryService.getDiaryAfterValidateAccess(diaryId, userId);
		boolean isLiked = likeService.isLiked(userId, diaryId);
		List<Media> media = mediaService.getMediaByDiaryId(diary.getDiaryId());

		// 해시태그 조회 추가
		List<String> hashtags = hashtagService.getHashtagsByDiaryId(diary.getDiaryId());

		return DiaryResponseDto.of(diary, media, hashtags, isLiked);
	}

	/**
	 * 다이어리 목록 조회 By UserId use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. diaryService : DiaryResponseDto Slice 객체 조회<br>
	 * 2. nextCursor 정보 생성<br>
	 * 3. PageResponse 조합 후 반환
	 * */
	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getDiariesByCursor(
		Long userId,
		Long targetUserId,
		Long cursorId,
		int size
	) {
		Slice<DiaryResponseDto> dtoSlice = diaryService.getDiaryResponseDtoSlice(userId, targetUserId, cursorId, size);
		// 다음 커서 ID 계산
		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;
		return PageResponse.of(dtoSlice, nextCursor);
	}

	/**
	 * 다이어리 검색 목록 조회 By Cursor use case
	 * <ul><li>호출 과정</li></ul>
	 * 1. diaryService : DiaryResponseDto Slice 객체 조회<br>
	 * 2. nextCursor 정보 생성<br>
	 * 3. PageResponse 조합 후 반환<br>
	 * */
	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> searchDiariesByCursor(
		String keyword,
		SortType sort,
		Long cursorId,
		int size
	) {
		Slice<DiaryResponseDto> dtoSlice = diaryService.searchDiariesByCursor(keyword, sort, cursorId, size);
		// 다음 커서 ID 계산
		Long nextCursor = !dtoSlice.isEmpty() ? dtoSlice.getContent().getLast().diaryId() : null;
		return PageResponse.of(dtoSlice, nextCursor);
	}
}