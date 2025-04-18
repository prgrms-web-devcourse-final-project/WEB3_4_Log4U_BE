package com.example.log4u.domain.user.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileMakeRequestDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final DiaryRepository diaryRepository;

	public UserProfileResponseDto getMyProfile(Long userId) {
		User me = getUserById(userId);

		return UserProfileResponseDto.fromUser(
			me,
			diaryRepository.countByUserId(userId),
			followRepository.countByTargetId(userId),
			followRepository.countByInitiatorId(userId)
		);
	}

	public UserProfileResponseDto getUserProfileById(Long id) {
		User target = getUserById(id);

		return UserProfileResponseDto.fromUser(
			target,
			diaryRepository.countByUserId(id),
			followRepository.countByTargetId(id),
			followRepository.countByInitiatorId(id)
		);
	}

	/**
	 * 소셜 로그인 후 DB에 임시 USER 객체가 ROLE_GUEST 상태로 존재함<br>
	 * 프로필 생성 시 DB속 임시 USER 객체 가져와서<br>
	 * 업데이트 하는 방식으로 프로필 생성
	 * */
	@Transactional
	public void createMyProfile(Long userId, UserProfileMakeRequestDto userProfileMakeRequestDto) {
		User user = getUserById(userId);
		user.createMyProfile(userProfileMakeRequestDto);
		userRepository.save(user);
	}

	public NicknameValidationResponseDto validateNickname(String nickname) {
		return new NicknameValidationResponseDto(
			!userRepository.existsByNickname(nickname));
	}

	@Transactional
	public void updateMyProfile(
		Long userId,
		UserProfileUpdateRequestDto userProfileUpdateRequestDto
	) {
		// 업데이트
		User user = getUserById(userId);
		user.updateMyProfile(userProfileUpdateRequestDto);
		userRepository.save(user);
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

	@Transactional(readOnly = true)
	public PageResponse<UserProfileResponseDto> searchUsersByCursor(
		String nickname,
		Long cursorId,
		int size
	) {
		Slice<User> userSlice = userRepository.searchUsersByCursor(
			nickname,
			cursorId != null ? cursorId : Long.MAX_VALUE,
			PageRequest.of(0, size)
		);

		List<UserProfileResponseDto> content = userSlice.getContent().stream()
			.map(user -> {
				Long userId = user.getUserId();
				Integer diaryCount = diaryRepository.countByUserId(userId);
				Long followers = followRepository.countByTargetId(userId);
				Long followings = followRepository.countByInitiatorId(userId);
				return UserProfileResponseDto.fromUser(user, diaryCount, followers, followings);
			})
			.toList();

		// 다음 커서 ID 계산
		Long nextCursor = content.isEmpty() ? null : content.get(content.size() - 1).userId();

		return PageResponse.of(
			new SliceImpl<>(content, userSlice.getPageable(), userSlice.hasNext()),
			nextCursor
		);
	}
}
