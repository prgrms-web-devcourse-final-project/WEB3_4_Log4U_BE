package com.example.log4u.domain.user.dto;

import lombok.Builder;

@Builder
public record UserThumbnailResponseDto(
	Long userId,
	String nickname,
	String thumbnailUrl
) {
}
