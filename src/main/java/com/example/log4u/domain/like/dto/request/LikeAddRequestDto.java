package com.example.log4u.domain.like.dto.request;

import com.example.log4u.domain.like.entity.Like;

public record LikeAddRequestDto(
	Long diaryId

) {

	public Like toEntity(Long userId) {
		return Like.builder()
			.userId(userId)
			.diaryId(diaryId)
			.build();
	}
}
