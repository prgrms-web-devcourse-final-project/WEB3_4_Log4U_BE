package com.example.log4u.common.oauth2.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

	private final SecretKey secretKey;
	private static final String USER_ID_KEY = "userId";
	private static final String TOKEN_TYPE_KEY = "token";
	private static final String USER_NAME_KEY = "name";
	private static final String USER_ROLE_KEY = "role";

	public JwtUtil(@Value("${jwt.secret}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public Long getUserId(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(USER_ID_KEY, Long.class);
		} catch (ExpiredJwtException ex) {
			return ex.getClaims().get(USER_ID_KEY, Long.class);
		}
	}

	public String getName(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(USER_NAME_KEY, String.class);
		} catch (ExpiredJwtException ex) {
			return ex.getClaims().get(USER_NAME_KEY, String.class);
		}
	}

	public String getRole(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
		} catch (ExpiredJwtException ex) {
			return ex.getClaims().get(USER_ROLE_KEY, String.class);
		}
	}

	public String getTokenType(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get(TOKEN_TYPE_KEY, String.class);
		} catch (ExpiredJwtException ex) {
			return ex.getClaims().get(TOKEN_TYPE_KEY, String.class);
		}
	}

	public Boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public String createJwt(String tokenType, Long userId, String name, String role, Long expiredMs) {
		return Jwts.builder()
			.claim(TOKEN_TYPE_KEY, tokenType)
			.claim(USER_ID_KEY, userId)
			.claim(USER_NAME_KEY, name)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs * 1000))
			.signWith(secretKey)
			.compact();
	}
}
