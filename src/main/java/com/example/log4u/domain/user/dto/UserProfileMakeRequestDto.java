package com.example.log4u.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserProfileMakeRequestDto(
	@NotEmpty String nickname,
	String statusMessage,
	String profileImage
) {
}
