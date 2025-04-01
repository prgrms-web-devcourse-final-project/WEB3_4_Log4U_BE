package com.example.log4u.common.oauth2.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.log4u.domain.user.dto.UserCreateRequestDto;
import com.example.log4u.domain.user.entity.User;

public class CustomOAuth2User implements OAuth2User {
	private final UserCreateRequestDto userCreateRequestDto;

	public CustomOAuth2User(UserCreateRequestDto userCreateRequestDto) {
		this.userCreateRequestDto = userCreateRequestDto;
	}

	public CustomOAuth2User(User user) {
		this.userCreateRequestDto = UserCreateRequestDto.fromEntity(user);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add((GrantedAuthority)userCreateRequestDto::role);
		return collection;
	}

	@Override
	public String getName() {
		return userCreateRequestDto.name();
	}

	public String getRole() {
		return userCreateRequestDto.role();
	}

	public String getProviderId() {
		return userCreateRequestDto.providerId();
	}

	public Long getUserId() {
		return userCreateRequestDto.userId();
	}
}
