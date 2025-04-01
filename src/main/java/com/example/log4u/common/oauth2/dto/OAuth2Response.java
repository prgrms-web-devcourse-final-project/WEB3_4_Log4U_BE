package com.example.log4u.common.oauth2.dto;

import com.example.log4u.domain.user.entity.SocialType;

public interface OAuth2Response {
	SocialType getSocialType();

	String getProviderId();

	String getEmail();

	String getName();

	String getNickname();

	String getProfileImage();
}
