package com.example.log4u.domain.mypage.dto;

import java.time.LocalDateTime;

public record OrderDTO(
        Long orderId,        // 주문 ID
        Long userId,         // 주문한 사용자 ID
        Double totalPrice,   // 총 금액
        OrderStatus orderStatus, // 주문 상태
        LocalDateTime orderDate  // 주문 날짜 및 시간
) {}