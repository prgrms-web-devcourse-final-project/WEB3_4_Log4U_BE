package com.example.log4u.domain.subscription.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.subscription.PaymentStatus;
import com.example.log4u.domain.subscription.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	Optional<Subscription> findByUserIdAndCreatedAtBeforeAndPaymentStatusOrderByCreatedAtDesc(Long userId,
		LocalDateTime now,
		PaymentStatus paymentStatus);
}

