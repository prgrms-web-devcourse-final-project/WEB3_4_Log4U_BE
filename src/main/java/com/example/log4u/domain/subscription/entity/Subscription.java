package com.example.log4u.domain.subscription.entity;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.domain.subscription.PaymentProvider;
import com.example.log4u.domain.subscription.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)

@Entity
public class Subscription extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentProvider paymentProvider;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false, unique = true)
	private String paymentKey;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
}
