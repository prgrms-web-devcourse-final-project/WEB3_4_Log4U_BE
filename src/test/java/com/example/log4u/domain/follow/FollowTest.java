package com.example.log4u.domain.follow;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.log4u.domain.follow.entitiy.Follow;
import com.example.log4u.domain.follow.exception.FollowNotFoundException;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.follow.service.FollowService;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;
import com.example.log4u.fixture.UserFixture;

import jakarta.transaction.Transactional;

@DisplayName("팔로우 통합 테스트(시큐리티 제외)")
@SpringBootTest
class FollowTest {

	@Autowired
	private FollowService followService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	private static final String WRONG_TARGET = "nonexistuser";
	private static final String TARGET = "targetUser";

	@Test
	@Transactional
	@DisplayName("팔로우 시 유저가 없어 USER NOT FOUND 예외 발생")
	void createFollowFailureWithUserNotFound() {
		User user = UserFixture.createUserFixture();
		userRepository.save(user);

		assertThrows(UserNotFoundException.class,
			() -> followService.createFollow(user.getUserId(), WRONG_TARGET));
	}

	@Test
	@Transactional
	@DisplayName("팔로우가 되어야 한다.")
	void createFollowSuccess() {
		String targetUserNickname = "targetUser";
		User initiator = UserFixture.createUserFixture();
		User target = UserFixture.createUserFixtureWithNickname(targetUserNickname);

		initiator = userRepository.save(initiator);
		target = userRepository.save(target);

		followService.createFollow(initiator.getUserId(), targetUserNickname);

		assertTrue(followRepository.existsByInitiatorIdAndTargetId(initiator.getUserId(), target.getUserId()));
	}

	@Test
	@Transactional
	@DisplayName("팔로우 취소가 되어야한다.")
	void deleteFollowSuccess() {
		Long userId = saveOneFollow();

		followService.deleteFollow(userId, TARGET);

		assertThrows(FollowNotFoundException.class,
			() -> followService.deleteFollow(userId, TARGET));
	}

	@Test
	@Transactional
	@DisplayName("팔로우한 정보가 없어 FollowNotFound 발생")
	void deleteFollowFailureWithFollowNotFound() {
		User user = UserFixture.createUserFixture();
		user = userRepository.save(user);
		final Long userId = user.getUserId();

		User target = UserFixture.createUserFixtureWithNickname(TARGET);
		userRepository.save(target);

		assertThrows(FollowNotFoundException.class,
			() -> followService.deleteFollow(userId, TARGET));
	}

	private Long saveOneFollow() {
		String targetUserNickname = "targetUser";
		User initiator = UserFixture.createUserFixture();
		User target = UserFixture.createUserFixtureWithNickname(targetUserNickname);

		initiator = userRepository.save(initiator);
		target = userRepository.save(target);

		Follow follow = Follow.of(
			initiator.getUserId(),
			target.getUserId()
		);

		followRepository.save(follow);

		return initiator.getUserId();
	}
}
