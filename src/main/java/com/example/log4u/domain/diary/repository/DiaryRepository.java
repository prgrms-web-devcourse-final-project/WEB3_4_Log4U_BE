package com.example.log4u.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.diary.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, CustomDiaryRepository {
}