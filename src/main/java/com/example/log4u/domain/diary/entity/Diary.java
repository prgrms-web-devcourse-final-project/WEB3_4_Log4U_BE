package com.example.log4u.domain.diary.entity;

import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.global.entity.BaseTimeEntity;

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

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diary extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	private String title;

	private String content;

	private Double latitude;

	private Double longitude;

	private String weatherInfo;

	@Enumerated(EnumType.STRING)
	private VisibilityType visibility;

	private String thumbnailUrl;

	private int likesCount = 0;

	public static Diary toEntity(Long userId, DiaryRequestDto request, String thumbnailUrl) {
		return Diary.builder()
			.userId(userId)
			.title(request.title())
			.content(request.content())
			.latitude(request.latitude())
			.longitude(request.longitude())
			.weatherInfo(request.weatherInfo())
			.visibility(VisibilityType.valueOf(request.visibility()))
			.thumbnailUrl(thumbnailUrl)
			.build();
	}

	public void update(DiaryRequestDto request, String newThumbnailUrl) {
		this.title = request.title();
		this.content = request.content();
		this.latitude = request.latitude();
		this.longitude = request.longitude();
		this.weatherInfo = request.weatherInfo();
		this.visibility = VisibilityType.valueOf(request.visibility());
		this.thumbnailUrl = newThumbnailUrl;
	}
}
