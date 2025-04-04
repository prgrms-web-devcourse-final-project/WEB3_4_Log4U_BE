package com.example.log4u.domain.user.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.constants.UrlConstants;
import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileMakeRequestDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		if (customOAuth2User.getRole().equals("ROLE_GUEST")) {
			return ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(UrlConstants.PROFILE_CREATE_URL))
				.build();
		}

		UserProfileResponseDto userProfileResponseDto =
			userService.getMyProfile(customOAuth2User.getUserId());
		return ResponseEntity.ok(userProfileResponseDto);
	}

	@GetMapping("/{nickname}")
	public ResponseEntity<UserProfileResponseDto> getUserProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable String nickname
	) {
		UserProfileResponseDto userProfileResponseDto =
			userService.getUserProfile(customOAuth2User.getUserId(), nickname);
		return ResponseEntity.ok(userProfileResponseDto);
	}

	@PostMapping("/profile/make")
	public ResponseEntity<Void> createMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestBody UserProfileMakeRequestDto userProfileMakeRequestDto
	) {
		userService.createMyProfile(customOAuth2User.getUserId(), userProfileMakeRequestDto);

		// 생성 후 리디렉션 URI 설정
		HttpHeaders headers = new HttpHeaders();

		// 위치: 메인페이지(내 프로필)
		headers.setLocation(URI.create(UrlConstants.MAIN_URL));
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	@PutMapping("/me")
	public ResponseEntity<UserProfileResponseDto> updateMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		UserProfileUpdateRequestDto userProfileUpdateRequestDto
	) {
		UserProfileResponseDto userProfileResponseDto =
			userService.updateMyProfile(customOAuth2User.getUserId(), userProfileUpdateRequestDto);
		return ResponseEntity.ok(userProfileResponseDto);
	}

	@GetMapping("/validation/{nickname}")
	public ResponseEntity<NicknameValidationResponseDto> validateNickname(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable String nickname
	) {
		return ResponseEntity.ok(userService.validateNickname(nickname));
	}
}
