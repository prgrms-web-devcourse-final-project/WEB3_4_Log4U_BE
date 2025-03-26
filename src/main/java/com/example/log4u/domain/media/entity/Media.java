package com.example.log4u.domain.media.entity;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.media.dto.MediaRequestDto;
import com.example.log4u.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Media extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String originalName;

	private String storedName;

	private String url;

	private String contentType;

	private Long size;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diary_id")
	private Diary diary;

	public static Media toEntity(MediaRequestDto request) {
		return Media.builder()
			.originalName(request.originalName())
			.storedName(request.storedName())
			.url(request.url())
			.contentType(request.contentType())
			.size(request.size())
			.build();
	}

}
