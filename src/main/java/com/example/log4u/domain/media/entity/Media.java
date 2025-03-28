package com.example.log4u.domain.media.entity;

import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.global.entity.BaseTimeEntity;

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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Media extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long diaryId;

	private String originalName;

	private String storedName;

	private String url;

	private String contentType;

	private Long size;

	public static Media toEntity(Long diaryId, MediaRequestDto request) {
		return Media.builder()
			.diaryId(diaryId)
			.originalName(request.originalName())
			.storedName(request.storedName())
			.url(request.url())
			.contentType(request.contentType())
			.size(request.size())
			.build();
	}

}
