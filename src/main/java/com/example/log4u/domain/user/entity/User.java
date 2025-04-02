package com.example.log4u.domain.user.entity;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.common.oauth2.dto.OAuth2Response;

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
	private String name;

	@Column(nullable = false)
	private String nickname;

	private String email;

	@Column(nullable = false)
	private String providerId;

	private String profileImage;

	@Column(nullable = false)
	private String role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	private String statusMessage;

	@Column(nullable = false)
	@Builder.Default
	private boolean isPremium = false;

	public void updateOauth2Profile(OAuth2Response oAuth2Response) {
		this.email = oAuth2Response.getEmail();
		this.name = oAuth2Response.getName();
		this.nickname = oAuth2Response.getNickname();
		this.profileImage = oAuth2Response.getProfileImage();
	}
}
