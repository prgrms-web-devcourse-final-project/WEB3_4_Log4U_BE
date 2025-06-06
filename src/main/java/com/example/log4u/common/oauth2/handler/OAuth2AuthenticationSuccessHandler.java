package com.example.log4u.common.oauth2.handler;

import static com.example.log4u.common.constants.TokenConstants.*;
import static com.example.log4u.common.constants.UrlConstants.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.jwt.JwtUtil;
import com.example.log4u.common.oauth2.service.RefreshTokenService;
import com.example.log4u.common.util.CookieUtil;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;
	private final JwtUtil jwtUtil;

	@Value("${jwt.access-token-expire-time-seconds}")
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		//OAuth2User
		CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		Optional<User> existUser = userRepository.findByProviderId(customOAuth2User.getProviderId());
		Long userId = existUser.map(User::getUserId).orElse(null);
		String name = customOAuth2User.getName();

		setCookieAndSaveRefreshToken(response, userId, authentication, name);
		redirectTo(response, customOAuth2User);
	}

	private void setCookieAndSaveRefreshToken(
		HttpServletResponse response,
		Long userId,
		Authentication authentication,
		String name
	) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		// 쿠키 생성
		String access = jwtUtil.createJwt(ACCESS_TOKEN, userId, name, role, accessTokenValidityInSeconds);
		String refresh = jwtUtil.createJwt(REFRESH_TOKEN, userId, name, role, refreshTokenValidityInSeconds);

		// 리프레시 토큰 DB 저장
		refreshTokenService.saveRefreshToken(name, refresh);

		// SameSite=None 속성이 있는 쿠키 생성 및 추가
		CookieUtil.createCookieWithSameSite(response, ACCESS_TOKEN, access);
		CookieUtil.createCookieWithSameSite(response, REFRESH_TOKEN, refresh);

		response.setStatus(HttpStatus.OK.value());
	}

	private void redirectTo(HttpServletResponse response, CustomOAuth2User customOAuth2User) throws IOException {
		switch (customOAuth2User.getRole()) {
			// 프론트 페이지 부재로 임시 비활성화
			case "ROLE_GUEST" -> response.sendRedirect(FRONT_VERCEL_ORIGIN);
			case "ROLE_USER" -> response.sendRedirect(FRONT_VERCEL_ORIGIN);
			default -> {
				// 로그인이 필요한 경우 401
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
			}
		}
	}

}
