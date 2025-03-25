package com.example.log4u.domain.media.entity;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Media extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String originalName;

	private String storedName;

	private String url;

	private String contentType;

	private Long size;

	@ManyToOne
	@JoinColumn(name = "diary_id")
	private Diary diary;
}
