package com.example.log4u.common.oauth2.jwt;

import static com.example.log4u.common.constants.TokenConstants.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.web.filter.GenericFilterBean;

import com.example.log4u.common.oauth2.service.RefreshTokenService;
import com.example.log4u.common.util.CookieUtil;

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
	private final RefreshTokenService refreshTokenService;
	private static final String REFRESH_TOKEN_EXPIRED_JSON_MSG = "{\"message\": \"토큰이 존재하지 않습니다.\"}";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	private void doFilter(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws IOException, ServletException {

		// url 이 logout 이 아닐 경우 필터 통과
		if (shouldSkipFilter(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		// POST 요청 아니면 통과
		if (!request.getMethod().equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 리프레시 토큰 추출
		String refresh = extractRefreshTokenFromCookie(request);

		if (refresh == null) {
			// 이미 로그아웃 상태
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			return;
		}

		// 리프레시 토큰 유효성 검사
		if (!validateTokenExpiration(response, refresh)) {
			return;
		}

		// 로그아웃 진행
		logout(response, refresh);
	}

	private boolean shouldSkipFilter(String requestUri) {
		// /oauth2/logout 만 필터실행
		if ("/oauth2/logout".equals(requestUri)) {
			return false;
		}

		// 나머지는 다 통과
		return requestUri.startsWith("/swagger-ui")
			|| requestUri.startsWith("/v3/api-docs")
			|| requestUri.startsWith("/oauth2");
	}

	private boolean validateTokenExpiration(
		HttpServletResponse response,
		String refresh
	) throws IOException {
		// 리프레시 토큰 만료 체크
		if (refresh == null) {
			PrintWriter writer = response.getWriter();
			writer.print(REFRESH_TOKEN_EXPIRED_JSON_MSG);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		// 만료 검사
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			PrintWriter writer = response.getWriter();
			writer.print(REFRESH_TOKEN_EXPIRED_JSON_MSG);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		// 토큰이 refresh 인지 확인 (발급시 페이로드에 명시)
		String tokenType = jwtUtil.getTokenType(refresh);
		if (!tokenType.equals(REFRESH_TOKEN)) {
			PrintWriter writer = response.getWriter();
			writer.print(REFRESH_TOKEN_EXPIRED_JSON_MSG);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		// 리프레시 토큰이 DB에 없는 경우
		Boolean isExist = refreshTokenService.existsByRefresh(refresh);
		if (Boolean.FALSE.equals(isExist)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		// 유효성 검사 성공
		return true;
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (REFRESH_TOKEN.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}

		return null;
	}

	public void logout(HttpServletResponse response, String refresh) throws IOException {
		// DB 에서 리프레시 토큰 제거
		refreshTokenService.deleteRefreshToken(refresh);
		// 쿠키 제거
		CookieUtil.deleteCookie(response, ACCESS_TOKEN);
		CookieUtil.deleteCookie(response, REFRESH_TOKEN);

		// 응답 (404 방지)
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.getWriter().write("{\"message\": \"로그아웃 성공\"}");
		response.getWriter().flush();
	}

}
