package com.example.log4u.domain.user.dto;

import com.example.log4u.common.oauth2.dto.OAuth2Response;
import com.example.log4u.domain.user.entity.SocialType;
import com.example.log4u.domain.user.entity.User;

public record UserCreateRequestDto(
	SocialType socialType,
	String providerId,
	Long userId,
	String name,
	String email,
	String nickname,
	String profileImage,
	String role
) {
	public static User toEntity(UserCreateRequestDto userCreateRequestDto) {
		return User.builder()
			.socialType(userCreateRequestDto.socialType)
			.providerId(userCreateRequestDto.providerId)
			.name(userCreateRequestDto.name)
			.email(userCreateRequestDto.email)
			.nickname(userCreateRequestDto.nickname)
			.profileImage(userCreateRequestDto.profileImage)
			.role(userCreateRequestDto.role)
			.build();
	}

	public static UserCreateRequestDto fromOAuth2Response(OAuth2Response oAuth2Response, Long userId, String role) {
		return new UserCreateRequestDto(
			oAuth2Response.getSocialType(),
			oAuth2Response.getProviderId(),
			userId,
			oAuth2Response.getName(),
			oAuth2Response.getEmail(),
			oAuth2Response.getNickname(),
			oAuth2Response.getProfileImage(),
			role
		);
	}

	public static UserCreateRequestDto fromEntity(User user) {
		return new UserCreateRequestDto(
			user.getSocialType(),
			user.getProviderId(),
			user.getUserId(),
			user.getName(),
			user.getEmail(),
			user.getNickname(),
			user.getProfileImage(),
			user.getRole()
		);
	}
}
