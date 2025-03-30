package com.example.log4u.domain.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.follow.entitiy.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
