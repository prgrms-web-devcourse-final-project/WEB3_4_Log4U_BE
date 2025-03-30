package com.example.log4u.domain.user.entity;

import com.example.log4u.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private Long providerId;

	@Column(nullable = false)
	private String provider;

	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	private String statusMessage;

	@Column(nullable = false)
	private boolean isPremium;
}
