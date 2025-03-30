package com.example.log4u.domain.like.dto.response;

public record LikeAddResponseDto(
	boolean liked,
	Long likeCount) {

	public static LikeAddResponseDto of(boolean liked, Long likeCount) {
		return new LikeAddResponseDto(liked, likeCount);
	}
}
