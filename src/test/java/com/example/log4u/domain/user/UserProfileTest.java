package com.example.log4u.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.repository.UserRepository;
import com.example.log4u.domain.user.service.UserService;
import com.example.log4u.fixture.UserFixture;

@DisplayName("유저 프로필 서비스-레포지토리 테스트")
@SpringBootTest
class UserProfileTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("내 프로필이 업데이트 되어야 한다.")
	void updateMyProfileSuccess() {
		User previous = userRepository.save(UserFixture.createUserFixture());
		UserProfileUpdateRequestDto userProfileUpdateRequestDto =
			new UserProfileUpdateRequestDto(
				"업데이트이미지경로",
				"업데이트상태메시지"
			);

		UserProfileResponseDto userProfileResponseDto =
			userService.updateMyProfile(previous.getUserId(), userProfileUpdateRequestDto);

		assertEquals(userProfileUpdateRequestDto.profileImage(), userProfileResponseDto.profileImage());
		assertEquals(userProfileUpdateRequestDto.statusMessage(), userProfileResponseDto.statusMessage());
	}
}
