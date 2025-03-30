package com.example.log4u.common.oauth2.jwt;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.example.log4u.common.oauth2.repository.RefreshTokenRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {

	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {

		// 경로 확인
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/logout$")) {
			filterChain.doFilter(request, response);
			return;
		}

		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 리프레시 토큰 추출
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		// 리프레시 토큰 만료 체크
		if (refresh == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 만료 검사
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String tokenType = jwtUtil.getTokenType(refresh);
		if (!tokenType.equals("refresh")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//DB에 저장되어 있는지 확인
		Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
		if (Boolean.FALSE.equals(isExist)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 로그아웃
		logout(response, refresh);
	}

	public void logout(HttpServletResponse response, String refresh) {
		// DB 에서 리프레시 토큰 제거
		refreshTokenRepository.deleteByRefresh(refresh);
		// 쿠키 제거
		deleteCookie(response);
	}

	public void deleteCookie(HttpServletResponse response) {
		Cookie access = new Cookie("access", null);
		Cookie refresh = new Cookie("refresh", null);

		access.setMaxAge(0);
		access.setPath("/");
		refresh.setMaxAge(0);
		refresh.setPath("/");

		response.addCookie(access);
		response.addCookie(refresh);
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
