package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final DiaryRepository diaryRepository;

	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			UserNotFoundException::new
		);
	}

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = userRepository.findById(userId).orElseThrow(
			UserNotFoundException::new
		);

		return new UserProfileResponseDto.Builder()
			.fromUser(me)
			.build();
	}

	public UserProfileResponseDto getUserProfile(Long userId) {
		User me = userRepository.findById(userId).orElseThrow(
			UserNotFoundException::new
		);
		return
	}


	public NicknameValidationResponseDto validateNickname(String nickname) {
		return new NicknameValidationResponseDto(
			userRepository.findByNickname(nickname).isPresent());
	}

	public UserProfileResponseDto updateMyProfile(Long userId, UserProfileUpdateRequestDto userProfileUpdateRequestDto) {

	}
}
