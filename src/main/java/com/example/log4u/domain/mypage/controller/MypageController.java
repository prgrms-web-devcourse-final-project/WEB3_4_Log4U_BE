package com.example.log4u.domain.mypage.controller;

import com.example.log4u.domain.mypage.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MypageController {

    // 내가 좋아요한 다이어리 목록 조회
    @GetMapping("/likes")
    public ResponseEntity<List<LikeDTO>> getLikedDiaries() {
        List<LikeDTO> likes = List.of(
                new LikeDTO(1L, 1L, 101L),
                new LikeDTO(2L, 1L, 102L)
        );
        return ResponseEntity.ok(likes);
    }

    // 내 팔로우 목록 조회
    @GetMapping("/following")
    public ResponseEntity<List<FollowDTO>> getFollowingList() {
        List<FollowDTO> following = List.of(
                new FollowDTO(1L, 1L, 2L),
                new FollowDTO(2L, 1L, 3L)
        );
        return ResponseEntity.ok(following);
    }

    // 내 팔로워 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<FollowDTO>> getFollowerList() {
        List<FollowDTO> followers = List.of(
                new FollowDTO(1L, 2L, 1L),
                new FollowDTO(2L, 3L, 1L)
        );
        return ResponseEntity.ok(followers);
    }

    // 내가 작성한 다이어리 목록 조회
    @GetMapping("/diaries")
    public ResponseEntity<List<DiaryDTO>> getDiaries() {
        List<DiaryDTO> diaries = List.of(
                new DiaryDTO(1L, 1L, "첫번째 다이어리", "thumbnail1.jpg", "첫번째 다이어리 내용", 37.5665, 126.9780, LocalDateTime.now(), LocalDateTime.now(), "Sunny", "public"),
                new DiaryDTO(2L, 1L, "두번째 다이어리", "thumbnail2.jpg", "두번째 다이어리 내용", 37.5665, 126.9780, LocalDateTime.now(), LocalDateTime.now(), "Cloudy", "private")
        );
        return ResponseEntity.ok(diaries);
    }

    // 내 구독 정보 조회
    @GetMapping("/subscriptions")
    public ResponseEntity<List<OrderDTO>> getSubscriptions() {
        List<OrderDTO> subscriptions = List.of(
                new OrderDTO(1L, 1L, 29.99, OrderStatus.PENDING, LocalDateTime.now()),
                new OrderDTO(2L, 1L, 49.99, OrderStatus.COMPLETED, LocalDateTime.now())
        );
        return ResponseEntity.ok(subscriptions);
    }
}