package com.example.log4u.domain.mypage.dto;

public record FollowDTO(
        Long followId,       // 팔로우 ID
        Long followingId,    // 내가 팔로우한 유저 ID
        Long followerId      // 나를 팔로우한 유저 ID
) {}