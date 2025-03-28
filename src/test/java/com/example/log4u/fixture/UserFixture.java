package com.example.log4u.fixture;

import com.example.log4u.domain.user.entity.SocialType;
import com.example.log4u.domain.user.entity.User;

public class UserFixture {

	public static User createUserFixture() {
		return User.builder()
			.userId(1L)
			.nickname("testUser")
			.providerId(123L)
			.email("test@example.com")
			.socialType(SocialType.KAKAO)
			.statusMessage("상태 메시지")
			.isPremium(false)
			.build();
	}

	public static User createUserFixture(Long userId) {
		return User.builder()
			.userId(userId)
			.nickname("testUser" + userId)
			.providerId(100L + userId)
			.email("test" + userId + "@example.com")
			.socialType(SocialType.KAKAO)
			.statusMessage("상태 메시지 " + userId)
			.isPremium(false)
			.build();
	}

	public static User createPremiumUserFixture(Long userId) {
		return User.builder()
			.userId(userId)
			.nickname("premiumUser" + userId)
			.providerId(1000L + userId)
			.email("premium" + userId + "@example.com")
			.socialType(SocialType.KAKAO)
			.statusMessage("프리미엄 사용자")
			.isPremium(true)
			.build();
	}
}

