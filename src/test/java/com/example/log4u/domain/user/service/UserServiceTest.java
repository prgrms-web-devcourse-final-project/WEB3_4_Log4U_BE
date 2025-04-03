package com.example.log4u.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.repository.UserRepository;

@DisplayName("유저 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName(("닉네임 중복 검사 결과 이용 가능"))
	void validateNicknameAvailableTrue() {
		String nickname = "available";

		given(userRepository.existsByNickname(nickname)).willReturn(true);
		NicknameValidationResponseDto result = userService.validateNickname(nickname);

		verify(userRepository).existsByNickname(nickname);
		assertTrue(result.available());
	}

	@Test
	@DisplayName(("닉네임 중복 검사 결과 이용 불가능"))
	void validateNicknameAvailableFalse() {
		String nickname = "unavailable";

		given(userRepository.existsByNickname(nickname)).willReturn(false);
		NicknameValidationResponseDto result = userService.validateNickname(nickname);

		verify(userRepository).existsByNickname(nickname);
		assertFalse(result.available());
	}

}