package com.example.log4u.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserProfileMakeRequestDto(
	@NotEmpty
	@Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
	String nickname,
	String statusMessage,
	String profileImage
) {
}
