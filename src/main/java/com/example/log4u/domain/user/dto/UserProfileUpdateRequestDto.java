package com.example.log4u.domain.user.dto;

public record UserProfileUpdateRequestDto(
	// 현재는 위 두가지만 변경이 가능하다고 생각해서 두개만 추가했습니다.
	String statusMessage,
	String profileImage
) {

}
