package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
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
	private final FollowService followService;
	private final DiaryService diaryService;

	private static final int DEFAULT_DIARIES_SIZE = 9;

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = getUserById(userId);
		PageResponse<DiaryResponseDto> diaries =
			diaryService.getDiariesByCursor(
				userId,
				userId,
				null,
				DEFAULT_DIARIES_SIZE);

		return UserProfileResponseDto.fromUser(
			me,
			followService.getFollowerCount(userId),
			followService.getFollowingCount(userId),
			diaries
		);
	}

	public UserProfileResponseDto getUserProfile(Long userId, String nickname) {
		User target = getUserByNickname(nickname);
		final Long targetId = target.getUserId();

		PageResponse<DiaryResponseDto> diaries =
			diaryService.getDiariesByCursor(
				userId,
				targetId,
				null,
				DEFAULT_DIARIES_SIZE);

		return UserProfileResponseDto.fromUser(
			target,
			followService.getFollowerCount(targetId),
			followService.getFollowingCount(targetId),
			diaries
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
