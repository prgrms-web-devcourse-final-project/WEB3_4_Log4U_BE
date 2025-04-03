package com.example.log4u.domain.user.dto;

public record UserProfileMakeRequestDto(
	String nickname,
	String statusMessage,
	String profileImage
) {
}
