package com.example.log4u.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByUserIdAndDiaryId(Long userId, Long diaryId);

	Optional<Like> findByUserIdAndDiaryId(Long userId, Long diaryId);
}
