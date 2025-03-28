package com.example.log4u.common.oauth2.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.dto.UserCreateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	private final UserService userService;

	@Value("${jwt.access-token-expiration-time-seconds}")
	private long accessTokenValiditySeconds;

	@Value("${jwt.refresh-token-expiration-time-seconds}")
	private long refreshTokenValiditySeconds;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 헤더에서 access키에 담긴 토큰 추출
		String accessToken = request.getHeader("access");

		// 토큰이 없다면 다음 필터로 넘겨서 발급 받아야함
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 토큰 만료 확인, 만료 시 다음 필터로 넘기지 않음(재발급 필요)
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			PrintWriter writer = response.getWriter();
			writer.print("토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// 토큰이 access인지 확인 (발급 시 페이로드에 명시)
		String category = jwtUtil.getCategory(accessToken);
		
		// 이상한 값일 경우
		if (!category.equals("access")) {
			PrintWriter writer = response.getWriter();
			writer.print("토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// userId, role
		Long userId = jwtUtil.getUserId(accessToken);

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userService.getUserById(userId));

		// security context holder 에 추가해줌
		Authentication oAuth2Token = new UsernamePasswordAuthenticationToken(
			customOAuth2User,
			null,
			customOAuth2User.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(oAuth2Token);
		filterChain.doFilter(request, response);
	}
}
