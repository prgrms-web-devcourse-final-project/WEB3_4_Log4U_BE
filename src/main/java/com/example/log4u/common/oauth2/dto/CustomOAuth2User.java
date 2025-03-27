package com.example.log4u.common.oauth2.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

public class CustomOAuth2User implements OAuth2User {
	private final UserCreateRequestDto userCreateRequestDto;

	@Getter
	private final Long userId;

	public CustomOAuth2User(UserCreateRequestDto userCreateRequestDto) {
		this.userCreateRequestDto = userCreateRequestDto;
		this.userId = null;
	}

	public CustomOAuth2User(UserCreateRequestDto userCreateRequestDto, Long userId) {
		this.userCreateRequestDto = userCreateRequestDto;
		this.userId = userId;
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

	public String getUid() {
		return userCreateRequestDto.uid();
	}

	public String getRole() { return userCreateRequestDto.role();}
}
