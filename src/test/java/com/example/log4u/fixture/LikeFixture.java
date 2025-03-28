package com.example.log4u.fixture;

import com.example.log4u.domain.like.entity.Like;

public class LikeFixture {

	public static Like createLikeFixture(Long likeId, Long userId, Long diaryId) {
		return Like.builder()
			.likeId(likeId)
			.userId(userId)
			.diaryId(diaryId)
			.build();
	}
}
