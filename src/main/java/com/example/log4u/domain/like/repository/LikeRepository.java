package com.example.log4u.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByUserIdAndDiaryId(Long userId, Long diaryId);
}
