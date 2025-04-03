package com.example.log4u.domain.follow;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

	private static final String WRONG_TARGET = "nonExistUser";
	private static final String TARGET = "targetUser";

	@Test
	@DisplayName("테스트용 DB 연결 확인")
	void checkDatabaseConnection() {
		try (Connection connection = DriverManager.getConnection(
			"jdbc:mysql://localhost:3307/log4u",
			"dev",
			"devcos4-team08")) {
			assertFalse(connection.isClosed());
		} catch (SQLException e) {
			fail("데이터베이스 연결 실패: " + e.getMessage());
		}
	}

	@Test
	@Transactional
	@DisplayName("팔로우 시 타겟 유저가 없어 USER NOT FOUND 예외 발생")
	void createFollowFailureWithUserNotFound() {
		User initiator = UserFixture.createUserFixture();
		final Long initiatorId = initiator.getUserId();

		assertThrows(UserNotFoundException.class,
			() -> followService.createFollow(initiatorId, WRONG_TARGET));
	}

	@Test
	@Transactional
	@DisplayName("팔로우가 되어야 한다.")
	void createFollowSuccess() {
		Long[] ids = saveOneFollow();
		final Long initiatorId = ids[0];
		final Long targetId = ids[1];

		assertTrue(followRepository.existsByInitiatorIdAndTargetId(initiatorId, targetId));
	}

	@Test
	@Transactional
	@DisplayName("팔로우 취소가 되어야한다.")
	void deleteFollowSuccess() {
		Long[] ids = saveOneFollow();
		final Long initiatorId = ids[0];

		followService.deleteFollow(initiatorId, TARGET);

		assertThrows(FollowNotFoundException.class,
			() -> followService.deleteFollow(initiatorId, TARGET));
	}

	@Test
	@Transactional
	@DisplayName("팔로우한 정보가 없어 FollowNotFound 발생")
	void deleteFollowFailureWithFollowNotFound() {
		User initiator = UserFixture.createUserFixture();
		User target = UserFixture.createUserFixtureWithNickname(TARGET);

		final Long initiatorId = initiator.getUserId();
		userRepository.save(target);

		assertThrows(FollowNotFoundException.class,
			() -> followService.deleteFollow(initiatorId, TARGET));
	}

	private Long[] saveOneFollow() {
		User initiator = UserFixture.createUserFixture();
		User target = UserFixture.createUserFixtureWithNickname(TARGET);

		initiator = userRepository.save(initiator);
		target = userRepository.save(target);

		Follow follow = Follow.of(
			initiator.getUserId(),
			target.getUserId()
		);

		followRepository.save(follow);

		return new Long[] {initiator.getUserId(), target.getUserId()};
	}
}
