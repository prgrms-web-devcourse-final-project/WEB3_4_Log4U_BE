package com.example.log4u.domain.mypage.dto;

public record LikeDTO(
        Long likeId,         // 좋아요 ID
        Long userId,         // 좋아요를 누른 사용자 ID
        Long diaryId         // 좋아요가 눌린 다이어리 ID
) {}