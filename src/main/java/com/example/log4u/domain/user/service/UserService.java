package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.service.DiaryService;
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
	private final FollowRepository followRepository;
	private final DiaryService diaryService;

	private static final int DEFAULT_DIARIES_SIZE = 9;

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = getUserById(userId);

		return UserProfileResponseDto.fromUser(
			me,
			followRepository.countByTargetId(userId),
			followRepository.countByInitiatorId(userId)
		);
	}

	public UserProfileResponseDto getUserProfile(String nickname) {
		User target = getUserByNickname(nickname);
		final Long targetId = target.getUserId();

		return UserProfileResponseDto.fromUser(
			target,
			followRepository.countByTargetId(targetId),
			followRepository.countByInitiatorId(targetId)
		);
	}

	public NicknameValidationResponseDto validateNickname(String nickname) {
		return new NicknameValidationResponseDto(
			userRepository.existsByNickname(nickname));
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
