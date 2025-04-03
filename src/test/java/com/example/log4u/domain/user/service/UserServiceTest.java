package com.example.log4u.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;
import com.example.log4u.fixture.UserFixture;

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

	@Test
	@DisplayName("유저가 존재하면 유저 엔티티를 반환한다.")
	void shouldReturnUserWhenUserExists() {
		Long userId = 1L;
		User user = UserFixture.createUserFixture();

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		User result = userService.getUserById(userId);

		verify(userRepository).findById(userId);
		assertNotNull(result);
		assertEquals(user, result);
	}

	@Test
	@DisplayName("유저가 없으면 USER NOT FOUND 예외가 발생한다.")
	void shouldThrowExceptionWhenUserNotFound() {
		Long userId = 1L;
		given(userRepository.findById(userId)).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
		verify(userRepository).findById(userId);
	}

}