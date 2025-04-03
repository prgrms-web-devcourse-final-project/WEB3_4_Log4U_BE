package com.example.log4u.domain.subscription.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.subscription.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	List<Subscription> findByUserIdAndEndTimeAfter(Long userId, LocalDateTime now);
}

