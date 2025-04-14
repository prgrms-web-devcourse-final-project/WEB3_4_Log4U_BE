package com.example.log4u.domain.user.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.constants.UrlConstants;
import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.user.dto.NicknameValidationResponseDto;
import com.example.log4u.domain.user.dto.UserProfileMakeRequestDto;
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

	/**
	 * 프론트 개발용 임시 jwt 발급 api
	 * */
	@GetMapping("/dev")
	public ResponseEntity<Void> loginAsDevUser(HttpServletResponse response) {
		// 개발자 전용 유저 정보 세팅
		Long userId = 1L;

		// 쿠키 전달
		Cookie accessCookie = new Cookie("access", "devtoken");
		accessCookie.setSecure(true);
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		response.addCookie(accessCookie);

		log.debug("테스트 유저 정보 조회\n");
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
	public ResponseEntity<String> loginAsDevTest(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		if (customOAuth2User == null) {
			log.debug("customOAuth2User is null!");
		} else {
			log.debug("customOAuth2UserId: {}", customOAuth2User.getUserId());
		}

		return ResponseEntity.ok("test login");
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		if (customOAuth2User.getRole().equals("ROLE_GUEST")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
			userService.getUserProfile(nickname);
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
	public ResponseEntity<Void> updateMyProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		UserProfileUpdateRequestDto userProfileUpdateRequestDto
	) {
		userService.updateMyProfile(customOAuth2User.getUserId(), userProfileUpdateRequestDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/validation/{nickname}")
	public ResponseEntity<NicknameValidationResponseDto> validateNickname(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable String nickname
	) {
		return ResponseEntity.ok(userService.validateNickname(nickname));
	}

	@GetMapping("/search")
	public ResponseEntity<PageResponse<UserProfileResponseDto>> searchUsers(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@RequestParam(required = false) String nickname,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(defaultValue = "12") int size
	) {
		return ResponseEntity.ok(
			userService.searchUsersByCursor(nickname, cursorId, size)
		);
	}
}
