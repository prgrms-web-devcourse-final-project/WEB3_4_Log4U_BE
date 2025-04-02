package com.example.log4u.domain.user.dto;

public record UserProfileUpdateRequestDto(
	String profileImage,
	String statusMessage
) {

}
