package com.example.log4u.common.oauth2.dto;

import com.example.log4u.domain.user.entity.User;

public record UserCreateRequestDto (
	String provider,
	String providerId,
	Long userId,
	String name,
	String email,
	String nickname,
	String profileImageUrl,
	String role
){
	public static User toEntity(UserCreateRequestDto userCreateRequestDto){
		return User.builder()
			.provider(userCreateRequestDto.provider)
			.providerId(userCreateRequestDto.providerId)
			.name(userCreateRequestDto.name)
			.email(userCreateRequestDto.email)
			.nickname(userCreateRequestDto.nickname)
			.profileImageUrl(userCreateRequestDto.profileImageUrl)
			.role(userCreateRequestDto.role)
			.build();
	}

	public static UserCreateRequestDto fromOAuth2Response(OAuth2Response oAuth2Response, Long userId, String role){
		return new UserCreateRequestDto(
			oAuth2Response.getProvider(),
			oAuth2Response.getProviderId(),
			userId,
			oAuth2Response.getName(),
			oAuth2Response.getEmail(),
			oAuth2Response.getNickname(),
			oAuth2Response.getProfileImageUrl(),
			role
		);
	}
}
