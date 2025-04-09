package com.example.log4u.domain.user.dto;

import com.example.log4u.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserProfileResponseDto(
	Long userId,
	String name,
	String nickname,
	String statusMessage,
	String profileImage,
	Long followers,
	Long followings
) {

	public static UserProfileResponseDto fromUser(
		User user,
		Long followers,
		Long followings
	) {
		return builder()
			.userId(user.getUserId())
			.name(user.getName())
			.nickname(user.getNickname())
			.statusMessage(user.getStatusMessage())
			.profileImage(user.getProfileImage())
			.followers(followers)
			.followings(followings)
			.build();
	}
}
