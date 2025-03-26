package com.example.log4u.domain.diary.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.diary.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

	private final DiaryRepository diaryRepository;

}
