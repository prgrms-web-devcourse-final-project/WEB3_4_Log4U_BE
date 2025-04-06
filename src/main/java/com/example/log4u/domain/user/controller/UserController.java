package com.example.log4u.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.constants.TokenConstants;
import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.jwt.JwtUtil;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileResponseDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;
import com.example.log4u.domain.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	/**
	 * 프론트 개발용 임시 jwt 발급 api
	 * */
	@GetMapping("/dev")
	public ResponseEntity<?> loginAsDevUser(HttpServletResponse response) {
		// 개발자 전용 유저 정보 세팅
		Long userId = 1L;
		String name = "test";

		String email = "test@test.com";
		String role = "ROLE_USER";

		// 개발용 JWT 발급
		String access = jwtUtil.createJwt(TokenConstants.ACCESS_TOKEN, userId, name, role, 2592000L);
		String refresh = jwtUtil.createJwt(TokenConstants.REFRESH_TOKEN, userId, name, role, 2592000L);

		// 쿠키 전달
		Cookie accessCookie = new Cookie("access", "devtoken");
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		response.addCookie(accessCookie);

		log.info("테스트 유저 정보 조회\n");
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userService.getUserById(userId));

		// security context holder 에 추가해줌
		Authentication oAuth2Token = new UsernamePasswordAuthenticationToken(
			customOAuth2User,
			null,
			customOAuth2User.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(oAuth2Token);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/test")
	public ResponseEntity<Void> loginAsDevTest(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		if (customOAuth2User == null) {
			log.info("customOAuth2User is null!");
		} else {
			log.info("customOAuth2UserId: {}", customOAuth2User.getUserId());
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
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
			userService.getUserProfile(nickname);
		return ResponseEntity.ok(userProfileResponseDto);
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
