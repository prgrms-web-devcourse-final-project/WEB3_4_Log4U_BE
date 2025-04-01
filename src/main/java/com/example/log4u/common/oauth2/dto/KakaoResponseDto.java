package com.example.log4u.common.oauth2.dto;

import java.util.Map;

import com.example.log4u.domain.user.entity.SocialType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponseDto implements OAuth2Response {

	private static final String KAKAO_ACCOUNT = "kakao_account";

	@Override
	public String getNickname() {
		return "";
	}

	@Override
	public String getProfileImage() {
		// kakao_account.profile.profile_image_url 형태로 응답
		Map<String, Object> account = (Map<String, Object>)attribute.get(KAKAO_ACCOUNT);
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("profile_image_url");
	}

	private final Map<String, Object> attribute;

	@Override
	public SocialType getSocialType() {
		return SocialType.KAKAO;
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> account = (Map<String, Object>)attribute.get(KAKAO_ACCOUNT);
		return (String)account.get("email");
	}

	@Override
	public String getName() {
		Map<String, Object> account = (Map<String, Object>)attribute.get(KAKAO_ACCOUNT);
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return profile.get("nickname").toString();
	}

}
