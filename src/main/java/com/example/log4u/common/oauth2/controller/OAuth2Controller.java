package com.example.log4u.common.oauth2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.constants.TokenConstants;
import com.example.log4u.common.oauth2.jwt.JwtUtil;
import com.example.log4u.common.oauth2.service.RefreshTokenService;
import com.example.log4u.common.util.CookieUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2Controller {

	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	@GetMapping("/token/reissue")
	public ResponseEntity<?> reissue(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		// 쿠키가 없으면 바로 401 (로그아웃)
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body("쿠키가 존재하지 않습니다.");
		}

		String refresh = null;
		String access = null;

		// 쿠키에서 토큰 추출
		for (Cookie cookie : cookies) {
			if (TokenConstants.REFRESH_TOKEN.equals(cookie.getName())) {
				refresh = cookie.getValue();
			} else if (TokenConstants.ACCESS_TOKEN.equals(cookie.getName())) {
				access = cookie.getValue();
			}
		}

		// 리프레시 토큰이 없는 경우
		if (refresh == null) {
			return ResponseEntity
				.badRequest()
				.body("리프레시 토큰이 존재하지 않습니다.");
		}

		//  DB에 리프레시 토큰 존재하는지 확인
		if (!refreshTokenService.existsByRefresh(refresh)) {
			return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body("이미 로그아웃된 사용자입니다.");
		}

		// 리프레시 토큰 만료 여부 확인
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body("리프레시 토큰이 만료되었습니다.");
		}

		// 리프레시 토큰인지 타입 확인
		String category = jwtUtil.getTokenType(refresh);
		if (!TokenConstants.REFRESH_TOKEN.equals(category)) {
			return ResponseEntity
				.badRequest()
				.body("리프레시 토큰이 아닙니다.");
		}

		// 새 토큰 발급
		createNewTokens(response, access, refresh);

		return ResponseEntity.ok().build();
	}

	private void createNewTokens(HttpServletResponse response, String access, String refresh) {
		// 기존 리프레시 토큰 삭제
		refreshTokenService.deleteRefreshToken(refresh);

		Long userId = jwtUtil.getUserId(access);
		String role = jwtUtil.getRole(access);
		String name = jwtUtil.getName(access);

		String newAccessToken = jwtUtil.createJwt(TokenConstants.ACCESS_TOKEN, userId, name, role, 600000L);
		String newRefreshToken = jwtUtil.createJwt(TokenConstants.REFRESH_TOKEN, userId, name, role, 600000L);

		// SameSite=None 속성이 있는 쿠키 생성 및 추가
		CookieUtil.createCookieWithSameSite(response, TokenConstants.ACCESS_TOKEN, newAccessToken);
		CookieUtil.createCookieWithSameSite(response, TokenConstants.REFRESH_TOKEN, newRefreshToken);

		// 새 리프레시 토큰 저장
		refreshTokenService.saveRefreshToken(
			name,
			refresh
		);

	}

}
