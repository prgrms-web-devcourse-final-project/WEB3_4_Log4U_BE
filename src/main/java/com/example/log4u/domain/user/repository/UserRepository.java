package com.example.log4u.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
