package com.example.log4u.common.oauth2.dto;

import java.util.Map;

import com.example.log4u.domain.user.entity.SocialType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleResponseDto implements OAuth2Response {

	private final Map<String, Object> attribute;

	@Override
	public SocialType getSocialType() {
		return SocialType.GOOGLE;
	}

	@Override
	public String getProviderId() {
		return attribute.get("sub").toString();
	}

	@Override
	public String getEmail() {
		return attribute.get("email").toString();
	}

	// 구글은 이름이 닉네임
	@Override
	public String getName() {
		return attribute.get("name").toString();
	}

	@Override
	public String getNickname() {
		return "";
	}

	@Override
	public String getProfileImageUrl() {
		return attribute.get("picture").toString();
	}

}
