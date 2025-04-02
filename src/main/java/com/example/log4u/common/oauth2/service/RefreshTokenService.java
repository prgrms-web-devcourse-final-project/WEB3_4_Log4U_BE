package com.example.log4u.common.oauth2.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.log4u.common.oauth2.entity.RefreshToken;
import com.example.log4u.common.oauth2.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.refresh-token-expire-time-seconds}")
	private long refreshTokenValidityInSeconds;

	public void saveRefreshToken(String name, String refresh) {
		Date date = new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds);

		RefreshToken refreshToken = new RefreshToken(
			null,
			name,
			refresh,
			date.toString()
		);
		refreshTokenRepository.save(refreshToken);
	}

	@Transactional
	public void deleteRefreshToken(String refresh) {
		refreshTokenRepository.deleteByRefresh(refresh);
	}
}
