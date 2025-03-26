package com.example.log4u.domain.diary.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;

	@Transactional
	public void saveDiary(Long id, DiaryRequestDto request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Diary diary = Diary.toEntity(request);
		diary.addUser(user);

		if (request.mediaList() != null && !request.mediaList().isEmpty()) {
			List<Media> mediaList = request.mediaList()
				.stream()
				.map(Media::toEntity)
				.toList();
			diary.addMedia(mediaList);
		}
		diaryRepository.save(diary);
	}
}
