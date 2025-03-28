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
				.get("userId", Long.class);
		} catch(ExpiredJwtException ex){
			return ex.getClaims().get("userId", Long.class);
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
		}catch(ExpiredJwtException ex){
			return ex.getClaims().get("role", String.class);
		}
	}

	public String getCategory(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("category", String.class);
		}catch(ExpiredJwtException ex){
			return ex.getClaims().get("category", String.class);
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

	public String createJwt(String category, Long userId, String role, Long expiredMs) {
		return Jwts.builder()
			.claim("category", category)
			.claim("userId", userId)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs*1000))
			.signWith(secretKey)
			.compact();
	}
}
