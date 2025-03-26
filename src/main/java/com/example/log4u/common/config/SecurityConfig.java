package com.example.log4u.common.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.log4u.common.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.example.log4u.common.oauth2.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
//@EnableWebSecurity
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
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
			);

		// 요청에 대한 권한 설정
		// 	.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
		// 	// H2 콘솔 관련 경로
		// 	.requestMatchers("/h2-console/**").permitAll()
		//
		// 	// Swagger UI 관련 경로 (swagger-ui.html 추가)
		// 	.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
		// 	// 채팅 관련 경로
		// 	.requestMatchers("/chat-test.html", "/chat/**").permitAll()
		// 	.requestMatchers("/", "/auth/**", "/oauth2/**").permitAll()
		// 	.requestMatchers("/", "login/auth/**", "login/oauth2/**").permitAll()
		//
		// 	.requestMatchers(HttpMethod.GET, "/travels/*").permitAll()
		// 	.requestMatchers(HttpMethod.GET, "/travels/**").permitAll()
		// 	.requestMatchers(HttpMethod.GET, "/guides/*").permitAll()
		// 	.requestMatchers(HttpMethod.GET, "/guides/**").permitAll()
		//
		// 	.requestMatchers("/guide-requests/**").permitAll()
		// 	.requestMatchers("/auth/signup", "/auth/login").permitAll()
		// 	.anyRequest().authenticated())
		//
		// 	.userDetailsService(customUserDetailsService)
		// 	.formLogin(formLogin -> formLogin
		// 		.loginProcessingUrl("/auth/login")
		// 		.usernameParameter("email")
		// 		.passwordParameter("password")
		// 		.disable())

		//세션 설정 : STATELESS
		http
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// CORS 설정
			.cors((corsCustomizer -> corsCustomizer.configurationSource(request -> {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);
				configuration.setExposedHeaders(Collections.singletonList("Authorization"));
				return configuration;
			})));
		return http.build();
	}
}
