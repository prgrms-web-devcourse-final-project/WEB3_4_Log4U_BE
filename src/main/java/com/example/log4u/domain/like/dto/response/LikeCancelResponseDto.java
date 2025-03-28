package com.example.log4u.domain.like.dto.response;

public record LikeCancelResponseDto(
	boolean liked,
	Long likeCount) {

	public static LikeCancelResponseDto of(boolean liked, Long likeCount) {
		return new LikeCancelResponseDto(liked, likeCount);
	}
}
