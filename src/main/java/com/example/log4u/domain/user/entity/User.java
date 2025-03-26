package com.example.log4u.domain.user.entity;

import java.util.List;

import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	private Long providerId;

	private String provider;

	private String email;

	private String status_message;

	@OneToMany(mappedBy = "user")
	private List<Diary> diaries;
}
