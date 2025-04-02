package com.example.log4u.domain.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.follow.entitiy.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByInitiatorIdAndTargetId(Long initiatorId, Long targetId);

	void deleteByInitiatorIdAndTargetId(Long initiatorId, Long targetId);

	// 기능 구현 초기용, 데이터 쌓이면 개선 필요
	Long countByInitiatorId(Long initiatorId);

	Long countByTargetId(Long targetId);
}
