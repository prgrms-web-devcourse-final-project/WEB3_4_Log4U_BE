package com.example.log4u.domain.mypage.dto;

public enum OrderStatus {
    PENDING,      // 주문 대기
    COMPLETED,    // 주문 완료
    CANCELLED,    // 주문 취소
    SHIPPED,      // 배송 시작
    DELIVERED;    // 배송 완료
}