package com.example.log4u.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUid(String username);
	Optional<User> findByNickname(String nickname);
	Optional<User> findByProviderId(String providerId);
}
