package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long>, CustomDiaryRepository {
	List<Diary> findTop10ByVisibilityOrderByLikeCountDesc(VisibilityType visibility);

	Integer countByUserId(Long userId);
}
