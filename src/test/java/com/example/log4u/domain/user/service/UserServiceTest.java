package com.example.log4u.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileMakeRequestDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
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

	@Mock
	private FollowRepository followRepository;

	@Test
	@DisplayName("내 프로필을 생성하고 저장해야 한다.")
	void shouldCreateAndSaveMyUserProfile() {
		Long userId = 1L;
		UserProfileMakeRequestDto userProfileMakeRequestDto = new UserProfileMakeRequestDto(
			"test nickname",
			"test msg",
			"test img"
		);

		User mockUser = mock(User.class);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		userService.createMyProfile(userId, userProfileMakeRequestDto);

		verify(mockUser).createMyProfile(userProfileMakeRequestDto);
		verify(userRepository).save(mockUser);
	}

	@Test
	@DisplayName("내 프로필을 업데이트 하고 저장해야 한다")
	void shouldUpdateAndSaveMyProfile() {
		Long userId = 1L;
		UserProfileUpdateRequestDto userProfileUpdateRequestDto = new UserProfileUpdateRequestDto(
			"test msg",
			"test img"
		);

		User mockUser = mock(User.class);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		userService.updateMyProfile(userId, userProfileUpdateRequestDto);

		verify(mockUser).updateMyProfile(userProfileUpdateRequestDto);
		verify(userRepository).save(mockUser);
	}

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
	
	@Test
	@DisplayName("검색 결과가 없으면 빈 리스트를 반환해야 한다")
	void shouldReturnEmptyListWhenNoSearchResults() {
		// given
		String nickname = "nonexistent";
		Long cursorId = null;
		int size = 10;

		List<User> emptyList = List.of();
		Slice<User> emptySlice = new SliceImpl<>(emptyList, PageRequest.of(0, size), false);

		// Mock 설정
		when(userRepository.searchUsersByCursor(
			eq(nickname),
			eq(Long.MAX_VALUE),
			ArgumentMatchers.any(PageRequest.class)))
			.thenReturn(emptySlice);

		// when
		PageResponse<UserProfileResponseDto> result = userService.searchUsersByCursor(nickname, cursorId, size);

		// then
		assertNotNull(result);
		assertTrue(result.list().isEmpty());
		assertNull(result.pageInfo().nextCursor());

		// 메서드 호출 확인
		verify(userRepository).searchUsersByCursor(
			eq(nickname),
			eq(Long.MAX_VALUE),
			ArgumentMatchers.any(PageRequest.class));

		// 팔로워/팔로잉 수 조회 메서드가 호출되지 않아야 함
		verify(followRepository, never()).countByTargetId(anyLong());
		verify(followRepository, never()).countByInitiatorId(anyLong());
	}
}