package com.example.log4u.domain.diary.dto;

import com.example.log4u.domain.diary.entity.Diary;

public record DiaryWithAuthorDto(
	Diary diary,
	String authorNickname,
	String authorProfileImage
) {
}
