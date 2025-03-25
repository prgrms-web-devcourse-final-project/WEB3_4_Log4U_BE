package com.example.log4u.common.oauth2.dto;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponseDto implements OAuth2Response{

	private final Map<String, Object> attribute;


	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return attribute.get("sub").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> account = (Map<String, Object>)attribute.get("kakao_account");
		return (String)account.get("email");
	}

	@Override
	public String getName() {
		Map<String, Object> account = (Map<String, Object>)attribute.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return profile.get("nickname").toString();
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> account = (Map<String, Object>)attribute.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return profile.get("profile_image_url").toString();
	}
}
