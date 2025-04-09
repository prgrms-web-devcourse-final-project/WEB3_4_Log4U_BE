package com.example.log4u.domain.user.mypage.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.follow.repository.FollowQuerydsl;
import com.example.log4u.domain.subscription.PaymentStatus;
import com.example.log4u.domain.subscription.dto.SubscriptionResponseDto;
import com.example.log4u.domain.subscription.entity.Subscription;
import com.example.log4u.domain.subscription.repository.SubscriptionRepository;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {
	private final int defaultPageSize = 6;
	private final DiaryService diaryService;
	private final FollowQuerydsl followQuerydsl;
	private final SubscriptionRepository subscriptionRepository;

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getMyDiariesByCursor(Long userId, VisibilityType visibilityType,
		Long cursorId) {
		return diaryService.getMyDiariesByCursor(userId, visibilityType, cursorId,
			defaultPageSize); // 일단 로직 자체가 구현 그대로 돼있길래 그대로 갖다 썼는데 이래도 구조가 괜찮을 지 모르겠습니다.
	}

	@Transactional(readOnly = true)
	public PageResponse<DiaryResponseDto> getLikeDiariesByCursor(Long userId, Long cursorId) {
		return diaryService.getLikeDiariesByCursor(userId, userId,
			cursorId, defaultPageSize);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getMyFollowers(Long userId, Long cursorId) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowerSliceByUserId(
			userId,
			cursorId,
			PageRequest.of(0, defaultPageSize));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}

	@Transactional(readOnly = true)
	public PageResponse<UserThumbnailResponseDto> getMyFollowings(Long userId, Long cursorId) {
		Slice<UserThumbnailResponseDto> slice = followQuerydsl.getFollowingSliceByUserId(
			userId,
			cursorId,
			PageRequest.of(0, defaultPageSize));

		Long nextCursor = !slice.isEmpty() ? slice.getContent().getLast().userId() : null;

		return PageResponse.of(slice, nextCursor);
	}

	@Transactional(readOnly = true)
	public SubscriptionResponseDto getMySubscription(Long userId) {
		Optional<Subscription> optionalSubscription = subscriptionRepository
			.findByUserIdAndCreatedAtBeforeAndPaymentStatusOrderByCreatedAtDesc(
				userId,
				LocalDateTime.now(), PaymentStatus.SUCCESS);
		if (optionalSubscription.isEmpty()) {
			return SubscriptionResponseDto.builder()
				.isSubscriptionActive(false)
				.build();
		} else {
			Subscription subscription = optionalSubscription.get();
			return SubscriptionResponseDto.builder()
				.isSubscriptionActive(true)
				.paymentProvider(subscription.getPaymentProvider())
				.startDate(subscription.getCreatedAt())
				.build();
		}
	}
}
