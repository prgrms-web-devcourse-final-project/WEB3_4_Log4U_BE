package com.example.log4u.domain.user.dto;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserProfileResponseDto(
	String name,
	String nickname,
	String statusMessage,
	String profileImage,
	Long followers,
	Long followings,
	PageResponse<DiaryResponseDto> diaries
) {

	public static UserProfileResponseDto fromUser(
		User user,
		Long followers,
		Long followings,
		PageResponse<DiaryResponseDto> diaries
	) {
		return builder()
			.name(user.getName())
			.nickname(user.getNickname())
			.statusMessage(user.getStatusMessage())
			.profileImage(user.getProfileImage())
			.followers(followers)
			.followings(followings)
			.diaries(diaries)
			.build();
	}
}
