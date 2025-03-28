package com.example.log4u.domain.diary.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.follow.repository.FollowRepository;
import com.example.log4u.domain.media.service.MediaService;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

	private static final int PAGE_SIZE = 6;

	private final DiaryRepository diaryRepository;
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final MediaService mediaService;

	@Transactional
	public void saveDiary(Long userId, DiaryRequestDto request) {
		validateUser(userId);
		String thumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		Diary diary = diaryRepository.save(
			Diary.toEntity(userId, request, thumbnailUrl)
		);
		mediaService.saveMedia(diary.getId(), request.mediaList());
	}

	@Transactional(readOnly = true)
	public List<DiaryResponseDto> searchDiaries(
		String keyword,
		String sort,
		int page
	) {
		List<VisibilityType> visibilities = List.of(VisibilityType.PUBLIC);

		return diaryRepository.searchDiaries(
				keyword,
				visibilities,
				sort,
				PageRequest.of(page, PAGE_SIZE)
			).stream()
			.map(DiaryResponseDto::of)
			.toList();
	}

	@Transactional(readOnly = true)
	public DiaryResponseDto getDiary(Long userId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new RuntimeException("Diary not found"));

		validateDiaryAccess(diary, userId);
		return DiaryResponseDto.of(diary);
	}

	@Transactional
	public void updateDiary(Long userId, Long diaryId, DiaryRequestDto request) {
		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new RuntimeException("Diary not found"));

		validateDiaryOwner(diary, userId);

		if (request.mediaList() != null) {
			mediaService.updateMedia(diary.getId(), request.mediaList());
		}

		String newThumbnailUrl = mediaService.extractThumbnailUrl(request.mediaList());
		diary.update(request, newThumbnailUrl);
	}

	@Transactional
	public void deleteDiary(Long userId, Long diaryId) {
		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new RuntimeException("Diary not found"));

		validateDiaryOwner(diary, userId);

		mediaService.deleteMedia(diaryId);
		diaryRepository.delete(diary);
	}

	private void validateDiaryAccess(Diary diary, Long userId) {
		if (diary.getVisibility() == VisibilityType.PRIVATE) {
			if (!diary.getUserId().equals(userId)) {
				throw new RuntimeException("Diary access denied");
			}
		}

		if (diary.getVisibility() == VisibilityType.FOLLOWER) {
			if (!diary.getUserId().equals(userId)
				&& !followRepository.existsByFollowerIdAndFollowingId(userId, diary.getUserId())) {
				throw new RuntimeException("Follower access denied");
			}
		}
	}

	private void validateDiaryOwner(Diary diary, Long userId) {
		if (!diary.getUserId().equals(userId)) {
			throw new RuntimeException("Diary owner access denied");
		}
	}

	private void validateUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new RuntimeException("User not found");
		}
	}
}
