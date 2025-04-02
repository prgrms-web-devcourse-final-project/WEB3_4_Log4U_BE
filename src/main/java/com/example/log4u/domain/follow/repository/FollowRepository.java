package com.example.log4u.domain.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.follow.entitiy.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByInitiatorIdAndTargetId(Long initiatorId, Long targetId);
}
