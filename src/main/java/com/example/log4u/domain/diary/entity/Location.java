package com.example.log4u.domain.diary.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

	private Double latitude;

	private Double longitude;

	private String sido;

	private String sigungu;

	private String eupmyeondong;
}
