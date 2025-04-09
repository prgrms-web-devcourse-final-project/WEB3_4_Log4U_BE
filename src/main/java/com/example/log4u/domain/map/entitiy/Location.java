package com.example.log4u.domain.map.entitiy;

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

	private Double latitude; // 위도

	private Double longitude; // 경도

	private String sido; // 시/도

	private String sigungu; // 시/군/구

	private String eupmyeondong; // 읍/면/동
}
