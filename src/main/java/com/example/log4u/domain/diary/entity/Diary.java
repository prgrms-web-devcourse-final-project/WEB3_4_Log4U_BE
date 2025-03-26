package com.example.log4u.domain.diary.entity;

import com.example.log4u.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	//JPA 연관관계 사용 X
	// 외래키 방식을 사용 O
	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String title;

	private String thumbnailUrl;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Long likeCount;

	public Long updateLikeCount() {
		this.likeCount++;
		return this.likeCount;
	}
}
