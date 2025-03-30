package com.example.log4u.common.oauth2.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.entity.RefreshToken;
import com.example.log4u.common.oauth2.jwt.JwtUtil;
import com.example.log4u.common.oauth2.repository.RefreshTokenRepository;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtil jwtUtil;

	private static final String MAIN_PAGE = "http://localhost:3000/";
	private static final String PROFILE_CREATE_PAGE = "http://localhost:3000/profile";
	private static final String LOGIN_PAGE = "http://localhost:3000/login";

	private static final String ACCESS_TOKEN_KEY = "access";
	private static final String REFRESH_TOKEN_KEY = "refresh";

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

		String redirectUrl = switch (customOAuth2User.getRole()) {
			case "ROLE_GUEST" -> PROFILE_CREATE_PAGE;
			case "ROLE_USER" -> MAIN_PAGE;
			default -> LOGIN_PAGE;
		};

		setCookieAndSaveRefreshToken(response, userId, authentication, name);
		redirectTo(response, redirectUrl);
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
		String access = jwtUtil.createJwt(ACCESS_TOKEN_KEY, userId, role, accessTokenValidityInSeconds);
		String refresh = jwtUtil.createJwt(REFRESH_TOKEN_KEY, userId, role, refreshTokenValidityInSeconds);
		// 저장
		saveRefreshToken(refresh, name);

		response.addCookie(createCookie(ACCESS_TOKEN_KEY, access));
		response.addCookie(createCookie(REFRESH_TOKEN_KEY, refresh));
		response.setStatus(HttpStatus.OK.value());
	}

	public void redirectTo(HttpServletResponse response, String redirectUrl) throws IOException {
		response.sendRedirect(redirectUrl);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public void saveRefreshToken(String refresh, String name) {
		Date date = new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds);

		RefreshToken refreshToken = new RefreshToken(
			null,
			name,
			refresh,
			date.toString()
		);
		refreshTokenRepository.save(refreshToken);
	}
}