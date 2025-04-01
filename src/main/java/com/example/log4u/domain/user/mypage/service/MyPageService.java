package com.example.log4u.domain.user.mypage.service;

import static com.example.log4u.domain.diary.service.DiaryService.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.follow.repository.FollowQuerydsl;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

	private final DiaryService diaryService;
	private final FollowQuerydsl followQuerydsl;

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getMyDiariesByCursor(Long userId, Long cursorId) {
		return diaryService.getDiariesByCursor(userId, userId,
			cursorId); // 일단 로직 자체가 구현 그대로 돼있길래 그대로 갖다 썼는데 이래도 구조가 괜찮을 지 모르겠습니다.
	}

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getLikeDiariesByCursor(Long userId, Long cursorId) {
		return diaryService.getLikeDiariesByCursor(userId, userId,
			cursorId);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getMyFollowers(Long userId, Long cursorId) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowerSliceByUserId(
			userId,
			cursorId,
			PageRequest.of(0, CURSOR_PAGE_SIZE));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getMyFollowings(Long userId, Long cursorId) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowingSliceByUserId(
			userId,
			cursorId,
			PageRequest.of(0, CURSOR_PAGE_SIZE));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}
}
