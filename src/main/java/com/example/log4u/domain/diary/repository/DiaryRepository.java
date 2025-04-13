package com.example.log4u.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long>, CustomDiaryRepository {
}
