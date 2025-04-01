package com.example.log4u.common.oauth2.dto;

import java.util.Map;

import com.example.log4u.domain.user.entity.SocialType;

public class NaverResponseDto implements OAuth2Response {

	private final Map<String, Object> attribute;

	public NaverResponseDto(Map<String, Object> attribute) {
		this.attribute = (Map<String, Object>)attribute.get("response");
	}

	@Override
	public SocialType getSocialType() {
		return SocialType.NAVER;
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		return attribute.get("email").toString();
	}

	@Override
	public String getName() {
		return attribute.get("name").toString();
	}

	@Override
	public String getNickname() {
		return attribute.get("nickname").toString();
	}

	@Override
	public String getProfileImageUrl() {
		return attribute.get("profile_image").toString();
	}

}
