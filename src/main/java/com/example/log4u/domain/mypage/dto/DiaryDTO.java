package com.example.log4u.domain.mypage.dto;

import java.time.LocalDateTime;

public record DiaryDTO(
        Long diaryId,          // 다이어리 ID
        Long userId,           // 작성자 ID
        String title,          // 다이어리 제목
        String thumbnailUrl,   // 썸네일 URL
        String content,        // 다이어리 내용
        Double latitude,       // 위도 - erd에 varchar말고 double?
        Double longitude,      // 경도
        LocalDateTime createdAt, // 작성 날짜
        LocalDateTime updatedAt, // 수정 날짜
        String weatherInfo,    // 날씨 정보
        String visibility      // 공개 범위 (예: "public", "private")
) {}