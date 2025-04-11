package com.example.log4u.domain.hashtag.dto;

import com.example.log4u.domain.hashtag.entity.Hashtag;

import lombok.Builder;

@Builder
public record HashtagDto(
	Long hashtagId,
	String name
) {
	public static HashtagDto of(Hashtag hashtag) {
		return HashtagDto.builder()
			.hashtagId(hashtag.getHashtagId())
			.name(hashtag.getName())
			.build();
	}

	public static Hashtag toEntity(HashtagDto hashtagDto) {
		return Hashtag.builder()
			.hashtagId(hashtagDto.hashtagId())
			.name(hashtagDto.name())
			.build();
	}
}
