package com.example.log4u.domain.user.dto;

import com.example.log4u.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserThumbnailResponseDto(
	Long userId,
	String nickname,
	String thumbnailUrl
) {
	public static UserThumbnailResponseDto of(User user) {
		return UserThumbnailResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.thumbnailUrl(user.getProfileImage())
			.build();
	}
}
