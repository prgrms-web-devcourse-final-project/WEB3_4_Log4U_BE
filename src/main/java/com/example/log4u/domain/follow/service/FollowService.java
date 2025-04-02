package com.example.log4u.domain.follow.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.follow.entitiy.Follow;
import com.example.log4u.domain.follow.exception.FollowNotFoundException;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
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

	public Long getFollowerCount() {
		//TODO: 구현
		return 0L;
	}

	public Long getFollowingCount() {
		//TODO: 구현
		return 0L;
	}
}
