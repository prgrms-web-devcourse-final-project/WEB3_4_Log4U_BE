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

		response.addCookie(CookieUtil.createCookie(ACCESS_TOKEN, access));
		response.addCookie(CookieUtil.createCookie(REFRESH_TOKEN, refresh));
		response.setStatus(HttpStatus.OK.value());
	}

	private void redirectTo(HttpServletResponse response, CustomOAuth2User customOAuth2User) throws IOException {
		String redirectUrl = switch (customOAuth2User.getRole()) {
			case "ROLE_GUEST" -> PROFILE_CREATE_URL;
			case "ROLE_USER" -> FRONT_VERCEL_ORIGIN;
			default -> LOGIN_URL;
		};
		response.sendRedirect(redirectUrl);
	}

}
