package com.example.log4u.domain.follow.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.follow.entitiy.Follow;
import com.example.log4u.domain.follow.exception.FollowNotFoundException;
import com.example.log4u.domain.follow.repository.FollowQuerydsl;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final int defaultPageSize = 6;
	private final FollowRepository followRepository;
	private final FollowQuerydsl followQuerydsl;
	private final UserService userService;

	@Transactional
	public void createFollow(Long initiatorId, String nickname) {
		validateTargetUser(nickname);
		followRepository.save(Follow.of(
			initiatorId,
			userService.getUserIdByNickname(nickname)));
	}

	@Transactional
	public void deleteFollow(Long userId, String nickname) {
		Long targetId = userService.getUserIdByNickname(nickname);
		validateFollow(userId, targetId);
		followRepository.deleteByInitiatorIdAndTargetId(userId, targetId);
	}

	private void validateTargetUser(String nickname) {
		// USER NOT FOUND EXCEPTION
		userService.getUserByNickname(nickname);
	}

	private void validateFollow(Long userId, Long targetId) {
		if (!followRepository.existsByInitiatorIdAndTargetId(userId, targetId)) {
			throw new FollowNotFoundException();
		}
	}

	/**
	 * 나를 팔로우 하는 사람 수 조회
	 * */
	@Transactional(readOnly = true)
	public Long getFollowerCount(Long userId) {
		return followRepository.countByTargetId(userId);
	}

	/**
	 * 내가 팔로우하는 사람 수 조회
	 * */
	@Transactional(readOnly = true)
	public Long getFollowingCount(Long userId) {
		return followRepository.countByInitiatorId(userId);
	}

	@Transactional(readOnly = true)
	public boolean existsFollow(Long initiatorId, Long targetId) {
		return followRepository.existsByInitiatorIdAndTargetId(initiatorId, targetId);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getFollowersByNickname(String nickname, Long cursorId,
		String keyword) {
		Long userId = userService.getUserIdByNickname(nickname);

		return getFollowersByUserId(userId, cursorId, keyword);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getFollowingsByNickname(String nickname, Long cursorId,
		String keyword) {
		Long userId = userService.getUserIdByNickname(nickname);

		return getFollowingsByUserId(userId, cursorId, keyword);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getFollowersByUserId(Long userId, Long cursorId, String keyword) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowerSliceByUserId(
			userId,
			cursorId,
			keyword,
			PageRequest.of(0, defaultPageSize));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getFollowingsByUserId(Long userId, Long cursorId, String keyword) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowingSliceByUserId(
			userId,
			cursorId,
			keyword,
			PageRequest.of(0, defaultPageSize));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}
}
