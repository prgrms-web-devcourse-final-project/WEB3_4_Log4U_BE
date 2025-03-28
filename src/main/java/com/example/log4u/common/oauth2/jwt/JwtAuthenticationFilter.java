package com.example.log4u.common.oauth2.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.dto.UserCreateRequestDto;
import com.example.log4u.domain.user.entity.User;
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

	@Value("${jwt.access-token-expire-time-seconds}")
	private long accessTokenValiditySeconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValiditySeconds;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		if (requestUri.matches("^\\/login(?:\\/.*)?$")) {
			filterChain.doFilter(request, response);
			return;
		}
		if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 쿠키에서 access키에 담긴 토큰 추출
		String accessToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies){
			if (cookie.getName().equals("access")) {
				accessToken = cookie.getValue();
			}
		}

		// 토큰이 없다면 다음 필터로 넘겨서 발급 받아야함
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		log.info("필터에서 추출한 access: " + accessToken + "\n");


		// 토큰 만료 확인 , 만료 시 다음 필터로 넘기지 않음(재발급 필요)
		try {
			System.out.println("만료확인");
			System.out.println("token type : " + jwtUtil.getTokenType(accessToken));
			System.out.println("userId : " + jwtUtil.getUserId(accessToken));
			System.out.println("role : " + jwtUtil.getRole(accessToken));
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.print("토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// 토큰이 access인지 확인 (발급 시 페이로드에 명시)
		String tokenType = jwtUtil.getTokenType(accessToken);
		
		// 이상한 값일 경우
		if (!tokenType.equals("access")) {
			PrintWriter writer = response.getWriter();
			writer.print("토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// userId, role
		Long userId = jwtUtil.getUserId(accessToken);

		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userService.getUserById(userId));
		log.info("필터에서 추출한 userId: " + userId);
		log.info("생성된 CustomOAuth2User ID: " + customOAuth2User.getUserId());

		// security context holder 에 추가해줌
		Authentication oAuth2Token = new UsernamePasswordAuthenticationToken(
			customOAuth2User,
			null,
			customOAuth2User.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(oAuth2Token);
		filterChain.doFilter(request, response);
	}
}
