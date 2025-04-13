package com.example.log4u.fixture;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.log4u.domain.user.entity.SocialType;
import com.example.log4u.domain.user.entity.User;

public class UserFixture {

	public static User createUserFixture() {
		return User.builder()
			.name("test" + UUID.randomUUID())
			.nickname("testUser" + UUID.randomUUID())
			.providerId("123" + UUID.randomUUID())
			.email("test" + UUID.randomUUID() + "@example.com")
			.socialType(SocialType.KAKAO)
			.role("ROLE_USER")
			.statusMessage(LocalDateTime.now().toString())
			.isPremium(false)
			.build();
	}

	public static User createUserFixtureWithProfileImage(Long userId) {
		return User.builder()
			.userId(userId)
			.name("test" + UUID.randomUUID())
			.nickname("testUser" + UUID.randomUUID())
			.providerId("123" + UUID.randomUUID())
			.email("test" + UUID.randomUUID() + "@example.com")
			.socialType(SocialType.KAKAO)
			.role("ROLE_USER")
			.statusMessage(LocalDateTime.now().toString())
			.profileImage(UUID.randomUUID().toString())
			.build();
	}

	public static User createUserFixtureWithNickname(String nickname) {
		return User.builder()
			.name("name" + nickname)
			.nickname(nickname)
			.providerId("100 " + nickname)
			.email("test" + nickname + "@example.com")
			.socialType(SocialType.KAKAO)
			.role("ROLE_USER")
			.statusMessage("상태 메시지 " + nickname)
			.isPremium(false)
			.build();
	}

	public static User createPremiumUserFixture(String nickname) {
		return User.builder()
			.name("premium" + nickname)
			.nickname("premiumUser" + nickname)
			.providerId("1000L" + nickname)
			.email("premium" + nickname + "@example.com")
			.socialType(SocialType.KAKAO)
			.role("ROLE_USER")
			.statusMessage("프리미엄 사용자")
			.isPremium(true)
			.build();
	}
}

