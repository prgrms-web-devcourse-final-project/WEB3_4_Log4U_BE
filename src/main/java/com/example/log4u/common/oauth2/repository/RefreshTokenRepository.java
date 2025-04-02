package com.example.log4u.common.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.common.oauth2.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Boolean existsByRefresh(String refresh);

	void deleteByRefresh(String refresh);
}
