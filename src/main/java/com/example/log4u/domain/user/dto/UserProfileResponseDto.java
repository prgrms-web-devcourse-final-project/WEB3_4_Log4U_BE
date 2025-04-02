package com.example.log4u.domain.user.dto;

import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.user.entity.User;

import lombok.Builder;

@Builder
public record UserProfileResponseDto(
	String name,
	String nickname,
	String statusMessage,
	String profileImage,
	Long diaryCount,
	Long followers,
	Long followings,
	List<Diary> diaries
) {

	public static UserProfileResponseDto fromUser(
		User user,
		Long diaryCount,
		Long followers,
		Long followings,
		List<Diary> diaries
	) {
		return builder()
			.name(user.getName())
			.nickname(user.getNickname())
			.statusMessage(user.getStatusMessage())
			.profileImage(user.getProfileImage())
			.diaryCount(diaryCount)
			.followers(followers)
			.followings(followings)
			.diaries(diaries)
			.build();
	}
}
