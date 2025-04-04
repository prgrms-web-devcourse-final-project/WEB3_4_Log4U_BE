package com.example.log4u.common.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.log4u.common.constants.UrlConstants;
import com.example.log4u.common.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.example.log4u.common.oauth2.jwt.JwtAuthenticationFilter;
import com.example.log4u.common.oauth2.jwt.JwtLogoutFilter;
import com.example.log4u.common.oauth2.jwt.JwtUtil;
import com.example.log4u.common.oauth2.repository.RefreshTokenRepository;
import com.example.log4u.common.oauth2.service.CustomOAuth2UserService;
import com.example.log4u.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final UserService userService;
	private final RefreshTokenRepository refreshTokenRepository;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtUtil, userService);
	}

	@Bean
	public JwtLogoutFilter jwtLogoutFilter() {
		return new JwtLogoutFilter(jwtUtil, refreshTokenRepository);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)    //csrf 비활성화
			.formLogin(AbstractHttpConfigurer::disable)    //폼 로그인 방식 disable
			.httpBasic(AbstractHttpConfigurer::disable);            // HTTP Basic 인증 방식 disable

		// oauth2 설정
		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2AuthenticationSuccessHandler)
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userService), OAuth2LoginAuthenticationFilter.class)
			.addFilterBefore(new JwtLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

		//경로별 인가 작업
		http
			.authorizeHttpRequests(auth -> auth
				// 소셜 로그인 경로
				.requestMatchers("/oauth2/**").permitAll()
				// Swagger UI 관련 경로 (swagger-ui.html 추가)
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.anyRequest().authenticated());

		//세션 설정 : STATELESS
		http
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// CORS 설정
			.cors((corsCustomizer -> corsCustomizer.configurationSource(request -> {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(Collections.singletonList(UrlConstants.FRONT_ORIGIN_URL));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);
				configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
				configuration.setExposedHeaders(Collections.singletonList("access"));
				configuration.setExposedHeaders(Collections.singletonList("refresh"));
				return configuration;
			})));
		return http.build();
	}
}
