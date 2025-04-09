package com.example.log4u.common.config;

import java.util.Collections;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.log4u.common.constants.UrlConstants;

public class CorsConfig {
	public static CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of(
			UrlConstants.FRONT_ORIGIN_URL,
			UrlConstants.FRONT_SUB_DOMAIN_URL
		));

		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setMaxAge(3600L);
		configuration.addExposedHeader("Set-Cookie");
		configuration.addExposedHeader("access");
		configuration.addExposedHeader("refresh");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private CorsConfig() {
	}
}
