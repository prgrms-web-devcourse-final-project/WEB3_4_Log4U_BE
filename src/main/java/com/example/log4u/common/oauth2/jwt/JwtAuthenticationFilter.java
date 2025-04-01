package com.example.log4u.common.oauth2.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final UserService userService;
	private static final String ACCESS_TOKEN_EXPIRED_JSON_MSG = "{\"message\": \"토큰이 존재하지 않습니다.\"}";

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		// 필터 스킵이 필요한지 확인
		if (shouldSkipFilter(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = extractAccessTokenFromCookie(request);
		// 엑세스 토큰이 없을 경우 통과해서 발급 절차
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		log.debug("필터에서 추출한 엑세스 토큰: " + accessToken + "\n");

		// 토큰 유효성 검사(실패 시 바로 리턴
		if (!validateTokenExpiration(response, accessToken)) {
			return;
		}

		addUserToContextHolder(accessToken);
		filterChain.doFilter(request, response);
	}

	private boolean shouldSkipFilter(String requestUri) {
		return requestUri.matches("^/login(/.*)?$") || requestUri.matches("^/oauth2(/.*)?$");
	}

	private String extractAccessTokenFromCookie(HttpServletRequest request) {
		// 쿠키에서 access 토큰 추출
		String accessToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("access")) {
				accessToken = cookie.getValue();
			}
		}
		return accessToken;
	}

	private boolean validateTokenExpiration(
		HttpServletResponse response,
		String accessToken
	) throws IOException {
		// 토큰 만료 확인 , 만료 시 다음 필터로 넘기지 않음(재발급 필요)
		try {
			log.debug("만료확인체크" + "\n");
			log.debug("유저 ID : " + jwtUtil.getUserId(accessToken) + "\n");
			log.debug("role : " + jwtUtil.getRole(accessToken) + "\n");
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			PrintWriter writer = response.getWriter();
			writer.print(ACCESS_TOKEN_EXPIRED_JSON_MSG);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		// access 토큰 인지 확인 (발급 시 페이로드에 명시)
		if (!jwtUtil.getTokenType(accessToken).equals("access")) {
			PrintWriter writer = response.getWriter();
			writer.print(ACCESS_TOKEN_EXPIRED_JSON_MSG);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		// 유효성 검사 성공
		return true;
	}

	private void addUserToContextHolder(String accessToken) {
		// 토큰에서 id 추출
		Long userId = jwtUtil.getUserId(accessToken);
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userService.getUserById(userId));
		log.debug("필터에서 추출한 userId: " + userId);
		log.debug("생성된 CustomOAuth2User ID: " + customOAuth2User.getUserId());

		// security context holder 에 추가해줌
		Authentication oAuth2Token = new UsernamePasswordAuthenticationToken(
			customOAuth2User,
			null,
			customOAuth2User.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(oAuth2Token);
	}

}
