package com.example.log4u.domain.hashtag.entity;

import com.example.log4u.common.entity.BaseEntity;

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
public class DiaryHashtag extends BaseEntity { // 다이어리 - 해시태그 연결 엔티티

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long diaryHashtagId;

	@Column(nullable = false)
	private Long diaryId;

	@Column(nullable = false)
	private Long hashtagId;
}