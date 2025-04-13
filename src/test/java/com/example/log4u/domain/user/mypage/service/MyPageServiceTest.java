package com.example.log4u.domain.user.mypage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryResponseDto;
import com.example.log4u.domain.diary.service.DiaryService;
import com.example.log4u.domain.follow.repository.FollowQuerydsl;
import com.example.log4u.domain.subscription.PaymentProvider;
import com.example.log4u.domain.subscription.PaymentStatus;
import com.example.log4u.domain.subscription.dto.SubscriptionResponseDto;
import com.example.log4u.domain.subscription.entity.Subscription;
import com.example.log4u.domain.subscription.repository.SubscriptionRepository;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.fixture.DiaryFixture;

@ExtendWith(MockitoExtension.class)
public class MyPageServiceTest {
	@InjectMocks
	private MyPageService myPageService;

	@Mock
	private DiaryService diaryService;

	@Mock
	private FollowQuerydsl followQuerydsl;

	@Mock
	private SubscriptionRepository subscriptionRepository;

	private final Long userId = 1L;
	private final Long cursorId = 10L;

	private List<DiaryResponseDto> diaries;

	@BeforeEach
	public void setUp() {
		diaries = DiaryFixture.createDiariesFixture()
			.stream()
			.map(diary -> DiaryResponseDto.of(diary, new ArrayList<>()))
			.toList();
	}

	@DisplayName("성공 테스트 : 내 다이어리 조회")
	@Test
	void getMyDiariesByCursor_returnsCorrectData() {
		PageResponse<DiaryResponseDto> mockResponse = PageResponse.of(
			new SliceImpl<>(diaries), null
		);

		when(diaryService.getMyDiariesByCursor(userId, VisibilityType.PUBLIC, cursorId, 6)).thenReturn(mockResponse);

		PageResponse<DiaryResponseDto> result = myPageService.getMyDiariesByCursor(userId, VisibilityType.PUBLIC,
			cursorId);

		assertThat(result).isNotNull();
		verify(diaryService).getMyDiariesByCursor(userId, VisibilityType.PUBLIC, cursorId, 6);
	}

	@DisplayName("성공 테스트 : 좋아요한 다이어리 조회")
	@Test
	void getLikeDiariesByCursor_returnsCorrectData() {
		PageResponse<DiaryResponseDto> mockResponse = PageResponse.of(
			new SliceImpl<>(diaries), null
		);

		when(diaryService.getLikeDiariesByCursor(userId, userId, cursorId, 6)).thenReturn(mockResponse);

		PageResponse<DiaryResponseDto> result = myPageService.getLikeDiariesByCursor(userId, cursorId);

		assertThat(result).isNotNull();
		verify(diaryService).getLikeDiariesByCursor(userId, userId, cursorId, 6);
	}

	@DisplayName("성공 테스트 : 내 팔로워 조회")
	@Test
	void getMyFollowers_returnsCorrectData() {
		var slice = new SliceImpl<>(List.of(new UserThumbnailResponseDto(userId, "nick", "image")));

		when(followQuerydsl.getFollowerSliceByUserId(eq(userId), eq(cursorId), any(), any(PageRequest.class)))
			.thenReturn(slice);

		PageResponse<UserThumbnailResponseDto> result = myPageService.getMyFollowers(userId, cursorId, null);

		assertThat(result).isNotNull();
	}

	@DisplayName("성공 테스트 : 내 팔로잉 조회")
	@Test
	void getMyFollowings_returnsCorrectData() {
		var slice = new SliceImpl<>(List.of(new UserThumbnailResponseDto(userId, "nick", "image")));

		when(followQuerydsl.getFollowingSliceByUserId(eq(userId), eq(cursorId), any(), any(PageRequest.class)))
			.thenReturn(slice);

		PageResponse<UserThumbnailResponseDto> result = myPageService.getMyFollowings(userId, cursorId, null);

		assertThat(result).isNotNull();
	}

	@DisplayName("구독 정보 조회 - 구독이 있을 때")
	@Test
	void getMySubscription_whenExists_returnsActiveSubscription() {
		LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
		Subscription subscription = Subscription.builder()
			.id(1L)
			.userId(userId)
			.createdAt(createdAt)
			.paymentProvider(PaymentProvider.KAKAO)
			.paymentStatus(PaymentStatus.SUCCESS)
			.build();

		when(subscriptionRepository.findByUserIdAndCreatedAtBeforeAndPaymentStatusOrderByCreatedAtDesc(
			eq(userId), any(LocalDateTime.class), eq(PaymentStatus.SUCCESS)))
			.thenReturn(Optional.of(subscription));

		SubscriptionResponseDto result = myPageService.getMySubscription(userId);

		assertThat(result.isSubscriptionActive()).isTrue();
		assertThat(result.startDate()).isEqualTo(createdAt);
		assertThat(result.paymentProvider()).isEqualTo(PaymentProvider.KAKAO);
	}

	@DisplayName("구독 정보 조회 - 구독이 없을 때")
	@Test
	void getMySubscription_whenNotExists_returnsInactive() {
		when(subscriptionRepository.findByUserIdAndCreatedAtBeforeAndPaymentStatusOrderByCreatedAtDesc(
			anyLong(), any(), eq(PaymentStatus.SUCCESS)))
			.thenReturn(Optional.empty());

		SubscriptionResponseDto result = myPageService.getMySubscription(userId);

		assertThat(result.isSubscriptionActive()).isFalse();
	}
}
