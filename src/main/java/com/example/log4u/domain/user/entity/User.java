package com.example.log4u.domain.user.entity;

import java.util.Objects;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.common.oauth2.dto.OAuth2Response;
import com.example.log4u.domain.user.dto.UserProfileMakeRequestDto;
import com.example.log4u.domain.user.dto.UserProfileUpdateRequestDto;

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

	public void updateMyProfile(UserProfileUpdateRequestDto userProfileUpdateRequestDto) {
		this.profileImage = userProfileUpdateRequestDto.profileImage();
		this.statusMessage = userProfileUpdateRequestDto.statusMessage();
	}

	public void createMyProfile(UserProfileMakeRequestDto userProfileMakeRequestDto) {
		this.nickname = userProfileMakeRequestDto.nickname();
		this.statusMessage = userProfileMakeRequestDto.statusMessage();
		this.profileImage = userProfileMakeRequestDto.profileImage();
		this.role = "ROLE_USER";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		User user = (User)obj;
		return isPremium() == user.isPremium() && Objects.equals(getName(), user.getName())
			&& Objects.equals(getNickname(), user.getNickname()) && Objects.equals(getEmail(),
			user.getEmail()) && Objects.equals(getProviderId(), user.getProviderId()) && Objects.equals(
			getProfileImage(), user.getProfileImage()) && Objects.equals(getRole(), user.getRole())
			&& getSocialType() == user.getSocialType() && Objects.equals(getStatusMessage(),
			user.getStatusMessage());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getNickname(), getEmail(), getProviderId(), getProfileImage(), getRole(),
			getSocialType(), getStatusMessage(), isPremium());
	}

}
