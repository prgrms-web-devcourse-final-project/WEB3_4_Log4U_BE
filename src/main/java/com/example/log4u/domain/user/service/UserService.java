package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final DiaryRepository diaryRepository;
	private final FollowRepository followRepository;

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = getUserById(userId);

		return UserProfileResponseDto.fromUser(
			me,
			0L,
			0L,
			0L,
			null
		);
	}

	public UserProfileResponseDto getUserProfile(String nickname) {
		User user = getUserByNickname(nickname);

		return UserProfileResponseDto.fromUser(
			user,
			0L,
			0L,
			0L,
			null
		);
	}

	public NicknameValidationResponseDto validateNickname(String nickname) {
		return new NicknameValidationResponseDto(
			userRepository.findByNickname(nickname).isPresent());
	}

	public UserProfileResponseDto updateMyProfile(
		Long userId,
		UserProfileUpdateRequestDto userProfileUpdateRequestDto
	) {
		User user = getUserById(userId);
		return null;
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			UserNotFoundException::new
		);
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname).orElseThrow(
			UserNotFoundException::new
		);
	}

	public Long getUserIdByNickname(String nickname) {
		return userRepository.findByNickname(nickname).orElseThrow(
			UserNotFoundException::new
		).getUserId();
	}
}
