package com.example.log4u.domain.media.entity;

import com.example.log4u.common.entity.BaseEntity;
import com.example.log4u.domain.media.MediaStatus;

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
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Media extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long mediaId;

	private Long diaryId; // 임시 상태에서 null 허용

	private String originalName;

	private String storedName;

	private String url;

	private String contentType;

	private Long size;

	@Builder.Default
	@Setter
	private Integer orderIndex = 0; // 순서 필드

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private MediaStatus status = MediaStatus.TEMPORARY;

	public void connectToDiary(Long diaryId) {
		this.diaryId = diaryId;
		this.status = MediaStatus.PERMANENT;
	}

	public void markAsDeleted() {
		this.status = MediaStatus.DELETED;
	}
}
