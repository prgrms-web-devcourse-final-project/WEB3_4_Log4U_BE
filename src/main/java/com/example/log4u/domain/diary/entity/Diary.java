package com.example.log4u.domain.diary.entity;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.map.dto.LocationDto;
import com.example.log4u.domain.map.entitiy.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long diaryId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String title;

	private String thumbnailUrl;

	@Column(nullable = false)
	private String content;

	@Embedded
	private Location location;

	@Enumerated(EnumType.STRING)
	private WeatherInfo weatherInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VisibilityType visibility;

	@Column(nullable = false)
	@Builder.Default
	private Long likeCount = 0L;

	public void update(DiaryRequestDto request, String newThumbnailUrl) {
		this.title = request.title();
		this.content = request.content();
		this.location = LocationDto.toEntity(request.location());
		this.weatherInfo = request.weatherInfo();
		this.visibility = request.visibility();
		this.thumbnailUrl = newThumbnailUrl;
	}

	public Long incrementLikeCount() {
		this.likeCount++;
		return this.likeCount;
	}

	public Long decreaseLikeCount() {
		this.likeCount--;
		return this.likeCount;
	}

	public boolean isOwner(Long userId) {
		return this.userId.equals(userId);
	}
}
