package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.service.FollowService;
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
	private final FollowService followService;

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = getUserById(userId);

		return UserProfileResponseDto.fromUser(
			me,
			0L,
			followService.getFollowerCount(userId),
			followService.getFollowingCount(userId)
		);
	}

	public UserProfileResponseDto getUserProfile(String nickname) {
		User user = getUserByNickname(nickname);

		return UserProfileResponseDto.fromUser(
			user,
			0L,
			followService.getFollowerCount(user.getUserId()),
			followService.getFollowingCount(user.getUserId())
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
		// 업데이트
		User user = getUserById(userId);
		user.updateProfile(userProfileUpdateRequestDto);
		userRepository.save(user);

		return getMyProfile(userId);
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
