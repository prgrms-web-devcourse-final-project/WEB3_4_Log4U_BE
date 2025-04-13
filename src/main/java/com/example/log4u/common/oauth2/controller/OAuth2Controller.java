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
		// 리프레시 토큰 추출
		String refresh = null;
		String access = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(TokenConstants.REFRESH_TOKEN)) {
				refresh = cookie.getValue();
			}
			if (cookie.getName().equals(TokenConstants.ACCESS_TOKEN)) {
				access = cookie.getValue();
			}
		}

		if (refresh == null) {
			// 리프레시 토큰이 없는 경우
			return new ResponseEntity<>("잘못된 요청입니다..", HttpStatus.BAD_REQUEST);
		}

		// 리프레시 토큰 만료 체크
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getTokenType(refresh);
		if (!category.equals(TokenConstants.REFRESH_TOKEN)) {
			return new ResponseEntity<>("잘못된 토큰입니다.", HttpStatus.BAD_REQUEST);
		}

		createNewTokens(response, access, refresh);
		return new ResponseEntity<>(HttpStatus.OK);
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
