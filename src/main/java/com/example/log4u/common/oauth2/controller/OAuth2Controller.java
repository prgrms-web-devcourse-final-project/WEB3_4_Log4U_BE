package com.example.log4u.common.oauth2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.jwt.JwtUtil;

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
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
			if( cookie.getName().equals("access") ) {
				access = cookie.getValue();
			}
		}

		// if (refresh == null) {
		// 	// 리프레시 토큰이 없는 경우
		// 	return new ResponseEntity<>("잘못된 요청입니다..", HttpStatus.BAD_REQUEST);
		// }

		// 리프레시 토큰 만료 체크
		// try {
		// 	jwtUtil.isExpired(refresh);
		// } catch (ExpiredJwtException e) {
		// 	return new ResponseEntity<>("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
		// }

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		// String category = jwtUtil.getCategory(refresh);
		// if (!category.equals("refresh")) {
		// 	return new ResponseEntity<>("잘못된 토큰입니다.", HttpStatus.BAD_REQUEST);
		// }

		Long userId = jwtUtil.getUserId(access);
		String role = jwtUtil.getRole(access);

		// 액세스 토큰 새로 생성
		String newAccessToken = jwtUtil.createJwt("access", userId, role, 600000L);
		String newRefreshToken = jwtUtil.createJwt("refresh", userId, role, 600000L);

		// 액세스 토큰 헤더 설정
		//response.setHeader("access", newAccessToken);

		// Refresh Token Rotate
		response.addCookie(createCookie("refresh", newRefreshToken));
		response.addCookie(createCookie("access", newAccessToken));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
}
